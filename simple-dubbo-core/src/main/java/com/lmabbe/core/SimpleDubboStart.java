package com.lmabbe.core;


public class SimpleDubboStart {


    private Configuration configuration;


    private SimpleDubboStart() {

    }


    public SimpleDubboStart(String configPath) {
        //1.读取配置文件
        XmlParserHandler handler = new XmlParserHandler(configPath);
        configuration = handler.parser();
        //2.向服务中心注册
        //此处应该用适配器，但是我不用
        new RegisterZk(configuration);
        //建立socker连接
        new SocketHandler(configuration);
    }

    public Object getBean(String beanName) {
        if (configuration.getReference().getId().equals(beanName)) {
            try {
                return ClientProxy.getProxy(Class.forName(configuration.getReference().getInterfaces()),beanName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
