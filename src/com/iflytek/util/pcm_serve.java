package com.iflytek.util;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by YUAN on 2016-09-17.
 */
public class pcm_serve {
    
    private ServerSocket server;
    private int port = 5020;

    public pcm_serve() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
        }
    }
    
    public void pcmsave(String path,String filename) {
        System.out.println("监控端口：" + port);
        Socket socket = null;
        while (true) {
            try {
                // 阻塞等待，每接收到一个请求就创建一个新的连接实例
                socket = server.accept();
                System.out.println("连接客户端地址：" + socket.getRemoteSocketAddress());

                // 装饰流BufferedReader封装输入流（接收客户端的流）
                BufferedInputStream bis = new BufferedInputStream(
                        socket.getInputStream());

                DataInputStream dis = new DataInputStream(bis);//用于读取客户端发来的pcm数据
                byte[] bytes = new byte[1]; // 一次读取一个byte
                String ret = "";
                
        		File file = new File(path+filename);
        		if(file.exists())
        		{
        			file.delete();
        		}// 创建新文件,有同名的文件的话直接覆盖
        		file.createNewFile();
        		
        		
        		
                FileOutputStream fs =new FileOutputStream(path+filename);
                DataOutputStream da = new DataOutputStream(fs);//用于写入收到的pcm数据
                
                while (dis.read(bytes) != -1) {
                	da.write(bytes);
                    ret += bytesToHexString(bytes) + " ";
                }
                
                
                doSomething(ret);
                System.out.println("接收完成：" + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    }
    
    public void talk() {
        System.out.println("监控端口：" + port);
        Socket socket = null;
        while (true) {
            try {
                // 阻塞等待，每接收到一个请求就创建一个新的连接实例
                socket = server.accept();
                System.out.println("连接客户端地址：" + socket.getRemoteSocketAddress());

                // 装饰流BufferedReader封装输入流（接收客户端的流）
                BufferedInputStream bis = new BufferedInputStream(
                        socket.getInputStream());

                DataInputStream dis = new DataInputStream(bis);
                byte[] bytes = new byte[1]; // 一次读取一个byte
                String ret = "";
                FileOutputStream fs =new FileOutputStream("C:\\Users\\24349\\eclipse-workspace\\voice_serve1.0\\test\\to.pcm");
                DataOutputStream da = new DataOutputStream(fs);
                
                while (dis.read(bytes) != -1) {
                	da.write(bytes);
                    ret += bytesToHexString(bytes) + " ";
                }
                doSomething(ret);
                System.out.println("接收完成：" + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    }
    
    public static void doSomething(String ret) {
        System.out.println(ret);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String BytesHexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static void main(String[] args) {
        pcm_serve server = new pcm_serve();
        server.pcmsave("C:\\Users\\24349\\eclipse-workspace\\voice_serve1.0\\test\\","111.txt");
    }
}