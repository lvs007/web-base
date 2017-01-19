package liang.mvc.commons;

/**
 * Created by mc-050 on 2017/1/17 16:51.
 * KIVEN will tell you life,send email to xxx@163.com
 */


import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BeanUtils {
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object newInstance(String className) {
        try {
            return newInstance(Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getPropertyType(Object bean, String field) {
        try {
            return PropertyUtils.getPropertyType(bean, field);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Object getPropertyValue(Object orig, String field) {
        try {
            return PropertyUtils.getSimpleProperty(orig, field);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void setProperty(Object bean, String name, Object value) {
        try {
            PropertyUtils.setProperty(bean, name, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void setSimpleProperty(Object bean, String name, Object value) {
        try {
            PropertyUtils.setSimpleProperty(bean, name, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void copyProperties(Object dest, Object orig) {
        try {
            if (orig != null) {
                PropertyUtils.copyProperties(dest, orig);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void copyNotNullProperties(Object dest, Object orig) {
        try {
            if (orig != null) {
                OrigNotNullUtilsBean beanUtilsBean = new OrigNotNullUtilsBean();
                beanUtilsBean.copyProperties(dest, orig);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void fillNullProperties(Object dest, Object orig) {
        try {
            if (orig != null) {
                DestNullUtilsBean beanUtilsBean = new DestNullUtilsBean();
                beanUtilsBean.copyProperties(dest, orig);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static <T> T copyProperties(Object orig, Class<T> destClazz) {
        T dest = newInstance(destClazz);
        copyProperties(dest, orig);
        return dest;
    }

    public static <T> T copyNotNullProperties(Object orig, Class<T> destClazz) {
        T dest = newInstance(destClazz);
        copyNotNullProperties(dest, orig);
        return dest;
    }

    public static <T> T fillNullProperties(Object orig, Class<T> destClazz) {
        T dest = newInstance(destClazz);
        fillNullProperties(dest, orig);
        return dest;
    }

    public static void copyProperties(Object dest, Object orig, String getField, String setField) {
        try {
            if (orig != null) {
                Object origValue = PropertyUtils.getSimpleProperty(orig, getField);

                PropertyUtils.setSimpleProperty(dest, setField, origValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static <T> T copyProperties(Object orig, Class<T> destClazz, String getField, String setField) {
        T dest = newInstance(destClazz);
        copyProperties(dest, orig, getField, setField);
        return dest;
    }

    public static void copyProperties(Object dest, Map<String, ?> orig) {
        try {
            org.apache.commons.beanutils.BeanUtils.populate(dest, orig);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static <T> T copyProperties(Map<?, ?> orig, Class<T> destClazz) {
        T dest = newInstance(destClazz);
        copyProperties(dest, orig);
        return dest;
    }

    public static Map<String, Object> copyProperties(Object orig) {
        try {
            return PropertyUtils.describe(orig);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, Object>> copyProperties(List<?> origs) {
        if (origs == null) {
            return null;
        }
        List<Map<String, Object>> resultList = new ArrayList();
        for (Object orig : origs) {
            resultList.add(copyProperties(orig));
        }
        return resultList;
    }

    public static <T1, T2> List<T2> copyList(List<T1> origList, Class<T2> destClazz) {
        if (origList == null) {
            return null;
        }
        List<T2> resultList = new ArrayList();
        for (T1 orig : origList) {
            resultList.add(copyProperties(orig, destClazz));
        }
        return resultList;
    }

    public static <T1, T2> List<T2> copyList(List<T1> origList, Class<T2> destClazz, String getField, String setField) {
        if (origList == null) {
            return null;
        }
        List<T2> resultList = new ArrayList();
        for (T1 orig : origList) {
            resultList.add(copyProperties(orig, destClazz, getField, setField));
        }
        return resultList;
    }
}


