package com.iflytek.util;

import java.net.Socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class deal_quest {
	static send_recive sandr = new send_recive();
	public static void deal(JSONObject object,Socket socket) //处理所有的请求
	{
		int from,time,k;
		String id;
		String order;
		from = object.getIntValue("from");
		id = object.getString("ID");
		order = object.getString("order");
		time = object.getIntValue("time");
		k = object.getIntValue("k");
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
			
			
		}
		if(from==1)
		{
			
			System.out.println("这是来自单片机的请求");
		}
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

