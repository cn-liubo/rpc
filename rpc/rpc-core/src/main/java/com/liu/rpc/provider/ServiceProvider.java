package com.liu.rpc.provider;

/**
 * 保存和提供服务实例对象
 */
public interface ServiceProvider {
    /**
     * 注册服务
     * @param service 服务
     * @param serviceName 服务名，使用这个对象的实现的接口的全限定类名
     * @param <T>
     */
    <T> void addServiceProvider(T service, String serviceName);

    /**
     * 通过一个服务名称来获取服务
     * @param serviceName 服务名称
     * @return
     */
    Object getServiceProvider(String serviceName);
}
