package com.niocoder.handler;

import com.niocoder.common.ClassInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

public class InvokeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("[Server]:" + incoming.remoteAddress().toString().substring(1) + "已连接");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ClassInfo classInfo = (ClassInfo) msg;
        Object clazz = Class.forName(getImplClassName(classInfo)).newInstance();
        Method method = clazz.getClass().getMethod(classInfo.getMethodName(), classInfo.getTypes());
        // 反射调用
        Object retuslt = method.invoke(clazz, classInfo.getArgs());
        ctx.writeAndFlush(retuslt);
    }

    private String getImplClassName(ClassInfo classInfo) throws ClassNotFoundException {
        Class<?> superClass = Class.forName(classInfo.getClassName());
        Reflections reflections = new Reflections(classInfo.getClassName().substring(0, classInfo.getClassName().lastIndexOf('.')) + ".impl");
        //接口下的所有实现类
        Set<Class<?>> implClassSet = (Set<Class<?>>) reflections.getSubTypesOf(superClass);
        if (implClassSet.size() == 0) {
            System.out.println("未找到实现类");
            return null;
        } else if (implClassSet.size() > 1) {
            System.out.println("找到多个实现类，未明确使用哪一个");
            return null;
        } else {
            // 集合转换为数组
            Class[] classes = implClassSet.toArray(new Class[0]);
            return classes[0].getName();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            System.out.println("超过规定时间服务器仍未收到客户端的心跳或正常信息，关闭连接");
            ctx.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
