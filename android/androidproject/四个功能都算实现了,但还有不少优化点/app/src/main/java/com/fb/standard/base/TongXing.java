package com.fb.standard.base;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
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
	private static String DEFAULT_SERVER_IP = "192.168.108.115";
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
	public static String produce_juzi_order(String order,String ID,int time, int k, String data)//生成指定请求帧
	{
		JSONObject jsonobject = new JSONObject();
		jsonobject.put("from", 0);
		jsonobject.put("ID", ID);
		jsonobject.put("order", order);
		jsonobject.put("time", time);
		jsonobject.put("k", k);
		jsonobject.put("data", data);
		return jsonobject.toString();
	}
	public Bitmap qiuqiuni(String type) throws IOException {
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_juzi_order("216","123",0,0,type);
		send_str(socket,quest);
		DataInputStream dataInput = new DataInputStream(socket.getInputStream());
		int size = dataInput.readInt();
		byte[] bs = new byte[size];
		dataInput.read(bs);

		YuvImage yuvimage=new YuvImage(bs, ImageFormat.NV21, 750,1000, null);//20、20分别是图的宽度与高度
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		yuvimage.compressToJpeg(new Rect(0, 0,750, 1000), 80, baos);//80--JPG图片的质量[0-100],100最高
		byte[] jdata = baos.toByteArray();

		ByteArrayOutputStream outPut = new ByteArrayOutputStream();
		Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
		dataInput.close();
		outPut.close();
		return bmp;
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
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, outPut);
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
		String result = "";
		String expression = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while((expression = in.readLine())!= null)
			{
				result += expression;
			}
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
		return result;
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

	public String get_sug(String type)
	{
		String result = "";
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_juzi_order("215","123",0,0,type);
		send_str(socket,quest);
		result = recive_str(socket);
		System.out.println("终端句子接收结果："+result);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public Bitmap get_sug_photo(String type)
	{
		Bitmap bitmap = null;
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_juzi_order("216","123",0,0,type);
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
	public static String get_juzi(String type)//获取指定类型的句子
	{

		String result = "";
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_juzi_order("217","123",0,0,type);
		send_str(socket,quest);
		result = recive_str(socket);
		System.out.println("终端句子接收结果："+result);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public static String get_novel_juzi()//获取指定小说类型的句子
	{

		String result = "";
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_juzi_order("218","123",0,0,"");
		send_str(socket,quest);
		result = recive_str(socket);
		System.out.println("终端句子接收结果："+result);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
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
	public static String get_report(int time)//获取指定时间段内的数据分析结果
	{
		String result = "";
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_order("213","123",time,0);
		send_str(socket,quest);
		result = recive_str(socket);
		System.out.println("终端分析数据接收结果：" + result);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static String get_sug_str(String key_word)//获取指定事情的推荐字符串
	{
		String result = "";
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_juzi_order("215","123",0,0, key_word);
		send_str(socket,quest);
		result = recive_str(socket);
		System.out.println("终端捕捉值接收结果："+result);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static String get_future(int time)//获取指定时间段内的捕捉到的事情
	{
		String result = "";
		Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		String quest = produce_order("212","123",time,0);
		send_str(socket,quest);
		result = recive_str(socket);

		System.out.println("终端捕捉值接收结果："+result);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
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
