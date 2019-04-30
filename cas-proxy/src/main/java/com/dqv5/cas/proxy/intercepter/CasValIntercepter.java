package com.dqv5.cas.proxy.intercepter;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dqv5.cas.proxy.entity.AppOperateResponse;
import com.dqv5.cas.proxy.tool.WebUtil;
import com.dqv5.cas.proxy.constant.ErrCode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * CasValIntercepter
 */
public class CasValIntercepter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
            throws Exception {
        // 获取 referer
        String referer = WebUtil.getRequestHeaderReferer(httpServletRequest);
        if (StringUtils.isBlank(referer)) {
            // 只允许跨域访问
            httpServletResponse.setContentType("application/json; charset=utf-8");
            PrintWriter writer = httpServletResponse.getWriter();
            AppOperateResponse obj = new AppOperateResponse(ErrCode.UNKONW_ERROR.getErrCode(), "只支持跨域请求!");
            writer.print(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat));
            writer.close();
            httpServletResponse.flushBuffer();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}