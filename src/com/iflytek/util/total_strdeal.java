package com.iflytek.util;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class total_strdeal {
	// webapi接口地址
	private static final String FIX_PATH = "./fix_path";// 不能改变的文件夹
	private static final String BUZUO_NAME = "关键词匹配库.txt";
	static String URL = "http://ltpapi.xfyun.cn/v1/";
	private static final String WEBTTS_URL = "http://ltpapi.xfyun.cn/v1/pos";
	// 应用ID
	private static final String APPID = "5fed5ca2";
	// 接口密钥
	private static final String API_KEY = "31c6c1836c5c2946fdf18ca5a9fddc5d";
	// 文本
	private static final String TEXT = "我要去看电影.";
	

	private static final String TYPE = "dependent";
	
	public static String manyfun(String fun,String str)  //通过fun参数的不同，调用不同的api对str进行处理
	{
		String result= "";
		String local = URL + fun ;
		Map<String, String> header = null;
		try {
			header = buildHttpHeader();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			result = HttpUtil.doPost1(local, header, "text=" + URLEncoder.encode(str, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public static String[] cws(String str)//对指定字符串str进行中文划分，返回每个词
	{
		String result= "";
		JSONObject object = new JSONObject();
		result = manyfun("cws",str);
		object = JSON.parseObject(result);
		String data = object.getString("data");
		//System.out.println(data);
		if (data == "")
		{
			return null;
		}
		JSONObject dataobject = new JSONObject();
		
		dataobject = JSON.parseObject(data);
		String word = dataobject.getString("word");
		//System.out.println(word);
		
		if(word == null)
			return null;
		String temp[] = word.split("\"");
		String xx ="";
		for(int i=0;i<temp.length;i++)
		{
			xx+=temp[i];
			//System.out.print(temp[i]);
		}
		xx = xx.substring(1,xx.length()-1);
		
		String finaltemp[] = xx.split(","); 
		for(int i =0 ;i<finaltemp.length;i++)
		{
			System.out.print(" "+finaltemp[i]);
		}
		System.out.print("\n");
		return finaltemp;		
	}
	
	public static String[] pos(String str)//对指定字符串str进行词性标注，返回每个词的词性
	{
		String result= "";
		JSONObject object = new JSONObject();
		result = manyfun("pos",str);
		object = JSON.parseObject(result);
		String data = object.getString("data");
		//System.out.println(data);
		
		JSONObject dataobject = new JSONObject();
		
		dataobject = JSON.parseObject(data);
		String word = dataobject.getString("pos");
		//System.out.println(word);
		
		
		String temp[] = word.split("\"");
		String xx ="";
		for(int i=0;i<temp.length;i++)
		{
			xx+=temp[i];
			//System.out.print(temp[i]);
		}
		xx = xx.substring(1,xx.length()-1);
		
		String finaltemp[] = xx.split(","); 
		for(int i =0 ;i<finaltemp.length;i++)
		{
			System.out.print(" "+finaltemp[i]);
		}
		System.out.print("\n");
		return finaltemp;		
	}
	
	public  String sa(String str)//对指定字符串str进行情感分析，返回每个词的词性
	{
		String result= "";
		JSONObject object = new JSONObject();
		
		String local = "http://ltpapi.xfyun.cn/v2/" + "sa" ;
		Map<String, String> header = null;
		try {
			header = buildHttpHeader();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			result = HttpUtil.doPost1(local, header, "text=" + URLEncoder.encode(str, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		object = JSON.parseObject(result);
		String data = object.getString("data");
		System.out.println(result);
		
		JSONObject dataobject = new JSONObject();
		
		dataobject = JSON.parseObject(data);
		String word = dataobject.getString("score");
		//System.out.println(word);
		return data;		
	}
	public  void Judge2(String str)
	{
		String result = sa(str);
		JSONObject object = new JSONObject();
		object = JSON.parseObject(result);
		String score = object.getString("score");
		String sentiment = object.getString("sentiment");
		
		System.out.println(score+" "+sentiment);
		
	}

	// 进行 关键词捕捉 通过和捕捉库比较的方法得到
	public String BuZhuo(String str)
	{
		String result = "";
		File_ways file_ways = new File_ways();
		String tempStr = file_ways.get_recent_name(FIX_PATH, BUZUO_NAME);
		String TempStr[] = tempStr.split(" ");
		for(int i = 0;i<TempStr.length; i++)
		{
			if(str.contains(TempStr[i]))
			{
				result = TempStr[i];
				break;
			}
				
		}
		return result;
	}
	public String Judge1(String str)//实现策略一匹配找出未来将要做的事情
	{
		String result = "";
		int i = 0;
		int flag = 0 ;
		String cws[] = cws(str);
		String pos[] = pos(str);
		for(i=0;i<cws.length;i++)
		{
			switch(flag)
			{
				case 0:
					if(pos[i].equals("r"))
					{
						if(cws[i].equals("我"))
							flag = 1;
					}
				break;
				
				case 1:
					if(pos[i].equals("v"))
					{
						if(cws[i].equals("要")||cws[i].equals("想"))
							flag = 2;
					}
				break;
				
				
				case 2:
					if(pos[i].equals("n")||pos[i].equals("v"))
					{
						if(cws[i].length()>1)
						{
							result = cws[i];
							flag = 3;
						}

					}
					
				break;
				
			}
			if(3 == flag)
			{
				
				break;
			}
				

		}
		
		return result;
	}
	public static void main(String[] args) throws IOException {
		//System.out.println(Judge1("我要飞得更高"));
		//Judge2("我爱你");
	}

	/**
	 * 组装http请求头
	 */
	private static Map<String, String> buildHttpHeader() throws UnsupportedEncodingException {
		String curTime = System.currentTimeMillis() / 1000L + "";
		String param = "{\"type\":\"" + TYPE +"\"}";
		String paramBase64 = new String(Base64.encodeBase64(param.getBytes("UTF-8")));
		String checkSum = DigestUtils.md5Hex(API_KEY + curTime + paramBase64);
		Map<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		header.put("X-Param", paramBase64);
		header.put("X-CurTime", curTime);
		header.put("X-CheckSum", checkSum);
		header.put("X-Appid", APPID);
		return header;
	}
}

