package com.lmabbe.core;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.CreateMode;

import java.security.Provider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegisterZk {

    public static final String ROOT_NODE = "/simple_zookeeper";

    public static final String CONSUMER_NODE = ROOT_NODE + "/consumer";

    public static final String PROVIDER_NODE = ROOT_NODE + "/provider";

    private Configuration configuration;

    private CuratorFramework client;

    private Map<String, String> nodesMap = new HashMap<String, String>();

    private RegisterZk() {
    }

    public RegisterZk(Configuration configuration) {
        this.configuration = configuration;
        String registryUrl = configuration.getRegistry();

        if (configuration.getRegistry() == null || configuration.getRegistry() == "") {
            throw new RuntimeException("注册信息为空");
        }

        //registerInfo[0] 是注册中心类型
        //registerInfo[1] 是注册中心地址
        String registerInfo[] = registryUrl.split("://");
        if (registerInfo.length != 2) {
            throw new RuntimeException("注册信息格式错误");
        }

        String url = registerInfo[1];
        this.client = ZkConnectionFactory.getConnection(url);

        handlerNodes();
        if (!configuration.isProvider()) {
            getProviderNodeList();
            watchProviderNode();

        }

    }

    private void watchProviderNode() {
        try {
            client.getChildren().usingWatcher((CuratorWatcher) event -> {
                String nodePath = event.getPath();
                String nodeName = nodePath.substring(nodePath.lastIndexOf("/"));
                ProviderNodes.remove(nodeName);
            }).forPath(PROVIDER_NODE);
        } catch (Exception e) {
            throw new RuntimeException("节点监控异常");
        }
    }

    private void getProviderNodeList() {
        try {
            Iterator it = client.getChildren().forPath(PROVIDER_NODE).iterator();
            while (it.hasNext()) {
                String nodeName = it.next().toString();
                System.out.println("nodeName:"+nodeName);
                String host = new String(client.getData().forPath(PROVIDER_NODE + "/" + nodeName));
                ProviderNodes.setNodes(nodeName, host);
            }
        } catch (Exception e) {
            throw new RuntimeException("获取提供者节点异常");
        }

    }

    private void handlerNodes() {
        try {
            if (client.checkExists().forPath(ROOT_NODE) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(ROOT_NODE);
            }
            if (configuration.isProvider() && client.checkExists().forPath(PROVIDER_NODE) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(PROVIDER_NODE);
            }
            if (!(configuration.isProvider()) && client.checkExists().forPath(CONSUMER_NODE) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(CONSUMER_NODE);
            }
            if (configuration.isProvider()) {
                //添加application作为临时顺序节点，为负载均衡做准备
                String value = "127.0.0.1:" + configuration.getProtocol().getPort();
                client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(PROVIDER_NODE + "/" + configuration.getApplication(), value.getBytes());
            } else {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(CONSUMER_NODE + "/" + configuration.getApplication());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
