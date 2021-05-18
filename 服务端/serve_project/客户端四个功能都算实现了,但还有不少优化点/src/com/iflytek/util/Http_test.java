package com.iflytek.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Http_test {


	public static String doGet(String httpUrl){
        //链接
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        try {
            //创建连接
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            //设置连接超时时间
            connection.setReadTimeout(15000);
            //开始连接
            connection.connect();
            //获取响应数据
            if (connection.getResponseCode() == 200) {
                //获取返回的数据
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭远程连接
            connection.disconnect();
        }
        return result.toString();
    }

	public static ArrayList<String> GetTypes(int time)
	{
		JSONObject quest = new JSONObject();
		ArrayList<String> types = new ArrayList<String>();
		for(int i = 0;i<time;i++)
		{
			String temp = doGet("http://rainbow.ilibrary.me/api/rainbow/random");
			quest = JSON.parseObject(temp);
			String feature = quest.getString("feature_list");
			System.out.println(feature);
			feature = feature.substring(1, feature.length()-1);
			
			String TempStr[] = feature.split(",");
			
			for(int j = 0;j <TempStr.length; j++)
			{
				if(!types.contains(TempStr[j]))
				{
					types.add(TempStr[j]);
				}
			}
			
		}
		return types;
	}
	public static String GetJuzi(String Type)// 获取指定类型的句子
	{
		String result = "";
		if(Type != "")
			Type = "\"" + Type + "\"";
		int flag = 1;
		while(flag == 1) {
			
			JSONObject quest = new JSONObject();
			String temp = doGet("http://rainbow.ilibrary.me/api/rainbow/random");
			quest = JSON.parseObject(temp);
			String feature = quest.getString("feature_list");
			
			feature = feature.substring(1, feature.length()-1);
			String TempStr[] = feature.split(",");
			for(int j = 0;j <TempStr.length; j++)
			{
				
				if(TempStr[j].equals(Type))
				{
					result = quest.getString("sentence");
					flag = 0;
				}
			}
			
		}
		return result;
		
		
	}
	public static String GetJuziNovel()// 获取小说的句子 https://v1.hitokoto.cn/?c=d
	{
		String result = "";
		int max_len =30;
		String req = "https://v1.hitokoto.cn/?" + "c=d";
		JSONObject quest = new JSONObject();
		boolean flag = true;
		while(flag)
		{
			String temp = doGet(req);
			quest = JSON.parseObject(temp);
			//System.out.println(quest);
			if(quest == null)
			{
				continue;
			}
			result = quest.getString("hitokoto") + "《" + quest.getString("from") +"》";
			if(result.length()<max_len || (quest.getIntValue("reviewer")>2000))
			{
				flag = false;
			}
		}
		
			
		return result;
		
		
	}
	public static String GetJuziType(String Type)// 获取指定类型的句子 https://v1.hitokoto.cn/?c=d
	{
		String result = "";
		int max_len =50;
		String req = "https://v1.hitokoto.cn/?" + "c=" + Type;
		JSONObject quest = new JSONObject();
		boolean flag = true;
		while(flag)
		{
			String temp = doGet(req);
			quest = JSON.parseObject(temp);
			//System.out.println(quest);
			if(quest == null)
			{
				continue;
			}
			result = quest.getString("hitokoto") + " 《" + quest.getString("from") +"》";
			if(result.length()<max_len && (quest.getIntValue("reviewer")>1000))
			{
				flag = false;
			}
		}
		
			
		return result;
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//ArrayList<String> types = GetTypes(20000);
		//System.out.print(types);
		//System.out.print(GetJuzi(""));
		for(int i=0;i<10;i++)
		{
			String result = GetJuziNovel();
			System.out.println(result);
		}
		

	}

}
