package com.liu.rpc.handler;

import com.liu.rpc.entity.RpcRequest;
import com.liu.rpc.entity.RpcResponse;
import com.liu.rpc.enumeration.ResponseCode;
import com.liu.rpc.provider.ServiceProvider;
import com.liu.rpc.provider.impl.ServiceProviderImpl;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 进行过程调用的过滤器
 */
@Slf4j
public class RequestHandler {

    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new ServiceProviderImpl();
    }

    public Object handler(RpcRequest rpcRequest) {
        Object service = RequestHandler.serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());//通过请求消息中的接口名得到对应的实现类的对象
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());//通过方法名和参数类型得到一个方法对象
            result = method.invoke(service, rpcRequest.getParameters());//传入一个实例化的对象和方法的参数反射调用方法，并获取返回值
            log.info("服务：{}，成功调用方法：{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        return result;
    }
}
