package com.anxpp.io.calculator.bio;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.iflytek.util.Voice_to_String;
import com.iflytek.util.key_word;


public class ServerHandler implements Runnable{
	private Socket socket;
	public ServerHandler(Socket socket) {
		this.socket = socket;
	}
	final String appid = "5fed5ca2";
	final String api_key = "31c6c1836c5c2946fdf18ca5a9fddc5d";
	String fileName="test.pcm";
	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			String expression;
			while(true){
				if((expression = in.readLine())==null) break;
				
				System.out.println("收到的信息为" + expression);
				//完成文件接收

				//进行转文字操作

				//提取关键字
				key_word kword=new key_word();
				//System.out.println("测试结果"+kword.get_keyword("aa.txt", appid, api_key));
			}
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