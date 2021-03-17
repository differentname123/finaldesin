package com.iflytek.util;

import java.io.File;

import com.alibaba.fastjson.JSONObject;

public class get_recent_xinqing {
	
	private static final String celue2path = "./celue2";
	static File_ways file_ways = new File_ways();//声明一个文件处理对象
	public static String get_xinqing(int time)
	{
		int count=0;
		JSONObject object = new JSONObject();
		String curTime = System.currentTimeMillis() / 1000L + "";
		int endtime = Integer.parseInt(curTime);
		int starttime = endtime - time;
		 File file = new File(celue2path);
		 String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
		 if(fileNameLists==null)
			 return null;
		 
			for(int i = 0; i < fileNameLists.length; i ++){
				String[] temp = fileNameLists[i].split("\\.");// .号需要进行转义  加上右双斜杠
				int x= Integer.parseInt(temp[0]);
				if(x>=starttime)
				{
					JSONObject tempjson = new JSONObject();
					String temp1 = file_ways.get_recent_name(celue2path, x+".txt");
					tempjson.put("time", x);
					tempjson.put("zhi", temp1);
					object.put(""+count, tempjson);
					count++;
					
				}
			}
			object.put("linecount", count);
		return object.toString();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String curTime = System.currentTimeMillis() / 1000L + "";
		int Timename = Integer.parseInt(curTime);
		System.out.println(get_xinqing(Timename));

	}

}