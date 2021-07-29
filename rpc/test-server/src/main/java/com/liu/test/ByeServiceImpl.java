package com.liu.test;

import com.liu.api.ByeService;
import com.liu.rpc.annotation.Service;

@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
