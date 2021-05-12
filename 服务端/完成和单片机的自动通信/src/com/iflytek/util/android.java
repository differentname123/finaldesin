package com.iflytek.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.alibaba.fastjson.JSONObject;

public class android {
	private static int DEFAULT_SERVER_PORT = 12345;
	private static String DEFAULT_SERVER_IP = "127.0.0.1";
	public static String produce_order(String order,String ID,int time, int k)//生成指定请求帧
	{
		JSONObject jsonobject = new JSONObject();
		jsonobject.put("from", 0);
		jsonobject.put("ID", ID);
		jsonobject.put("order", order);
		jsonobject.put("time", time);
		jsonobject.put("k", k);
		return jsonobject.toString();
	}
	public static String recive_str(Socket socket)
	{
		String expression = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			expression = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return expression;
	}
	
    public static int recive_photo(String path,String filename,Socket socket) {
      
        while (true) {
            try {

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
                }
              
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return 0;
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    return 0;
                }
            }
        }

    }
	public static void send_str(Socket socket,String data)
	{
		PrintWriter out = null;
		try {
			out = new PrintWriter(socket.getOutputStream(),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println(data);
		
	}
	public static Socket build_link(String ip,int port) //和指定ip端口建立连接，并且返回套接字
	{
		Socket socket = null;
		try {
			socket = new Socket(ip,port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return socket;
	}
	public static void get_word_cloud(int time)//获取指定时间段内的前k个关键词
	{
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_order("214","123",time,100);
		send_str(socket,quest);
		recive_photo("S:\\","areyou.png",socket);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void get_topk(int time,int k)//获取指定时间段内的前k个关键词
	{
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_order("210","123",time,k);
		send_str(socket,quest);
		System.out.println("终端关键词接收结果："+recive_str(socket));
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void get_xinqing(int time)//获取指定时间段内的心情值
	{
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_order("211","123",time,0);
		send_str(socket,quest);
		System.out.println("终端心情值接收结果："+recive_str(socket));
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void get_report(int time)//获取指定时间段内的数据分析结果
	{
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_order("213","123",time,0);
		send_str(socket,quest);
		System.out.println("终端分析数据接收结果："+recive_str(socket));
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void get_future(int time)//获取指定时间段内的捕捉到的事情
	{
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_order("212","123",time,0);
		send_str(socket,quest);
		System.out.println("终端捕捉值接收结果："+recive_str(socket));
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		  //终端向云端访问关键词用例
		/*
		String curTime = System.currentTimeMillis() / 1000L + "";
		int Timename = Integer.parseInt(curTime);
		get_topk(Timename,3);
		*/
		
		
		/* // 终端向云端访问心情值用例
		String curTime = System.currentTimeMillis() / 1000L + "";
		int Timename = Integer.parseInt(curTime);
		get_xinqing(Timename);
		*/
		
		/* // 终端向云端访问捕捉值用例
		String curTime = System.currentTimeMillis() / 1000L + "";
		int Timename = Integer.parseInt(curTime);
		get_future(Timename);
		*/
		
		/*
		//获取指定时间段内的数据分析结果用例
		String curTime = System.currentTimeMillis() / 1000L + "";
		int Timename = Integer.parseInt(curTime);
		get_report(Timename);
		*/
		//获取指定时间段内的词云图
		String curTime = System.currentTimeMillis() / 1000L + "";
		int Timename = Integer.parseInt(curTime);
		get_word_cloud(Timename);
	}

}
