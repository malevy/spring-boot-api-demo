package net.malevy.hyperdemo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class OPAInputFactory {

    public static Map<String, Object> buildHttpInput(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName).toLowerCase(Locale.ROOT));
        }

        URI uri = URI.create(request.getRequestURI());
        List<String> paths = Arrays.stream(uri.getPath().split("/"))
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());

        Map<String, Object> http = new HashMap<>();
        http.put("method", request.getMethod());
        http.put("headers", headers);
        http.put("path", paths);
        return http;
    }

    public static Map<String, Object> buildUserInput(Authentication authentication) {
        List<Object> authorities = Collections.emptyList();
        if (null != authentication.getAuthorities()) {
            authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }

        Map<String, Object> user = new HashMap<>();
        user.put("name", authentication.getName());
        user.put("isAuthenticated", authentication.isAuthenticated());
        user.put("authorities", authorities);
        return user;
    }
}
