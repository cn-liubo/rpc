package com.liu.rpc.registry.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.liu.rpc.enumeration.RpcError;
import com.liu.rpc.exception.RpcException;
import com.liu.rpc.registry.ServiceRegistry;
import com.liu.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * Nacos服务注册中心
 */
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            log.error("注册服务时有错误发生：", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}
