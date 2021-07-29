package com.liu.test;

import com.liu.rpc.annotation.ServiceScan;
import com.liu.rpc.serializer.CommonSerializer;
import com.liu.rpc.transport.RpcServer;
import com.liu.rpc.transport.socket.server.SocketServer;

@ServiceScan
public class TestSocketServer {
    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1", 9998, CommonSerializer.HESSIAN_SERIALIZER);
        server.start();
    }
}
