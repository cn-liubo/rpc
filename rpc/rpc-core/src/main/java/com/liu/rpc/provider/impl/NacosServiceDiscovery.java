package com.liu.rpc.provider.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.liu.rpc.provider.ServiceDiscovery;
import com.liu.rpc.enumeration.RpcError;
import com.liu.rpc.exception.RpcException;
import com.liu.rpc.loadbalancer.LoadBalancer;
import com.liu.rpc.loadbalancer.impl.RandomLoadBalancer;
import com.liu.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Nacos服务发现中心
 */
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery {

    private LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if (loadBalancer == null) {
            this.loadBalancer = new RandomLoadBalancer();
        } else {
            this.loadBalancer = loadBalancer;
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);//得到serviceName下的所有服务
            if (instances.size() == 0) {
                log.error("找不到对应的服务" + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);//通过负载均衡算法找到一个服务
            return new InetSocketAddress(instance.getIp(), instance.getPort());//返回该服务对应的ip和port
        } catch (NacosException e) {
            log.error("获取服务时有错误发生", e);
        }
        return null;
    }
}
