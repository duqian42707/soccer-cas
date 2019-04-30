package com.dqv5.cas.client.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author duqian
 * @date 2019-04-26
 */
public class CustomPasswordEncoder implements PasswordEncoder {

    /**
     * 前台传来的明文密码会进入这里。然后取得加密密码
     * 通过前台传来的明文密码根据自己的东西返回获取数据库中的加密密码的格式，用于matchs方法中进行比较
     *
     * @param passwordSeq 明文密码
     * @return 加密密码
     */
    @Override
    public String encode(CharSequence passwordSeq) {
        String password = passwordSeq.toString();
        return DigestUtils.md5Hex(password);
    }

    /**
     * 密码校验
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 加密密码
     * @return 是否成功
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return StringUtils.equals(this.encode(rawPassword), encodedPassword);
    }


}

