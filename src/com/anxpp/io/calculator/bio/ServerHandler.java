package com.anxpp.io.calculator.bio;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.alibaba.fastjson.JSON;
import com.iflytek.util.Voice_to_String;
import com.iflytek.util.deal_quest;
import com.iflytek.util.key_word;



public class ServerHandler implements Runnable{
	private Socket socket;
	deal_quest deal = new deal_quest();
	public ServerHandler(Socket socket) {
		this.socket = socket;
	}
	final String appid = "5fed5ca2";
	final String api_key = "31c6c1836c5c2946fdf18ca5a9fddc5d";
	String fileName="test.pcm";
	public String FixExpress(String str) {
		int len = str.length();
		if (str.charAt(len-1) !='}' )
		{
			str+="\"";
			str+="}";
		}
		return str; 
		
	}
	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			String expression;
			
				expression = in.readLine();
				
				System.out.println("收到的信息为" + expression);
				expression = FixExpress(expression);
				JSONObject quest = new JSONObject();
				quest = JSON.parseObject(expression);
				deal_quest.deal(quest, socket);
				//完成文件接收
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
			if(out != null){
				out.close();
				out = null;
			}
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				socket = null;
			}
		}
	}
}