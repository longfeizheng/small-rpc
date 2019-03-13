package com.niocoder.api.impl;

import com.niocoder.api.HelloRPC;

public class HelloRPCImpl implements HelloRPC {
    public String hello(String name) {
        return "hello " + name;
    }
}
