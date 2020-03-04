package com.lmabbe.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Configuration {

    private String application;

    private String registry;

    private Protocol protocol;

    private Service service;

    private Reference reference;

    private Bean bean;

    @Data
    @AllArgsConstructor
    public static class Protocol {

        private String name;

        private Integer port;

    }

    @Data
    @AllArgsConstructor
    public static class Service {
        private String interfaces;
        private String ref;
    }

    @Data
    @AllArgsConstructor
    public static class Bean {

        private String id;

        private String clazz;

    }

    @Data
    @AllArgsConstructor
    public static class Reference {
        private String id;

        private String interfaces;
    }

    /**
     * 判断是不是提供者节点
     *
     * @return true 是 false 不是
     */
    public boolean isProvider() {
        return (this.getReference() == null);
    }
}







