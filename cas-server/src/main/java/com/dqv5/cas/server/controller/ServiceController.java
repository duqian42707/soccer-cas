package com.dqv5.cas.server.controller;

import com.dqv5.cas.server.pojo.CommonResp;
import com.dqv5.cas.server.pojo.ServiceParam;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.services.RegexRegisteredService;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;


/**
 * 服务管理
 *
 */
@RestController
@Slf4j
@RequestMapping("/client")
public class ServiceController {

    @Resource
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    /**
     * 注册service
     *
     * @param serviceParam 服务参数
     */
    @PostMapping("/saveClient")
    public CommonResp addClient(@RequestBody @Validated ServiceParam serviceParam) {
        try {
            if (false) {
                log.warn("权限不足，拒绝访问！");
                return new CommonResp(false, "权限不足，拒绝访问！");
            }
            RegexRegisteredService service = new RegexRegisteredService();
            ReturnAllAttributeReleasePolicy re = new ReturnAllAttributeReleasePolicy();
            if (serviceParam.getId() != 0) {
                service.setId(serviceParam.getId());
            }
            service.setServiceId(serviceParam.getServiceId());
            service.setDescription(serviceParam.getDescription());
            service.setAttributeReleasePolicy(re);
            service.setName(serviceParam.getName());
            servicesManager.save(service);
            //执行load让他生效
            servicesManager.load();
            return new CommonResp(true, "保存服务成功!");
        } catch (Exception e) {
            log.error("注册service异常", e);
            return new CommonResp(false, "保存服务失败!");
        }
    }

    /**
     * 删除service
     *
     * @param id 服务id
     */
    @PostMapping("/deleteClient/{id}")
    public Object deleteClient(@PathVariable long id) {
        try {
            if (false) {
                log.warn("权限不足，拒绝访问！");
                return new CommonResp(false, "权限不足，拒绝访问！");
            }
            servicesManager.delete(id);
            //执行load生效
            servicesManager.load();
            return new CommonResp(true, "删除服务成功!");
        } catch (IllegalArgumentException e) {
            //执行load生效
            servicesManager.load();
            return new CommonResp(true, "删除服务成功!");
        } catch (Exception e) {
            log.error("删除service异常", e);
            return new CommonResp(false, "删除服务失败!");
        }

    }

    /**
     * 查询service
     */
    @GetMapping("/findAllClient")
    public Object findAllService() {
        try {
            if (false) {
                log.warn("权限不足，拒绝访问！");
                return new CommonResp(false, "权限不足，拒绝访问！");
            }
            Collection<RegisteredService> allServices = servicesManager.getAllServices();
            return new CommonResp(true, "查询服务成功!", allServices);
        } catch (Exception e) {
            log.error("查询service异常", e);
            return new CommonResp(false, "查询服务失败!");
        }
    }

}

