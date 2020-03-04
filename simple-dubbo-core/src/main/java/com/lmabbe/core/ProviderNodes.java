package com.lmabbe.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProviderNodes {

    private static final Map<String, String> nodes = new HashMap<>();


    public static String getNodes() {
        if(nodes.isEmpty()){
            throw new RuntimeException("没有提供者节点");
        }
        int num = new Random().nextInt(nodes.size());
        String key = nodes.keySet().stream().toArray()[num].toString();
        return nodes.get(key);
    }

    public static void setNodes(String nodeName, String nodeHost) {
        nodes.put(nodeName, nodeHost);
    }
    public static void remove(String key){
        nodes.remove(key);
    }



}
