package com.dqv5.cas.proxy.tool;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * WebUtil
 */
public class WebUtil {
    static Pattern pattern = Pattern.compile("(\\w+):\\/\\/([^/:]+)(:\\d*)?(\\/)?[a-zA-Z]*(\\/)?");

    /**
     * 从 request header 获取 referer
     */
    public static String getRequestHeaderReferer(HttpServletRequest request) {
        String referer = request.getHeader("referer");
        if (StringUtils.isNotBlank(referer)) {
            try {
                //先 对 referer 进行 处理，获取 第一级目录
                Matcher mather = pattern.matcher(referer);
                mather.find();
                referer = mather.group(0);
                if (!StringUtils.endsWith(referer, "/")) {
                    referer = referer + "/";
                }
                return StringUtils.lowerCase(URLEncoder.encode(referer, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取 用户的 UserAgent 信息
     * code by zyj
     */
    public static String getClientUserAgent(HttpServletRequest request) {
        JSONObject clientInfo = new JSONObject();
        //获取User Agent信息
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        //操作系统
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        clientInfo.put("OS", operatingSystem.getName());
        //浏览器信息
        Browser browser = userAgent.getBrowser();
        clientInfo.put("BN", browser.getName());
        clientInfo.put("BV", browser.getVersion(request.getHeader("User-Agent")).getVersion());
        //获取客户端ip信息
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        clientInfo.put("IP", ip);
        return clientInfo.toString();
    }
}