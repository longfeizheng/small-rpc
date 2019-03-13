package com.niocoder.client;

import com.niocoder.api.HelloNetty;
import com.niocoder.api.HelloRPC;
import com.niocoder.proxy.NettyRPCProxy;

public class TestNettyRPC {
    public static void main(String[] args) {
        HelloNetty helloNetty=(HelloNetty) NettyRPCProxy.create(HelloNetty.class);
        System.out.println(helloNetty.hello());
        HelloRPC helloRPC = (HelloRPC) NettyRPCProxy.create(HelloRPC.class);
        System.out.println(helloRPC.hello("RPC"));
    }
}
