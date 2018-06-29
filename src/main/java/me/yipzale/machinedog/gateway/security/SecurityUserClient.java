package me.yipzale.machinedog.gateway.security;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "machinedog-security")
public interface SecurityUserClient {
    @RequestMapping(value = "/users/{userId}/check_login", method = RequestMethod.GET)
    Object checkLogin(@PathVariable("userId") Long userId, @RequestParam("access_token") String accessToken);
}
