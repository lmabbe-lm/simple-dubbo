package com.lmabbe.provider;

import com.lmabbe.core.SimpleDubboStart;

public class Provider {
    public static void main(String[] args) throws Exception {
        new SimpleDubboStart("/provider.xml");
        System.out.println("启动完成");
        System.in.read(); // 按任意键退出

    }
}