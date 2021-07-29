package com.liu.test;

import com.liu.api.HelloObject;
import com.liu.api.HelloService;
import com.liu.rpc.annotation.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(HelloObject object) {
        log.info("接受到消息：{}", object.getMessage());
        return "这是Impl方法";
    }
}
