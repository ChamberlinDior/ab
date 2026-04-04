package com.agora.assemblee.common.controller;

import com.agora.assemblee.common.api.ApiResponse;
import com.agora.assemblee.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemController {
    private final AppProperties properties;

    @GetMapping("/network-config")
    public ApiResponse<Map<String, Object>> networkConfig() throws Exception {
        return ApiResponse.ok(Map.of(
                "publicBaseUrl", properties.getNetwork().getPublicBaseUrl(),
                "hostName", InetAddress.getLocalHost().getHostName(),
                "hostAddress", InetAddress.getLocalHost().getHostAddress(),
                "discoveryEnabled", properties.getNetwork().isDiscoveryEnabled()
        ), "Diagnostic réseau local React Native");
    }
}
