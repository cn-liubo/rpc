package com.liu.test;

import com.liu.api.ByeService;
import com.liu.api.HelloObject;
import com.liu.api.HelloService;
import com.liu.rpc.serializer.CommonSerializer;
import com.liu.rpc.transport.RpcClientProxy;
import com.liu.rpc.transport.socket.client.SocketClient;

/**
 * 测试用消费者（客户端）
 */
public class TestSocketClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient(CommonSerializer.KRYO_SERIALIZER);
        RpcClientProxy proxy = new RpcClientProxy(client);

        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);

        ByeService byeService = proxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Socket"));
    }
}
