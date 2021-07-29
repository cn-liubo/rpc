package com.liu.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册接口
 */
public interface ServiceRegistry {

    /**
     * 将一个服务注册进注册中心
     * @param serviceName 服务名称是该对象实现接口的全限定类名
     * @param inetSocketAddress 提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}
