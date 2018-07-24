package me.yipzale.machinedog.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import me.yipzale.machinedog.gateway.security.SecurityUserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class AccessFilter extends ZuulFilter {
    @Autowired
    private SecurityUserClient securityUserClient;

    private AccessSecurity accessSecurity;

    private Logger logger;

    public AccessFilter() {
        super();
        accessSecurity = new AccessSecurity();
        logger = LoggerFactory.getLogger(AccessFilter.class);
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        if (accessSecurity.match(request)) {
            String openId = request.getParameter("openId");
            String token = request.getParameter("access_token");
            if (null == openId || "".equals(openId) || null == token || "".equals(token)) {
                context.setSendZuulResponse(false);
                context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
                return null;
            }
            Long userId = (long)0;
            try {
                userId = Long.decode(openId);
            } catch (Exception ex) {
            }

            if (userId == 0) {
                context.setResponseStatusCode(HttpStatus.BAD_REQUEST.value());
                context.setSendZuulResponse(false);
                return null;

            }

            try {
                securityUserClient.checkLogin(userId, token);
            } catch (Exception ex) {
                logger.info(String.format("auth error. userId %s, token: %s", userId, token));
                context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
                context.setSendZuulResponse(false);
            }

        }
        return null;
    }

    class AccessSecurity {
        private AntPathMatcher matcher = new AntPathMatcher();

        List<RequestMatcherConfigure> matcherConfigures = new ArrayList<RequestMatcherConfigure>();

        public AccessSecurity() {
            this.configure();
        }

        public void configure() {
            matcherConfigures.add(new RequestMatcherConfigure("/blog/articles/**", "POST"));
            matcherConfigures.add(new RequestMatcherConfigure("/blog/articles", "POST"));
        }

        public boolean match(HttpServletRequest request) {
            String uri = request.getRequestURI();
            String method = request.getMethod().toLowerCase();
            for (RequestMatcherConfigure requestMatcherConfigure: matcherConfigures) {
                if ("*".equals(requestMatcherConfigure.getMethod()) || method.equals(requestMatcherConfigure.getMethod())) {
                    if (matcher.match(requestMatcherConfigure.getPatterns(), uri)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    class RequestMatcherConfigure {
        private String patterns;
        private String method;

        public RequestMatcherConfigure(String patterns) {
            this(patterns, "*");
        }

        public RequestMatcherConfigure(String patterns, String method) {
            this.patterns = patterns;
            this.method = method.toLowerCase();
        }

        public String getPatterns() {
            return patterns;
        }

        public String getMethod() {
            return method;
        }
    }
}
