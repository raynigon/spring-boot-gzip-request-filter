package com.raynigon.ecs.logging.access.server;

import ch.qos.logback.access.common.AccessConstants;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class CustomTeeFilter implements Filter {
    boolean active;

    @Override
    public void destroy() {
        // NOP
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        if (active && request instanceof HttpServletRequest) {
            try {
                TeeHttpServletRequest teeRequest = new TeeHttpServletRequest((HttpServletRequest) request);
                TeeHttpServletResponse teeResponse = new TeeHttpServletResponse((HttpServletResponse) response);

                filterChain.doFilter(teeRequest, teeResponse);

                teeResponse.finish();
                // let the output contents be available for later use by
                // logback-access-logging
                teeRequest.setAttribute(AccessConstants.LB_OUTPUT_BUFFER, teeResponse.getOutputBuffer());
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            } catch (ServletException e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            filterChain.doFilter(request, response);
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String includeListAsStr = filterConfig.getInitParameter(AccessConstants.TEE_FILTER_INCLUDES_PARAM);
        String excludeListAsStr = filterConfig.getInitParameter(AccessConstants.TEE_FILTER_EXCLUDES_PARAM);
        String localhostName = getLocalhostName();

        active = computeActivation(localhostName, includeListAsStr, excludeListAsStr);
        // Log the activation status on stdout, because loggers cannot be used here
        if (active) {
            System.out.println("TeeFilter will be ACTIVE on this host [" + localhostName + "]");
        } else {
            System.out.println("TeeFilter will be DISABLED on this host [" + localhostName + "]");
        }
    }

    public static List<String> extractNameList(String nameListAsStr) {
        List<String> nameList = new ArrayList<String>();
        if (nameListAsStr == null) {
            return nameList;
        }

        nameListAsStr = nameListAsStr.trim();
        if (nameListAsStr.length() == 0) {
            return nameList;
        }

        String[] nameArray = nameListAsStr.split("[,;]");
        for (String n : nameArray) {
            n = n.trim();
            nameList.add(n);
        }
        return nameList;
    }

    static String getLocalhostName() {
        String hostname = "127.0.0.1";

        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }
        return hostname;
    }

    public static boolean computeActivation(String hostname, String includeListAsStr, String excludeListAsStr) {
        List<String> includeList = extractNameList(includeListAsStr);
        List<String> excludeList = extractNameList(excludeListAsStr);
        boolean inIncludesList = matchesIncludesList(hostname, includeList);
        boolean inExcludesList = matchesExcludesList(hostname, excludeList);
        return inIncludesList && (!inExcludesList);
    }

    static boolean matchesIncludesList(String hostname, List<String> includeList) {
        if (includeList.isEmpty())
            return true;
        return includeList.contains(hostname);
    }

    static boolean matchesExcludesList(String hostname, List<String> excludesList) {
        if (excludesList.isEmpty())
            return false;
        return excludesList.contains(hostname);
    }
}
