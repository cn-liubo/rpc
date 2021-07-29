package com.liu.rpc.transport;

import com.liu.rpc.entity.RpcRequest;
import com.liu.rpc.entity.RpcResponse;
import com.liu.rpc.transport.netty.client.NettyClient;
import com.liu.rpc.transport.socket.client.SocketClient;
import com.liu.rpc.util.RpcMessageChecker;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/*
    客户端方面，由于在客户端这一侧我们并没有接口的具体实现类，就没有办法直接生成实例对象。
    这时，我们可以通过动态代理的方式生成实例，并且调用方法时生成需要的RpcRequest对象并且发送给服务端。
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    @SuppressWarnings("unckecked")
    public <T> T getProxy(Class<T> clazz) {
        //用哪个类加载器去加载代理对象；动态代理类需要实现的接口；动态代理方法在执行时，会调用h里面的invoke方法去执行
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);//返回实例化后的实现class接口类型的对象
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("调用方法：{}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), false);//封装RpcRequest对象，请求号、调用接口的全限定类名、方法名、方法参数、参数类型
        RpcResponse rpcResponse = null;
        if (client instanceof NettyClient) {
            try {
                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);//开启一个异步任务来执行发送请求方法
                rpcResponse = completableFuture.get();
            } catch (Exception e) {
                log.error("方法调用请求发送失败", e);
                return null;
            }
        }
        if (client instanceof SocketClient) {
            rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);//由客户端将封装好的请求发送出去，并返回响应
        }
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }
}
