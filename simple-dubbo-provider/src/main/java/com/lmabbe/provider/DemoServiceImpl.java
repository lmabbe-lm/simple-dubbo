package com.lmabbe.provider;


import com.lmabbe.provider.interfaces.DemoService;

public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
        return "Hello " + name;
    }
}