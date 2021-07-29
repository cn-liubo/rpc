package com.liu.rpc.transport.socket.client;

import com.liu.rpc.entity.RpcRequest;
import com.liu.rpc.entity.RpcResponse;
import com.liu.rpc.enumeration.ResponseCode;
import com.liu.rpc.enumeration.RpcError;
import com.liu.rpc.exception.RpcException;
import com.liu.rpc.loadbalancer.LoadBalancer;
import com.liu.rpc.loadbalancer.impl.RandomLoadBalancer;
import com.liu.rpc.provider.ServiceDiscovery;
import com.liu.rpc.provider.impl.NacosServiceDiscovery;
import com.liu.rpc.serializer.CommonSerializer;
import com.liu.rpc.transport.RpcClient;
import com.liu.rpc.transport.socket.util.ObjectReader;
import com.liu.rpc.transport.socket.util.ObjectWriter;
import com.liu.rpc.util.RpcMessageChecker;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket方式远程方法调用的消费者（客户端）
 */
@Slf4j
public class SocketClient implements RpcClient {

    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;

    public SocketClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }
    public SocketClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }
    public SocketClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }
    public SocketClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());//根据要调用的接口类型找到相应的ip和port
        try {
            Socket socket = new Socket();
            socket.connect(inetSocketAddress);//客户端先连接到这个ip和port上
            OutputStream outputStream = socket.getOutputStream();//通过Socket拿到输出流
            InputStream inputStream = socket.getInputStream();//通过Socket拿到输入流
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);//通过输出流将请求消息按照自定义的数据格式和序列化方式发送出去
            Object obj = ObjectReader.readObject(inputStream);//通过输入流将由服务端返回的消息解析为响应对象
            RpcResponse rpcResponse = (RpcResponse) obj;
            if (rpcResponse == null) {
                log.error("服务调用失败, service:{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if (rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                log.error("调用服务失败, service:{}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse;
        } catch (IOException e) {
            log.error("调用时有错误发生：", e);
            throw new RpcException("服务调用失败：", e);
        }
    }
}
