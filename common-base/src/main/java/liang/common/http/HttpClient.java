package liang.common.http;

import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.*;
import com.squareup.okhttp.internal.http.StatusLine;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * 内部封装的基于OkHttp实现的HttpClient，此对象可以缓存，共享读取超时和连接超时。
 * <p>
 * 如果没有特殊设置要求,如各种超时设置,可以使用DEFAULT代表的公用客户端;否则,请用Builder为场景创建单独的客户端。
 * <p>
 */
public class HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String UTF_8 = "UTF-8";
    private static final String REFERER = "Referer";

    private static final HttpClient DEFAULT = new Builder().build();

    private OkHttpClient httpClient;
    private String userAgent;

    public static class Builder {
        private long connectTimeoutInMs = 10_000;
        private long readTimeoutInMs = 10_000;
        private String userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 3.5.21022;)";
        private boolean followRedirects = false;

        public Builder() {
        }

        public Builder setConnectTimeoutInMs(long connectTimeoutInMs) {
            this.connectTimeoutInMs = connectTimeoutInMs;
            return this;
        }

        public Builder setReadTimeoutInMs(long readTimeoutInMs) {
            this.readTimeoutInMs = readTimeoutInMs;
            return this;
        }

        public Builder setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder setFollowRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public HttpClient build() {
            HttpClient client = new HttpClient();
            client.setConnectTimeout(connectTimeoutInMs);
            client.setReadTimeout(readTimeoutInMs);
            client.setUserAgent(userAgent);
            client.setFollowRedirects(followRedirects);
            return client;
        }
    }

    private HttpClient() {
        httpClient = new OkHttpClient();
        //添加处理307重定向的功能
        httpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                //在这里处理307重定向，并且只需要处理POST的情况，其他情况已经自动重定向了
                if (response.code() == StatusLine.HTTP_TEMP_REDIRECT && chain.request().method()
                        .equalsIgnoreCase("POST")) {
                    //取到新的地址
                    String location = response.header("Location");
                    if (location == null) {
                        return null;
                    }
                    URL url;
                    if (location.startsWith("http://") || location.startsWith("https://")) {
                        url = new URL(location);
                    } else {
                        url = new URL(chain.request().url(), location);
                    }
                    // Don't follow redirects to unsupported protocols.
                    if (!url.getProtocol().equals("https") && !url.getProtocol().equals("http")) {
                        return null;
                    }

                    // If configured, don't follow redirects between SSL and non-SSL.
                    boolean sameProtocol = url.getProtocol().equals(chain.request().url().getProtocol());
                    if (!sameProtocol && !httpClient.getFollowSslRedirects()) {
                        return null;
                    }

                    // Redirects don't include a request body.
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    Request newRequest = requestBuilder.url(url).build();
                    //重新克隆一个httpClient出来，并且去掉Interceptor
                    OkHttpClient clone = httpClient.clone();
                    clone.networkInterceptors().clear();
                    Response rsp = clone.newCall(newRequest).execute();
                    //去掉networkResponse，否则就会抛异常
                    return rsp.newBuilder().networkResponse(null).build();
                }
                return response;
            }
        });
    }

    public byte[] httpGetBytes(String url) throws IOException {
        Request.Builder builder = prepareRequestBuilder();
        builder.url(url);
        try {
            return toBytes(builder);
        } catch (Exception ex) {
            throw new IOException("网络连接失败", ex);
        }
    }

    public InputStream httpGetStream(String url) throws IOException {
        byte[] data = httpGetBytes(url);
        if (data == null || data.length == 0) {
            return null;
        }
        return new ByteArrayInputStream(data);
    }

    /**
     * httpGet返回原始的流，不会读进内存变成byte[]再变成流，而是直接把流返回了，这个方法
     * 适合读取一些比较大的文件
     */
    public InputStream httpGetRawStream(String url) throws IOException {
        Request.Builder builder = prepareRequestBuilder();
        //这里需要把这个头去掉，全部交给OkHttp去处理
        builder.removeHeader("Accept-Encoding");
        builder.removeHeader("Content-Encoding");
        builder.url(url);
        Response response = httpClient.newCall(builder.build()).execute();
        return response.body().byteStream();
    }

    public String httpGet(String url, String referer, String responseEncoding) throws IOException {
        Request.Builder builder = prepareRequestBuilder();
        builder.url(url);
        if (StringUtils.isNotBlank(referer)) {
            builder.header(REFERER, referer);
        }
        if (StringUtils.isBlank(responseEncoding)) {
            responseEncoding = UTF_8;
        }
        try {
            String content = toString(builder, responseEncoding);
            LOG.debug("httpGet,url=" + url + " ,content=" + content);
            return content;
        } catch (Exception ex) {
            throw new IOException("网络连接失败", ex);
        }
    }

    public String httpGet(String url) throws IOException {
        return httpGet(url, null);
    }

    public String httpGet(String url, List<NameValuePair> list) throws IOException {
        try {
            byte[] responseByte = decodeToBytes(httpGetResponse(url, list));
            if (responseByte == null) {
                return null;
            }
            return new String(responseByte, UTF_8);
        } catch (Exception ex) {
            throw new IOException("网络连接失败", ex);
        }
    }

    /**
     * 如果需要更多的应答内容,请扩充此类。
     */
    public static class LResponse {
        private final byte[] body;
        private final Map<String, List<String>> headers;
        private final int code;

        public LResponse(byte[] body, Map<String, List<String>> headers, int code) {
            this.body = body;
            Map<String, List<String>> temp = new HashMap<>();
            for (String key : headers.keySet()) {
                temp.put(key.toLowerCase(), headers.get(key));
            }
            this.headers = ImmutableMap.copyOf(temp);
            this.code = code;
        }

        public byte[] getBodyBytes() {
            return body;
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        public int getCode() {
            return code;
        }

        public String getBodyString() {
            try {
                return new String(body, UTF_8);
            } catch (Exception e) {
                return null;
            }
        }

        public String getHeader(String key) {
            try {
                return headers.get(key.toLowerCase()).get(0);
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * 返回更丰富的http应答内容。
     */
    public LResponse httpGetLResponse(String url, List<NameValuePair> list) throws Exception {
        Response response = httpGetResponse(url, list);
        return new LResponse(decodeToBytes(response), response.headers().toMultimap(), response.code());
    }

    /**
     * 封装后不应该暴露OkHttp的Response类出来,故淘汰。请使用{@link #httpGetLResponse(String, List)}
     */
    private Response httpGetResponse(String url, List<NameValuePair> list) throws IOException {
        Request.Builder builder = prepareRequestBuilder();
        if (CollectionUtils.isNotEmpty(list)) {
            HttpUrl httpUrl = HttpUrl.parse(url);
            HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
            for (NameValuePair pair : list) {
                urlBuilder.addQueryParameter(pair.getName(), trimToEmpty(pair.getValue()));
            }
            httpUrl = urlBuilder.build();
            builder.url(httpUrl);
        } else {
            builder.url(url);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("httpGet,url=" + builder.build().urlString());
        }
        return httpClient.newCall(builder.build()).execute();
    }

    private static String trimToEmpty(String s) {
        if (StringUtils.isBlank(s)) {
            return "";
        }
        return s;
    }

    public String httpPost(String url, List<NameValuePair> list) throws IOException {
        Request.Builder builder = prepareRequestBuilder();
        builder.url(url);
        FormEncodingBuilder encodingBuilder = new FormEncodingBuilder();
        for (NameValuePair pair : list) {
            encodingBuilder.add(pair.getName(), trimToEmpty(pair.getValue()));
        }
        builder.post(encodingBuilder.build());
        try {
            String content = toString(builder);
            LOG.debug("httpPost,url=" + url + " ,content=" + content);
            return content;
        } catch (Exception ex) {
            throw new IOException("网络连接失败", ex);
        }
    }

    /**
     * 包装后不应该对外暴露使用Request.Builder的接口,故淘汰。
     */
    @Deprecated
    public String httpPost(Request.Builder builder) throws IOException {
        return toString(builder);
    }

    public String httpPost(String url, String content) throws IOException {
        return httpPost(url, content, null, null, null);
    }

    public String httpPost(String url, String content, String referer, String requestEncoding, String responseEncoding)
            throws IOException {
        try {
            Request.Builder builder = prepareRequestBuilder();
            builder.url(url);
            if (StringUtils.isNoneBlank(referer)) {
                builder.header(REFERER, referer);
            }
            if (StringUtils.isBlank(requestEncoding)) {
                requestEncoding = UTF_8;
            }
            if (StringUtils.isBlank(responseEncoding)) {
                responseEncoding = UTF_8;
            }
            builder.post(RequestBody
                    .create(MediaType.parse(APPLICATION_X_WWW_FORM_URLENCODED), content.getBytes(requestEncoding)));
            String s = toString(builder, responseEncoding);
            LOG.debug("httpPost,url=" + url + " ,content=" + content);
            return s;
        } catch (Exception ioe) {
            throw new IOException("网络连接失败", ioe);
        }
    }

    public String httpPostBody(String url, String body, String contentType) throws IOException {
        return httpPostBody(url, body.getBytes(UTF_8), contentType);
    }

    public String httpPostBody(String url, byte[] body) throws IOException {
        return httpPostBody(url, body, "application/octet-stream");
    }

    public String httpPostBody(String url, byte[] body, String contentType) throws IOException {
        Request.Builder builder = prepareRequestBuilder();
        builder.url(url);
        builder.post(RequestBody.create(MediaType.parse(contentType), body));
        try {
            return toString(builder);
        } catch (Exception e) {
            throw new IOException("网络连接失败", e);
        }
    }

    private String toString(Request.Builder builder) throws IOException {
        return toString(builder, UTF_8);
    }

    private String toString(Request.Builder builder, String encoding) throws IOException {
        byte[] bytes = toBytes(builder);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, encoding);
    }

    private byte[] toBytes(Request.Builder builder) throws IOException {
        Response response = httpClient.newCall(builder.build()).execute();
        try {
            return decodeToBytes(response);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private Request.Builder prepareRequestBuilder() {
        Request.Builder builder = new Request.Builder();
        addDefaultHeader(builder);
        return builder;
    }

    private void addDefaultHeader(Request.Builder builder) {
        builder.header("User-Agent", userAgent);
        builder.header("User-Platform", "Java");
        builder.header("Accept-Encoding", "gzip");
    }

    private void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    private void setConnectTimeout(long timeout) {
        httpClient.setConnectTimeout(timeout, TimeUnit.MILLISECONDS);
    }

    private void setReadTimeout(long timeout) {
        httpClient.setReadTimeout(timeout, TimeUnit.MILLISECONDS);
    }

    private void setFollowRedirects(boolean redirect) {
        httpClient.setFollowRedirects(redirect);
    }

    public static HttpClient getDefault() {
        return DEFAULT;
    }

    private static byte[] md5(String source) {
        try {
            return MessageDigest.getInstance("MD5").digest((source).getBytes("UTF-8"));
        } catch (Exception ignored) {
        }
        return null;
    }

    private static byte[] sha1(String source) {
        try {
            return MessageDigest.getInstance("SHA-1").digest((source).getBytes("UTF-8"));
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Cipher createCipher(String token) throws Exception {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        byte[] data1 = md5(token);
        if (data1 == null) {
            return null;
        }
        byte[] data2 = sha1(token);
        if (data2 == null) {
            return null;
        }
        byte[] keyData = new byte[24];
        System.arraycopy(data1, 0, keyData, 0, 16);
        System.arraycopy(data2, 0, keyData, 16, 8);
        DESedeKeySpec dks = new DESedeKeySpec(keyData);

        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey key = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");

        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher;
    }

    private static byte[] decompress(byte[] data) throws Exception {
        ByteArrayOutputStream bout = null;
        BufferedInputStream bin = null;
        try {
            GZIPInputStream zip = new GZIPInputStream(new ByteArrayInputStream(data));
            bin = new BufferedInputStream(zip);
            bout = new ByteArrayOutputStream();
            IOUtils.copy(bin, bout);
            return bout.toByteArray();
        } finally {
            IOUtils.closeQuietly(bout);
            IOUtils.closeQuietly(bin);
        }
    }

    private static byte[] decryptData(byte[] data, String token) throws Exception {
        Cipher cipher = createCipher(token);
        if (cipher != null && data.length % 8 == 0) {
            // 正式执行解密操作
            return cipher.doFinal(data);
        } else {
            return data;
        }

    }

    private static byte[] decodeToBytes(Response rsp) throws Exception {

        byte[] rawData = rsp.body().bytes();

        String contentType = rsp.header("Content-Type");
        String contentEncoding = rsp.header("Content-Encoding");
        String token = rsp.header("X-Simple-Token");

        if (rawData == null || rawData.length == 0) {
            return null;
        }
        if (StringUtils.isBlank(contentType)) {
            contentType = "text/plain";
        }
        if (StringUtils.isBlank(contentEncoding)) {
            contentEncoding = "default";
        }
        if (contentType.contains("application/gzip-enc-stream")) {
            // 先解密再解压缩
            rawData = decryptData(rawData, token);
            rawData = decompress(rawData);
        } else if (contentType.contains("application/enc-stream")) {
            // 只需要解密
            rawData = decryptData(rawData, token);
        } else if (contentEncoding.contains("gzip")) {
            // 只需要加解压缩
            rawData = decompress(rawData);
        } else {
            // 明文，不需要变
        }
        return rawData;
    }
}
