package com.liu.rpc.hook;

import com.liu.rpc.factory.ThreadPoolFactory;
import com.liu.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于注销服务和关闭线程池
 * 如果你启动完成服务端后把服务端给关闭了，并不会自动地注销Nacos中对应的服务信息，这样就导致了当客户端再次向Nacos请求服务时，
 * 会获取到已经关闭的服务端信息，最终就有可能因为连接不到服务器而调用失败。
 * 那么我们就需要一种办法，在服务端关闭之前自动向 Nacos 注销服务。但是有一个问题，我们不知道什么时候服务器会关闭，
 * 也就不知道这个方法调用的时机，就没有办法手工去调用。这时，我们就需要钩子。
 * 钩子是什么呢？是在某些事件发生后自动去调用的方法。那么我们只需要把注销服务的方法写到关闭系统的钩子方法里就行了。
 */
@Slf4j
public class ShutdownHook {

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        log.info("关闭后将自动注销所有服务");
        /*
            当程序正常退出,系统调用 System.exit方法或虚拟机被关闭时才会执行添加的shutdownHook线程。
            其中shutdownHook是一个已初始化但并不有启动的线程，当jvm关闭的时候，会执行系统中已经设置的
            所有通过方法addShutdownHook添加的钩子，当系统执行完这些钩子后，jvm才会关闭。所以可通过
            这些钩子在jvm关闭的时候进行内存清理、资源回收等工作。本质上就是在jvm关闭时,执行一个线程。
         */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
