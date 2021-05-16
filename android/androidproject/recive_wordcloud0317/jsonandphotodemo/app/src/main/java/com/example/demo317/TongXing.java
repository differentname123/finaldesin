package com.example.demo317;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import net.sf.json.JSONObject;

public class TongXing {
	private static int DEFAULT_SERVER_PORT = 12345;
	private static String DEFAULT_SERVER_IP = "192.168.31.108";
	TCPSocket tcpsocket;

	{
		try {
			tcpsocket = new TCPSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
	public synchronized Bitmap recievephoto(Socket socket)
	{
		Bitmap bmp = null;
		int temp = 0;
		try {

			while(socket==null);
			DataInputStream dataInput = new DataInputStream(socket.getInputStream());
			int size = dataInput.readInt();
			Log.d("TCP1", String.valueOf(size));
			byte[] data = new byte[size];
			int len = 0;
			Log.d("TCP1", len+"");
			while (len < size) {
				temp = dataInput.read(data, len, size - len);
				if(temp<0)
					break;
				len += temp;
				Log.d("TCP1", len+"");
			}
			ByteArrayOutputStream outPut = new ByteArrayOutputStream();
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			Log.d("TCP1", bmp.toString());
			bmp.compress(Bitmap.CompressFormat.PNG, 100, outPut);
			dataInput.close();
			outPut.close();


		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d("TCP6", String.valueOf(bmp));
		return bmp;

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


	public  Bitmap get_word_cloud(int time)//获取指定时间段内的词云图
	{
		Bitmap bitmap = null;
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);

		String quest = produce_order("214","123",time,100);
		send_str(socket,quest);
		bitmap = recievephoto(socket);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
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
	public  String get_xinqing(int time)//获取指定时间段内的心情值
	{
		String result = "";
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_order("211","123",time,0);
		send_str(socket,quest);
		result = recive_str(socket) ;

		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
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
		String curTime = System.currentTimeMillis() / 1000L + "";
		int Timename = Integer.parseInt(curTime);
		get_topk(Timename,3);
		
		
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
	}

}