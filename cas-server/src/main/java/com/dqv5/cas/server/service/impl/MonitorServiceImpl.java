package com.dqv5.cas.server.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dqv5.cas.server.service.MonitorService;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.ticket.ServiceTicket;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * MonitorServiceImpl
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 总量
     */
    private final static String TOTAL = "total";

    /**
     * 数据
     */
    private final static String PAYLOAD = "payload";


    /**
     * 名称
     */
    private final static String NAME = "name";

    /**
     * 登录账号
     */
    private final static String ACCOUNT = "account";

    /**
     * 创建时间
     */
    private final static String DATE = "date";

    /**
     * cas服务器的id
     */
    private final static String CAS_SERVER_ID = "cas-service-id";

    /**
     * 数据
     */
    private final static String PAYLOADS = "payloads";

    /**
     * ticket 注册信息
     * <p>
     * 返回的 session 注册信息格式 { total: 0, cas-server: { name: "cas服务", payloads: [ {
     * account:"", date:"" } ] }, service-id: { name: "service名称", payloads: [ {
     * account:"", date:"" } ] } }
     */
    @NotNull
    @Autowired
    private TicketRegistry ticketRegistry;

    @Override
    public JSONObject obternSessions() {
        JSONObject response = new JSONObject();
        Collection<Ticket> tickets = ticketRegistry.getTickets();
        if (tickets == null || tickets.isEmpty()) {
            response.put(TOTAL, 0);
            return response;
        }

        JSONObject payload = new JSONObject();
        int total = 0;
        // 遍历 ticket 获取 session信息
        for (Ticket ticket : tickets) {
            // 统计数量
            total++;

            // 获取session信息
            if (ticket instanceof ServiceTicket) {
                ServiceTicket serviceTicket = (ServiceTicket) ticket;
                String serviceId = serviceTicket.getId();
                JSONObject sessionInfo = payload.getJSONObject(serviceId);
                if (sessionInfo == null) {
                    sessionInfo = new JSONObject();
                    sessionInfo.put(NAME, serviceTicket.getService().getId());

                }
                TicketGrantingTicket grantingTicket = serviceTicket.getTicketGrantingTicket();
                obternGrantingTicketInfo(sessionInfo, grantingTicket);
                if (!payload.containsKey(serviceId)) {
                    payload.put(serviceId, sessionInfo);
                }
            } else if (ticket instanceof TicketGrantingTicket) {
                JSONObject sessionInfo = payload.getJSONObject(CAS_SERVER_ID);
                if (sessionInfo == null) {
                    sessionInfo = new JSONObject();
                    sessionInfo.put(NAME, "cas服务");
                }
                TicketGrantingTicket grantingTicket = (TicketGrantingTicket) ticket;
                obternGrantingTicketInfo(sessionInfo, grantingTicket);

                if (!payload.containsKey(CAS_SERVER_ID)) {

                    payload.put(CAS_SERVER_ID, sessionInfo);
                }
            }
        }
        response.put(TOTAL, total);
        response.put(PAYLOAD, payload);
        return response;
    }

    /**
     * 获取授权 ticke信息
     */
    private void obternGrantingTicketInfo(JSONObject sessionInfo, TicketGrantingTicket grantingTicket) {
        JSONObject ticketInfo = new JSONObject();
        // 获取 认证信息
        Authentication authentication = grantingTicket.getAuthentication();
        Principal principal = authentication.getPrincipal();
        ticketInfo.put(ACCOUNT, principal.getId());
        // 获取认证日期
        ZonedDateTime authenDate = authentication.getAuthenticationDate();
        ticketInfo.put(DATE, authenDate.toString());
        // 获取用户数量
        // int userCount = grantingTicket.getCountOfUses();
        // sessionInfo.put("user_count", userCount);
        // 客户端相关信息
        Map<String, Object> attributes = principal.getAttributes();
        ticketInfo.put("clientIp", attributes.getOrDefault("clientIp", "unset"));
        ticketInfo.put("OS", attributes.getOrDefault("OS", "unset"));
        ticketInfo.put("BN", attributes.getOrDefault("BN", "unset"));
        ticketInfo.put("BV", attributes.getOrDefault("BV", "unset"));

        JSONArray payloads = sessionInfo.getJSONArray(PAYLOADS);
        if (payloads == null) {
            payloads = new JSONArray();
        }
        payloads.add(ticketInfo);
        if (!sessionInfo.containsKey(PAYLOADS)) {

            sessionInfo.put(PAYLOADS, payloads);
        }
    }

}