package com.liu.rpc.loadbalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.liu.rpc.loadbalancer.LoadBalancer;

import java.util.ArrayList;
import java.util.List;

/**
 * 加权轮询法
 */
public class WeightRoundRobinLoadBalancer implements LoadBalancer {

    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        List<Instance> instanceList = new ArrayList<>();
        for (Instance instance : instances) {
            int weight = (int) instance.getWeight();
            for (int i = 0; i < weight; i++) {
                instanceList.add(instance);
            }
        }

        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instanceList.get(index++);
    }
}
