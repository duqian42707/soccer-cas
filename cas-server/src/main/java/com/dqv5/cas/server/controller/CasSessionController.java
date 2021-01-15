//package com.dqv5.cas.server.controller;
//
//import com.dqv5.cas.server.service.MonitorService;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//
///**
// * CasSessionController
// */
//@RestController
//public class CasSessionController {
//
//    @Resource
//    private MonitorService monitorService;
//
//    /**
//     * 获取 cas中的 session
//     */
//    @GetMapping("/app/sessions")
//    @CrossOrigin(origins = "*", allowCredentials = "true")
//    @ResponseBody
//    public Object obternSessions() {
//        return monitorService.obternSessions();
//    }
//
//}
