package com.iflytek.util;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;

public class send_recive {

	
	
	public static void return_keyword(int time,int k,Socket socket)//向终端以降序json格式发送关键字
	{
		Map<String, Double> hashMap = get_recent_top_words.get_top_words(time, k);
		JSONObject jsonobject = new JSONObject();
		 Set<Entry<String, Double>> set = hashMap.entrySet();
	        Iterator<Entry<String, Double>> iterator = set.iterator();
	        while(iterator.hasNext()) {
	            Entry<String, Double> entry = iterator.next();
	            String key = (String) entry.getKey();
	            double value =entry.getValue();
	            jsonobject.put(key, value);
	        }
		System.out.println(jsonobject);
		send_str(socket,jsonobject.toString());
		
	}
	public static void return_xinqing(int time,Socket socket)//向终端以json嵌套json格式发送关键字
	{
		String result = get_recent_xinqing.get_xinqing(time);
		send_str(socket,result);
		
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
		
		if(out != null){
			out.close();
			out = null;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		//send_str(socket,"number1");

	}

}
