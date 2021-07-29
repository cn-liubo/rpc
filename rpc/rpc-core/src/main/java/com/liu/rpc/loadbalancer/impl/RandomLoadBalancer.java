package com.liu.rpc.loadbalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.liu.rpc.loadbalancer.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * 随机法
 */
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}
