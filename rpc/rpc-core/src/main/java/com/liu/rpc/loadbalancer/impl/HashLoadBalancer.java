package com.liu.rpc.loadbalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.liu.rpc.loadbalancer.LoadBalancer;

import java.util.List;

/**
 * 源地址哈希法
 */
public class HashLoadBalancer implements LoadBalancer {

    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        for (Instance instance : instances) {
            String ip = instance.getIp();
            index = ip.hashCode() % instances.size();
        }
        return instances.get(index);
    }
}
