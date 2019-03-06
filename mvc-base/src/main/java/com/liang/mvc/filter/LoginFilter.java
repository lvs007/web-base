package com.liang.mvc.filter;

import com.liang.common.util.PropertiesManager;
import com.liang.mvc.commons.SpringContextHolder;
import com.liang.mvc.constants.MvcConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by liangzhiyan on 2017/3/8.
 */
public class LoginFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(LoginFilter.class);

    private static final String DEFAULT_CHARSET = "UTF8";

    private static final String TOKEN = MvcConstants.TOKEN;

    public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";
    public static final String INCLUDE_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.include.context_path";

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    private PropertiesManager propertiesManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        propertiesManager = SpringContextHolder.getBean("propertiesManager");
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            request.setCharacterEncoding(DEFAULT_CHARSET);
            response.setCharacterEncoding(DEFAULT_CHARSET);
            doFilterInternal((HttpServletRequest) request, (HttpServletResponse) response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }


    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = getPathWithinApplication(request);
        String method = request.getMethod();

        //放过OPTIONS方法的请求。
        if (StringUtils.equalsIgnoreCase("OPTIONS", method)) {
            chain.doFilter(request, response);
            return;
        }

        if (pathMatcher.match("/login/**", path)) {
            String token = request.getParameter(TOKEN);
            if (StringUtils.isBlank(token)) {
                HttpSession session = request.getSession(false);
                token = session == null ? null : (String) session.getAttribute(TOKEN);
            }
            if (StringUtils.isBlank(token)) {
                token = request.getHeader(TOKEN);
            }
            //验证token
            if (StringUtils.isBlank(token) || LoginUtils.getUser(token) == null) {//重定向到登陆页
                response.sendRedirect(propertiesManager.getString("account.login.url","http://localhost:9091/v1/login/login"));
                return;
            }
        }

        chain.doFilter(request, response);
    }

    protected void addCrossDomainHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (StringUtils.isNotBlank(origin)) {
            String accessControlRequestHeaders = request.getHeader("access-control-request-headers");
            String accessControlRequestMethod = request.getHeader("access-control-request-method");

            response.setHeader("Access-Control-Allow-Origin", origin);
            response.addHeader("Access-Control-Allow-Headers", accessControlRequestHeaders);
            response.addHeader("Access-Control-Allow-Methods", accessControlRequestMethod);
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "600");
        }
    }

    private static String getPathWithinApplication(HttpServletRequest request) {
        String contextPath = getContextPath(request);
        String requestUri = getRequestUri(request);
        if (StringUtils.startsWithIgnoreCase(requestUri, contextPath)) {
            // Normal case: URI contains context path.
            String path = requestUri.substring(contextPath.length());
            return (StringUtils.isNotBlank(path) ? path : "/");
        } else {
            // Special case: rather unusual.
            return requestUri;
        }
    }

    public static String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE);
        if (uri == null) {
            uri = request.getRequestURI();
        }
        return normalize(decodeAndCleanUriString(request, uri));
    }

    private static String decodeAndCleanUriString(HttpServletRequest request, String uri) {
        uri = decodeRequestString(request, uri);
        int semicolonIndex = uri.indexOf(';');
        return (semicolonIndex != -1 ? uri.substring(0, semicolonIndex) : uri);
    }

    /**
     * Normalize a relative URI path that may have relative values ("/./",
     * "/../", and so on ) it it. <strong>WARNING</strong> - This method is
     * useful only for normalizing application-generated paths. It does not
     * try to perform security checks for malicious input.
     * Normalize operations were was happily taken from
     * org.apache.catalina.util.RequestUtil in
     * Tomcat trunk, r939305
     *
     * @param path Relative path to be normalized
     * @return normalized path
     */
    public static String normalize(String path) {
        return normalize(path, true);
    }

    /**
     * Normalize a relative URI path that may have relative values ("/./",
     * "/../", and so on ) it it. <strong>WARNING</strong> - This method is
     * useful only for normalizing application-generated paths. It does not
     * try to perform security checks for malicious input.
     * Normalize operations were was happily taken from
     * org.apache.catalina.util.RequestUtil in
     * Tomcat trunk, r939305
     *
     * @param path             Relative path to be normalized
     * @param replaceBackSlash Should '\\' be replaced with '/'
     * @return normalized path
     */
    private static String normalize(String path, boolean replaceBackSlash) {

        if (path == null) {
            return null;
        }

        // Create a place for the normalized path
        String normalized = path;

        if (replaceBackSlash && normalized.indexOf('\\') >= 0) {
            normalized = normalized.replace('\\', '/');
        }

        if (normalized.equals("/.")) {
            return "/";
        }

        // Add a leading "/" if necessary
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index)
                    + normalized.substring(index + 1);
        }

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index)
                    + normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0) {
                break;
            }
            if (index == 0) {
                return (null);  // Trying to go outside our context
            }
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2)
                    + normalized.substring(index + 3);
        }

        // Return the normalized path that we have completed
        return (normalized);

    }

    public static String getContextPath(HttpServletRequest request) {
        String contextPath = (String) request.getAttribute(INCLUDE_CONTEXT_PATH_ATTRIBUTE);
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }
        if ("/".equals(contextPath)) {
            // Invalid case, but happens for includes on Jetty: silently adapt it.
            contextPath = "";
        }
        return decodeRequestString(request, contextPath);
    }

    @SuppressWarnings({"deprecation"})
    public static String decodeRequestString(HttpServletRequest request, String source) {
        String enc = determineEncoding(request);
        try {
            return URLDecoder.decode(source, enc);
        } catch (UnsupportedEncodingException ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Could not decode request string [" + source + "] with encoding '" + enc
                        + "': falling back to platform default encoding; exception message: " + ex.getMessage());
            }
            return URLDecoder.decode(source);
        }
    }

    /**
     * Determine the encoding for the given request.
     * Can be overridden in subclasses.
     * <p/>
     * The default implementation checks the request's
     * {@link ServletRequest#getCharacterEncoding() character encoding}, and if
     * that
     * <code>null</code>, falls back to the {@link #DEFAULT_CHARSET}.
     *
     * @param request current HTTP request
     * @return the encoding for the request (never <code>null</code>)
     * @see javax.servlet.ServletRequest#getCharacterEncoding()
     */
    protected static String determineEncoding(HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = DEFAULT_CHARSET;
        }
        return enc;
    }

    @Override
    public void destroy() {

    }
}
