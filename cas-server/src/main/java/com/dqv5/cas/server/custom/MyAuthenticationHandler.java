package com.dqv5.cas.server.custom;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;

import javax.security.auth.login.AccountLockedException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangsaichao
 * @date: 2018/7/21
 * @description: 自定义验证器
 */
public class MyAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

//    private UserService userService;
//
//    public UserService getUserService() {
//        return userService;
//    }
//
//    public void setUserService(UserService userService) {
//        this.userService = userService;
//    }

    public MyAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {
        UsernamePasswordCredential myCredential = (UsernamePasswordCredential) credential;

        String username = myCredential.getUsername();
//        Map<String, Object> user = userService.findByUserName(username);

        Map<String, Object> user = new HashMap<>();
        user.put("account", username);
        user.put("id", 1);
        user.put("user_name", "管理员");
        user.put("realName", "管理员");
        //可以在这里直接对用户名校验,或者调用 CredentialsMatcher 校验
//        if (!user.get("password").equals(myCredential.getPassword())) {
//            throw new CredentialExpiredException("用户名或密码错误！");
//        }
        //这里将 密码对比 注销掉,否则 无法锁定  要将密码对比 交给 密码比较器 在这里可以添加自己的密码比较器等
        //if (!password.equals(user.getPassword())) {
        //    throw new IncorrectCredentialsException("用户名或密码错误！");
        //}
        if ("1".equals(user.get("state"))) {
            throw new AccountLockedException("账号已被锁定,请联系管理员！");
        }
        return createHandlerResult(credential, this.principalFactory.createPrincipal(username, user));
    }

    @Override
    public boolean supports(Credential credential) {
        return credential instanceof UsernamePasswordCredential;
    }
}
