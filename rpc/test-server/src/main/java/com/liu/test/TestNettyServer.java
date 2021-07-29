package com.liu.test;

import com.liu.rpc.annotation.ServiceScan;
import com.liu.rpc.serializer.CommonSerializer;
import com.liu.rpc.transport.RpcServer;
import com.liu.rpc.transport.netty.server.NettyServer;

/**
 * 测试用Netty服务提供者（服务端）
 */
@ServiceScan
public class TestNettyServer {
    public static void main(String[] args) {
        RpcServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }
}
