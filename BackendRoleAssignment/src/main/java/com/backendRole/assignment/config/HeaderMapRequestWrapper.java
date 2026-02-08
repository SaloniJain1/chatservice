package com.backendRole.assignment.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;
import lombok.extern.slf4j.Slf4j;

/**
 * A custom HttpServletRequestWrapper that allows adding headers to the request.
 * This is used to pass the validated User ID from the JWT filter to the
 * controllers.
 * It also logs operations for debugging and monitoring.
 */
@Slf4j
public class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

    /**
     * Map to store custom headers.
     */
    private final Map<String, String> customHeaders = new HashMap<>();

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request the request to wrap
     */
    public HeaderMapRequestWrapper(HttpServletRequest request) {
        super(request);
        log.debug("HeaderMapRequestWrapper created for request URI: {}", request.getRequestURI());
    }

    /**
     * Adds a custom header to the request.
     *
     * @param name  the name of the header
     * @param value the value of the header
     */
    public void addHeader(String name, String value) {
        this.customHeaders.put(name, value);
        log.debug("Added custom header: {} = {}", name, value);
    }

    /**
     * Returns the value of a header, checking custom headers first.
     *
     * @param name the name of the header
     * @return the value of the header, or null if not found
     */
    @Override
    public String getHeader(String name) {
        String customValue = customHeaders.get(name);
        if (customValue != null) {
            return customValue;
        }
        return super.getHeader(name);
    }

    /**
     * Returns an enumeration of all header names, including custom ones.
     *
     * @return an enumeration of header names
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> set = new HashSet<>(customHeaders.keySet());

        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }

        return Collections.enumeration(set);
    }

    /**
     * Returns all the values of a specified header.
     *
     * @param name the name of the header
     * @return an enumeration of header values
     */
    @Override
    public Enumeration<String> getHeaders(String name) {
        String customValue = customHeaders.get(name);
        if (customValue != null) {
            return Collections.enumeration(Collections.singletonList(customValue));
        }
        return super.getHeaders(name);
    }
}
