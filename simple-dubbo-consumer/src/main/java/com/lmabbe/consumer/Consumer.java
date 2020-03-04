package com.lmabbe.consumer;

import com.lmabbe.consumer.interfaces.DemoService;
import com.lmabbe.core.SimpleDubboStart;

public class Consumer {
    public static void main(String[] args) throws Exception {
        SimpleDubboStart sm = new SimpleDubboStart("/consumer.xml");
        System.out.println("启动完成");

        long start = System.currentTimeMillis();
        DemoService service = (DemoService) sm.getBean("demoService");
        long end = System.currentTimeMillis();

        System.out.println(end-start);
        System.in.read(); // 按任意键退出*/
    }
}