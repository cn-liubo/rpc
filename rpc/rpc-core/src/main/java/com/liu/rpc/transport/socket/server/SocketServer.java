package com.liu.rpc.transport.socket.server;

import com.liu.rpc.factory.ThreadPoolFactory;
import com.liu.rpc.handler.RequestHandler;
import com.liu.rpc.hook.ShutdownHook;
import com.liu.rpc.provider.impl.ServiceProviderImpl;
import com.liu.rpc.registry.impl.NacosServiceRegistry;
import com.liu.rpc.serializer.CommonSerializer;
import com.liu.rpc.transport.AbstractRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Socket方式远程方法调用的提供者（服务端）
 */
@Slf4j
public class SocketServer extends AbstractRpcServer {

    private final ExecutorService threadPool;
    private final CommonSerializer serializer;
    private final RequestHandler requestHandler = new RequestHandler();

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        this.threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }

    @Override
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(host, port));
            log.info("服务器启动...");
            ShutdownHook.getShutdownHook().addClearAllHook();//当服务器停止时，销毁注册中心的服务并销毁线程池
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info("消费者连接：{}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("服务器启动时有错误发生：", e);
        }
    }
}
