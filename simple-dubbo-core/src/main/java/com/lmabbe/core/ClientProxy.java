package com.lmabbe.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ClientProxy implements InvocationHandler {

    private Object target;
    private String beanName;

    public ClientProxy(Object target, String beanName) {
        this.target = target;
        this.beanName = beanName;
    }

    /**
     * 得到被代理对象;
     */
    public static <T> Object getProxy(Class<T> clazz, String beanName) {
        return Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz}, new ClientProxy(clazz, beanName));
    }

    /**
     * 调用此方法执行
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        String keyName = beanName + "#" + method.getName();

        String argsStr = "";
        for (int i = 0; i < args.length; i++) {
            argsStr += args[i].toString() + ",";
        }
        keyName = keyName + "&" + argsStr;
        if (keyName.lastIndexOf(",") == keyName.length() - 1) {
            keyName = keyName.substring(0, keyName.length() - 1);
        }
        return SocketHandler.sendMessage(keyName);
    }
}
