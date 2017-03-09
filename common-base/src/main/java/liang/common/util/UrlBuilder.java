/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.common.util;

import liang.common.http.SignUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author
 */
public class UrlBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(UrlBuilder.class);
    private List<Pair> params = new ArrayList<>();
    private List<Pair> originParams = new ArrayList<>();
    private String host;
    private String path;
    private String protocol;
    private int port;
    private String encoding;

    public static void main(String[] args) throws MalformedURLException {
        String url = "http://www.baidu.com/query?fuck=shit";
        UrlBuilder b = new UrlBuilder(url);
        b.setQuery("test", "hello world hahaha");
        b.setQuery("name", "北京");
        b.setQuery("fuck", "newFuck");
        String sign2 = SignUtils.signUrl(b.getUri(), "fuck");
        System.out.println(sign2);
        System.out.println(SignUtils.isValidSign("http://www.baidu.com/query?fuck=newFuck&test=hello+world+hahaha&name=%E5%8C%97%E4%BA%AC&sign=AA66E06FB4ECB355947D4D3342D434A5", "fuck"));
        System.out.println(SignUtils.isValidSign("http://baidu.com/adbasdf?a=%E5%3", "abcd"));
    }

    public UrlBuilder(String url, String encoding) {
        this.encoding = encoding;
        int index = url.indexOf("?");
        //说明以前就有参数，则解析之，并去除之
        if (index != -1) {
            if (index < url.length() - 1) {
                String parameter = url.substring(index + 1);
                String[] kvs = StringUtils.split(parameter, "&");
                if (kvs != null) {
                    for (String kv : kvs) {
                        String[] pair = kv.split("\\=", 2);
                        if (pair.length == 2) {
                            params.add(new Pair(pair[0], urlDecode(pair[1], encoding)));
                            originParams.add(new Pair(pair[0], pair[1]));
                        }
                    }
                }
            }
            host = url.substring(0, index);
        } else {
            host = url;
        }
        try {
            URI u = new URI(host);
            this.host = u.getHost();
            this.path = u.getPath();
            this.protocol = u.getScheme();
            this.port = u.getPort();
        } catch (Exception ex) {
            LOG.error(null, ex);
        }
    }

    public UrlBuilder(String url) {
        this(url, "UTF-8");
    }

    /**
     * 把指定对象的属性序列化到参数中
     * 必须保证此对象是一级的，并且都是toString方法安全的
     *
     * @param obj
     * @param ignoreFields
     */
    public void setParams(Object obj, String... ignoreFields) {
        if (obj instanceof Map) {
            Map<?, ?> map = (Map) obj;
            for (Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
                if (ArrayUtils.contains(ignoreFields, key)) {
                    continue;
                }
                setQuery(key, value);
            }
        } else {
            Class<?> objClazz = obj.getClass();
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(objClazz);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                try {
                    Method readMethod = propertyDescriptor.getReadMethod();
                    if (readMethod == null) {
                        continue;
                    }
                    String name = propertyDescriptor.getName();
                    if (ArrayUtils.contains(ignoreFields, name)) {
                        continue;
                    }
                    try {
                        Object value = readMethod.invoke(obj);
                        //只序列化基本数据类型和字符串还有枚举
                        if (value != null && (Number.class.isAssignableFrom(value.getClass())
                                || value.getClass() == Boolean.class
                                || value.getClass().isPrimitive()
                                || value.getClass() == String.class
                                || value.getClass().isEnum())) {
                            setQuery(name, String.valueOf(value));
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        LOG.error(readMethod.getName(), e);
                    }
                } catch (Exception ex) {
                    LOG.error(null, ex);
                }
            }
        }
    }

    public String getParam(String key) {
        StringBuilder sb = new StringBuilder();
        for (Pair pair : params) {
            if (pair.key.equals(key)) {
                if (sb.length() != 0) {
                    sb.append(",");
                }
                sb.append(pair.value);
            }
        }
        return sb.toString();
    }

    public List<Pair> getParams() {
        return params;
    }

    public void setQuery(String key, String value) {
        {
            boolean find = false;
            for (Pair pair : params) {
                if (pair.key.equals(key)) {
                    pair.value = value;
                    find = true;
                }
            }
            if (find == false) {
                params.add(new Pair(key, value));
            }
        }
        {
            boolean find = false;
            for (Pair pair : originParams) {
                if (pair.key.equals(key)) {
                    pair.value = value;
                    find = true;
                }
            }
            if (find == false) {
                originParams.add(new Pair(key, value));
            }
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void removeParam(String param) {
        removeParam(param, params);
        removeParam(param, originParams);
    }

    public String getUriWithRawQuery() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(protocol)) {
            sb.append(protocol).append("://");
        }
        if (StringUtils.isNotBlank(host)) {
            sb.append(host);
        }
        if (this.port > 0 && this.port < 65536) {
            sb.append(":").append(port);
        }
        if (StringUtils.isNotBlank(path)) {
            sb.append(path);
        }
        String queryString = getQueryString(originParams, false);
        if (StringUtils.isNotBlank(queryString)) {
            sb.append("?").append(queryString);
        }
        return sb.toString();
    }

    public String getUri() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(protocol)) {
            sb.append(protocol).append("://");
        }
        if (StringUtils.isNotBlank(host)) {
            sb.append(host);
        }
        if (this.port > 0 && this.port < 65536) {
            sb.append(":").append(port);
        }
        if (StringUtils.isNotBlank(path)) {
            sb.append(path);
        }
        String queryString = getQueryString(params, true);
        if (StringUtils.isNotBlank(queryString)) {
            sb.append("?").append(queryString);
        }
        return sb.toString();
    }

    private void removeParam(String key, List<Pair> list) {
        for (int i = 0; i < list.size(); i++) {
            Pair pair = list.get(i);
            if (pair.key.equals(key)) {
                list.remove(pair);
            }
        }
    }

    /**
     * 返回签名的时候，需要参与签名的部分
     *
     * @return
     */
    public String getSignPart() {
        List<Pair> list = new ArrayList<>(originParams);
        removeParam("sign", list);
        //把callback也去掉
        removeParam("callback", list);
        String query = getQueryString(list, false);
        if (StringUtils.isNotBlank(query)) {
            return path + "?" + query;
        } else {
            return path;
        }
    }

    private String getQueryString(List<Pair> params, boolean needEncode) {
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(params)) {
            boolean first = true;
            for (Pair pair : params) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }
                sb.append(pair.key).append("=");
                if (needEncode) {
                    String encode = urlEncode(pair.value, encoding);
                    sb.append(encode);
                } else {
                    sb.append(pair.value);
                }
            }
        }
        return sb.toString();
    }

    private String urlDecode(String s, String encoding) {
        try {
            try {
                return URLDecoder.decode(s, encoding);
            } catch (Exception e) {
                return urlDecode(s, Charset.forName(encoding), true);
            }
        } catch (Exception ex) {
            LOG.warn(null, ex);
        }
        return s;
    }

    private static String urlDecode(
            final String content,
            final Charset charset,
            final boolean plusAsBlank) {
        if (content == null) {
            return null;
        }
        final ByteBuffer bb = ByteBuffer.allocate(content.length());
        final CharBuffer cb = CharBuffer.wrap(content);
        while (cb.hasRemaining()) {
            final char c = cb.get();
            if (c == '%' && cb.remaining() >= 2) {
                final char uc = cb.get();
                final char lc = cb.get();
                final int u = Character.digit(uc, 16);
                final int l = Character.digit(lc, 16);
                if (u != -1 && l != -1) {
                    bb.put((byte) ((u << 4) + l));
                } else {
                    bb.put((byte) '%');
                    bb.put((byte) uc);
                    bb.put((byte) lc);
                }
            } else if (plusAsBlank && c == '+') {
                bb.put((byte) ' ');
            } else {
                bb.put((byte) c);
            }
        }
        bb.flip();
        return charset.decode(bb).toString();
    }

    private String urlEncode(String s, String encoding) {
        if (StringUtils.isBlank(s)) {
            return "";
        }
        try {
            return URLEncoder.encode(s, encoding);
        } catch (Exception ex) {
            LOG.warn(null, ex);
        }
        return s;
    }

    public static class Pair {
        private final String key;
        private String value;

        public Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
