package com.example.whoami.java;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.text.StringEscapeUtils;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                values[i] = escapeUserInput(values[i]);
            }
        }
        return values;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return escapeUserInput(value);
    }

    private String escapeUserInput(String input) {
        return StringEscapeUtils.escapeHtml4(input);
    }
}
