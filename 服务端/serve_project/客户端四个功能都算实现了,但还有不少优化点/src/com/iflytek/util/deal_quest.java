package com.iflytek.util;

import java.net.Socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class deal_quest {
	static String WAV_ORIGIN_DATA_PATH = "./wav_origin_data";//存储原始文件的路径
	static send_recive sandr = new send_recive();
	public static void deal(JSONObject object,Socket socket) throws Exception //处理所有的请求
	{
		int from,time,k;
		String id, data;
		String order;
		from = object.getIntValue("from");
		id = object.getString("ID");
		order = object.getString("order");
		time = object.getIntValue("time");
		k = object.getIntValue("k");
		data = object.getString("data");
		// 请求来自客户端
		if(from==0)
		{
			System.out.println("这是来自终端的请求");
			if(order.equals("210"))
			{
				sandr.return_keyword(time,k,socket);
			}
			if(order.equals("211"))
			{
				sandr.return_xinqing(time,socket);
			}
			if(order.equals("212"))
			{
				sandr.return_future(time,socket);
			}
			
			if(order.equals("213"))
			{
				sandr.return_report(time,socket);
			}
			if(order.equals("214"))
			{
				sandr.return_word_cloud(time,socket);
			}
			if(order.equals("215"))//表示客户端向云端访问指定事情的推荐描述
			{
				sandr.return_sug(data,socket);
			}
			
			if(order.equals("216"))//表示客户端向云端访问指定事情的推荐图片
			{
				sandr.return_sug_photo(data,socket);
			}
			if(order.equals("217"))//表示客户端向云端访问指定类型句子
			{
				sandr.return_juzi(data,socket);
			}
			if(order.equals("218"))//表示客户端向云端访问小说类的名言
			{
				sandr.return_novel_juzi(socket);
			}
		}
		// 请求来自感知层
		if(from==1)
		{
			// 010 代表单片机向服务端请求时间戳
			if(order.equals("010"))
			{
				System.out.print(object);
				sandr.return_now__time(socket);
			}
			
			// 011 代表单片机向服务端发送串口文件
			if (order.equals("011")) {
				
				System.out.print(object);
				String temp[] = data.split("/");
				sandr.recive_wav(socket, temp[temp.length - 1] + ".txt", WAV_ORIGIN_DATA_PATH);
			}
			//012 代表单片机向服务端询问当前的发送文件名
			if (order.equals("012")) {
				
				System.out.print(object);
				sandr.return_wav_time(socket);
			}
			System.out.println("这是来自单片机的请求");
		}
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

