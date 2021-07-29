package com.liu.rpc.transport.socket.server;

import com.liu.rpc.entity.RpcRequest;
import com.liu.rpc.entity.RpcResponse;
import com.liu.rpc.handler.RequestHandler;
import com.liu.rpc.serializer.CommonSerializer;
import com.liu.rpc.transport.socket.util.ObjectReader;
import com.liu.rpc.transport.socket.util.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 处理RpcRequest的工作线程
 */
@Slf4j
public class SocketRequestHandlerThread implements Runnable {

    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;

    public SocketRequestHandlerThread(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();//通过Socket得到输入流
            OutputStream outputStream = socket.getOutputStream();//通过Socket得到输出流

            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);//服务端解析客户端发送的RpcRequest请求
            Object result = requestHandler.handler(rpcRequest);//通过RpcRequest来调用方法，得到返回结果
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());//快速封装响应数据包
            ObjectWriter.writeObject(outputStream, response, serializer);//通过对应的序列化方式序列化后并发送响应数据包
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
