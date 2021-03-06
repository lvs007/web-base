package com.liang.dao.hibernate;

import com.liang.utils.ExceptionHandler;
import org.apache.commons.beanutils.ConvertUtils;
import org.hibernate.transform.BasicTransformerAdapter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link org.hibernate.transform.ResultTransformer}的接口实现,支持SQLQuery查询字段别名到实体对象属性的赋值<br/>
 * 区别于{@link org.hibernate.transform.AliasToBeanResultTransformer},这里查询字段别名不区分大小写.<br/>
 * 当存在多个setter方法时,会选择最优匹配方法,当参数与方法签名不一致,会尽可能尝试类型转换.<br/>
 * 该类是线程安全的,调用{@link BeanResultTransFormer#get(Class)}方法可以获取针对每个类型的全局唯一实例.
 *
 * @author skyfalling
 */
public class BeanResultTransFormer<T> extends BasicTransformerAdapter implements Serializable {
    /**
     * 已注册实例
     */
    private final static Map<Class, BeanResultTransFormer> instanceRegistered = new ConcurrentHashMap<Class, BeanResultTransFormer>();

    /**
     * 属性名到setter方法映射
     */
    private Map<String, List<Setter>> setters = new HashMap<String, List<Setter>>();
    /**
     * 实体类型
     */
    private Class<T> beanClass;

    /**
     * 构造方法
     *
     * @param beanClass 实体类型
     */
    protected BeanResultTransFormer(Class beanClass) {
        this.register(this.beanClass = beanClass);
    }

    /**
     * 注册实体类型
     *
     * @param beanClass
     */
    protected void register(Class beanClass) {
        Method[] methods = beanClass.getMethods();
        //根据setter方法机获取属性名称
        for (Method method : methods) {
            if (filter(method)) {
                String name = method.getName().toLowerCase().substring(3);
                if (!setters.containsKey(name)) {
                    setters.put(name, new ArrayList<Setter>());
                }
                //同名的方法列表,不分大小写
                setters.get(name).add(new Setter(method));
            }
        }
    }

    /**
     * 选择Setter方法
     *
     * @param method
     * @return
     */
    boolean filter(Method method) {
        if (method.getReturnType() == Void.TYPE && method.getParameterTypes().length == 1) {
            String methodName = method.getName();
            return methodName.startsWith("set") && methodName.length() > 3;
        }
        return false;
    }

    /**
     * 或者指定类型的转换对象
     *
     * @param beanClass
     * @return
     */
    public static BeanResultTransFormer get(Class beanClass) {
        synchronized (beanClass) {
            if (!instanceRegistered.containsKey(beanClass)) {
                instanceRegistered.put(beanClass, new BeanResultTransFormer(beanClass));
            }
        }
        return instanceRegistered.get(beanClass);
    }


    /**
     * 字段转换
     * {@inheritDoc}
     */
    public Object transformTuple(Object[] tuple, String[] aliases) {
        try {
            T data = beanClass.newInstance();
            for (int i = 0; i < tuple.length; i++) {
                String name = aliases[i];
                Object value = tuple[i];
                handle(data, name, value);
            }
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 尽可能地调用最匹配的Setter方法进行赋值
     *
     * @param data
     * @param name
     * @param value
     * @throws Exception
     */
    protected void handle(T data, String name, Object value) throws Exception {
        //忽略空值
        if (value == null)
            return;
        List<Setter> setterList = setters.get(name.toLowerCase());
        if (setterList == null || setterList.isEmpty())
            return;
        //唯一setter方法
        if (setterList.size() == 1) {
            Setter setter = setterList.get(0);
            if (value != null && !setter.type.isInstance(value)) {
                value = ConvertUtils.convert(value, setter.type);
            }
            setter.invoke(data, value);
            return;
        }
        //参数兼容的方法列表
        List<Setter> compatibleList = new ArrayList<Setter>();
        //数字参数的方法列表
        List<Setter> numericList = new ArrayList<Setter>();
        for (Setter setter : setterList) {
            Class type = setter.type;
            //参数类型一致
            if (value != null && type == value.getClass()) {
                setter.invoke(data, value);
            }
            //参数类型兼容
            if (type.isInstance(value)) {
                compatibleList.add(setter);
            } else if (Number.class.isAssignableFrom(type)
                    && value instanceof Number) {//数值型参数
                numericList.add(setter);
            }
        }
        if (compatibleList.size() == 1) {
            compatibleList.get(0).invoke(data, value);
            return;
        }
        if (compatibleList.isEmpty() && numericList.size() == 1) {
            value = ConvertUtils.convert(value, numericList.get(0).type);
            numericList.get(0).invoke(data, value);
            return;
        }
        if (compatibleList.size() > 0 || numericList.size() > 0) {
            throw new Exception("ambiguous setter methods to call");
        }
    }


    class Setter {
        Method method;
        Class type;

        Setter(Method method) {
            this.method = method;
            this.type = wrapClass(method.getParameterTypes()[0]);
        }

        void invoke(Object object, Object arg) {
            try {
                if (type.isInstance(arg)) {
                    method.invoke(object, arg);
                }
            } catch (Exception e) {
                ExceptionHandler.throwException(e);
            }
        }

        Class wrapClass(Class clazz) {
            if (clazz.equals(Boolean.TYPE)) {
                return Boolean.class;
            }
            if (clazz.equals(Byte.TYPE)) {
                return Byte.class;
            }
            if (clazz.equals(Short.TYPE)) {
                return Short.class;
            }
            if (clazz.equals(Integer.TYPE)) {
                return Integer.class;
            }
            if (clazz.equals(Long.TYPE)) {
                return Long.class;
            }
            if (clazz.equals(Float.TYPE)) {
                return Float.class;
            }
            if (clazz.equals(Double.TYPE)) {
                return Double.class;
            }
            if (clazz.equals(Character.TYPE)) {
                return Character.class;
            }
            return clazz;
        }

    }

}
