package com.liu.rpc.transport;

import com.liu.rpc.annotation.Service;
import com.liu.rpc.annotation.ServiceScan;
import com.liu.rpc.enumeration.RpcError;
import com.liu.rpc.exception.RpcException;
import com.liu.rpc.provider.ServiceProvider;
import com.liu.rpc.registry.ServiceRegistry;
import com.liu.rpc.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Set;

@Slf4j
public abstract class AbstractRpcServer implements RpcServer {

    protected String host;
    protected int port;

    protected ServiceProvider serviceProvider;
    protected ServiceRegistry serviceRegistry;

    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();//得到启动类的全限定类名
//        System.out.println(mainClassName);
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(ServiceScan.class)) {//判断这个启动类是否有ServiceScan注解
                log.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            log.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));//得到启动类所在的包名
//            System.out.println(basePackage);
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);//得到这个包下的所有类
//        System.out.println(classSet);
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Service.class)) {//找到这个包下带有Service注解的类
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();//创建服务器提供接口的实现类的对象，用来反射调用方法用的（注意：接口不能创建对象，其实创建的实现类的对象，需要强转）
                } catch (IllegalAccessException | InstantiationException e) {
                    log.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();//得到该类的所有的全限定的接口名
                    for (Class<?> oneInterface : interfaces) {
                        publishService(obj, oneInterface.getCanonicalName());//将接口的实现类对象和接口的全限定类名注册到注册中心
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    /**
     * 将服务和服务名称注册
     * @param service 服务，实现类的对象
     * @param serviceName 接口的全限定类名
     * @param <T>
     */
    @Override
    public <T> void publishService(T service, String serviceName) {
        //保存在本地的
        serviceProvider.addServiceProvider(service, serviceName);
        //注册到注册中心上的
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));//host和port在服务端的启动类传入的
    }
}
