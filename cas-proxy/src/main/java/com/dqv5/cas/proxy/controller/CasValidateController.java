package com.dqv5.cas.proxy.controller;

import com.alibaba.fastjson.JSONObject;
import com.dqv5.cas.proxy.config.CasServerProperties;
import com.dqv5.cas.proxy.entity.AppOperateResponse;
import com.dqv5.cas.proxy.entity.CasValidateResponse;
import com.dqv5.cas.proxy.constant.Constant;
import com.dqv5.cas.proxy.constant.ErrCode;
import com.dqv5.cas.proxy.exception.CasUnloginException;
import com.dqv5.cas.proxy.exception.UnAuthException;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * CasValidateController
 */
@Controller
public class CasValidateController {

    /**
     * 授权标示
     */
    private final static int VALIDATE_FLAG = 0;
    private final static int VALIDATE_FULL_FLAG = 1;


    @Autowired
    private CasServerProperties casServerProperties;

    @RequestMapping(value = {"/app/validate", "/app/validate_full"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @CrossOrigin(origins = "*", allowCredentials = "true")
    public Object validate(String callback, HttpServletRequest request, HttpSession session)
            throws UnsupportedEncodingException {
        try {
            if (StringUtils.isBlank(callback)) {
                return new AppOperateResponse(ErrCode.PARAM_ERROR.getErrCode(), "callback不能为空!");
            }
            CasValidateResponse response = loadCasServerData(request, session, VALIDATE_FLAG);
            response.setErrcode(ErrCode.SUCCESS.getErrCode());
            return response;
        } catch (UnAuthException e) {
            return new AppOperateResponse(ErrCode.APP_NO_ACCESS.getErrCode(), e.getMessage());
        } catch (CasUnloginException ec) {
            callback = URLDecoder.decode(callback, "UTF-8");
            session.setAttribute(Constant.BROWER_TO_CAS_AUTH_SERVICE, URLEncoder.encode(callback, "UTF-8"));
            System.err.println(session.getId());
            return new AppOperateResponse(ErrCode.REDIRECT.getErrCode(), ec.getMessage());
        }
    }

    /**
     * 读取 cas 返回数据
     *
     * @throws CasUnloginException
     * @throws UnsupportedEncodingException
     */
    private CasValidateResponse loadCasServerData(HttpServletRequest request, HttpSession session, int authFlag)
            throws UnAuthException, CasUnloginException, UnsupportedEncodingException {
        CasValidateResponse response = new CasValidateResponse();
        // todo 暂不验证授权
        return loadUserInfoFromCas(request, session, response);
        /*
        // 获取 request header referer 信息
        String referer = WebUtil.getRequestHeaderReferer(request);
        String casProxyUrl = StringUtils.lowerCase(URLEncoder.encode(casServerProperties.getCasProxy(), "UTF-8"));
        if (referer.equals(casProxyUrl)) {
            // 授权页面
            return loadUserInfoFromCas(request, session, response);
        } else {
            CasGrant casGrant = casValidateService.validateApplicationService(referer);

            if (casGrant != null) {
                if (VALIDATE_FLAG == authFlag && casGrant.getIs_validate() == 1) {
                    return loadUserInfoFromCas(request, session, response);
                } else if (VALIDATE_FULL_FLAG == authFlag && casGrant.getIs_validate_full() == 1) {
                    return loadUserInfoFromCas(request, session, response);
                } else {
                    throw new UnAuthException("未授权的应用，请联系管理员进行授权!");
                }
            } else {
                throw new UnAuthException("未授权的应用，请联系管理员进行授权!");
            }
        }*/

    }

    private CasValidateResponse loadUserInfoFromCas(HttpServletRequest request, HttpSession session,
                                                    CasValidateResponse response) throws UnsupportedEncodingException, CasUnloginException {
        // 从 session 中 提取信息
        String casUserInfoStr = (String) session.getAttribute(Constant.CAS_AUTH_SUCCESS_USER_INFO);
        if (StringUtils.isBlank(casUserInfoStr)) {
            // 从 request 中 获取 cas server 的 地址 和 部署上下文名称
            StringBuilder sb = new StringBuilder("http://");
            sb.append(request.getServerName());
            sb.append(":");
            sb.append(request.getServerPort());
            String contextPath = request.getContextPath();
            if (StringUtils.isNotBlank(contextPath)) {
                sb.append(request.getContextPath());
            }
            sb.append("/app/redirect");
            String serviceUrl = URLEncoder.encode(sb.toString(), "UTF-8");
            String casServerLoginUrl = casServerProperties.getCasServerName() + "/login?service=" + serviceUrl;
            throw new CasUnloginException(casServerLoginUrl);
        }
        JSONObject casUserInfo = JSONObject.parseObject(casUserInfoStr);
        response.setAccount(casUserInfo.getString(Constant.CAS_USER_ACCOUNT));
        response.setUserType(casUserInfo.getString(Constant.CAS_USER_TYPE));
        response.setUserCode(casUserInfo.getString(Constant.CAS_USER_CODE));
        response.setRealName(casUserInfo.getString(Constant.CAS_USER_REAL_NAME));
        return response;
    }


    @GetMapping("/app/redirect")
    public String redirect(HttpServletRequest request, HttpSession session) throws UnsupportedEncodingException {
        JSONObject browerUserInfo = new JSONObject();
        String account = request.getRemoteUser();
        browerUserInfo.put(Constant.CAS_USER_ACCOUNT, account);
        // 填充其他信息
        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
        Map<String, Object> attributes = principal.getAttributes();
        browerUserInfo.put(Constant.CAS_USER_TYPE, attributes.get("userType"));
        browerUserInfo.put(Constant.CAS_USER_CODE, attributes.get("userCode"));
        if (attributes.get("realName") != null) {
            browerUserInfo.put(Constant.CAS_USER_REAL_NAME,
                    URLDecoder.decode(attributes.get("realName").toString(), "utf-8"));
        } else {
            browerUserInfo.put(Constant.CAS_USER_REAL_NAME, null);
        }
        session.setAttribute(Constant.CAS_AUTH_SUCCESS_USER_INFO, browerUserInfo.toJSONString());
        String callback = (String) session.getAttribute(Constant.BROWER_TO_CAS_AUTH_SERVICE);
        System.err.println(session.getId());
        if (callback == null) {
            return "redirect:" + casServerProperties.getCasServerName() + "/login";
        }
        return "redirect:" + URLDecoder.decode(callback, "UTF-8");
    }

    @GetMapping("/app/logout")
    public String logout(HttpSession session) {
        // 清除 session信息
        session.removeAttribute(Constant.BROWER_TO_CAS_AUTH_SERVICE);
        session.removeAttribute(Constant.CAS_AUTH_SUCCESS_USER_INFO);
        return "redirect:" + casServerProperties.getCasServerName() + "/logout";
    }
}