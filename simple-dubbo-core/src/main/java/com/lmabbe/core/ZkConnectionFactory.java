package com.lmabbe.core;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZkConnectionFactory {

    private static CuratorFramework client = null;


    private ZkConnectionFactory() {
    }


    public static CuratorFramework getConnection(String url) {
        if (client == null) {
            client = CuratorFrameworkFactory.builder()
                    .connectString(url)
                    .sessionTimeoutMs(60 * 1000)
                    .connectionTimeoutMs(3 * 1000)
                    .retryPolicy(new ExponentialBackoffRetry(1000, 29, 60 * 1000))
                    .build();
            client.start();
        }
        return client;
    }
}
