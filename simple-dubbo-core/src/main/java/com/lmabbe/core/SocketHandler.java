package com.lmabbe.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketHandler {
    private SocketHandler() {

    }

    private Configuration configuration;


    public SocketHandler(Configuration configuration) {
        this.configuration = configuration;
        if (configuration.isProvider()) {
            new Thread(() -> {
                createNioServer();
            }).start();
        }
    }

    private void createNioServer() {
        try {
            // 监听指定的端口
            ServerSocket server = new ServerSocket(configuration.getProtocol().getPort());
            while (true) {
                // server将一直等待连接的到来
                Socket socket = server.accept();
                // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
                InputStream inputStream = socket.getInputStream();
                byte[] bytes = new byte[1024];
                int len;
                StringBuilder sb = new StringBuilder();
                //只有当客户端关闭它的输出流的时候，服务端才能取得结尾的-1
                while ((len = inputStream.read(bytes)) != -1) {
                    // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                    sb.append(new String(bytes, 0, len, "UTF-8"));
                }
                String queryUrl = sb.toString();
                String[] query = queryUrl.split("&");
                String classAndMethod = query[0];
                String[] classAndMethodInfo = classAndMethod.split("#");
                String args[] = query[1].split(",");

                //调用方法
                String returnMsg = "";
                if (configuration.getBean().getId().equals(classAndMethodInfo[0])) {
                    String method = classAndMethodInfo[1];
                    Class<?> clazz = Class.forName(configuration.getBean().getClazz());
                    Method[] a = clazz.getMethods();
                    for (Method methodInfo : a) {
                        if (methodInfo.getName().equals(method)) {
                            returnMsg = methodInfo.invoke(clazz.newInstance(), args).toString();
                        }
                    }
                }
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(returnMsg.getBytes("UTF-8"));
                inputStream.close();
                outputStream.close();
                socket.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static String sendMessage(String message) {
        try {
            String nodeHost = ProviderNodes.getNodes();
            // 要连接的服务端IP地址和端口
            String hostInfo[] = nodeHost.split(":");
            // 与服务端建立连接
            Socket socket = new Socket(hostInfo[0], Integer.parseInt(hostInfo[1]));
            // 建立连接后获得输出流
            OutputStream outputStream = socket.getOutputStream();
            socket.getOutputStream().write(message.getBytes("UTF-8"));
            //通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
            socket.shutdownOutput();
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                sb.append(new String(bytes, 0, len, "UTF-8"));
            }
            inputStream.close();
            outputStream.close();
            socket.close();
            return sb.toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}