package com.iflytek.util;

import java.io.File;

public class suggest {
	private static final String FIX_DATA_PATH = "./fix_path/关键词推荐数据";
	// 返回推荐的描述字符串
	public static String get_sug_str(String type)
	{
		 String result = "";
		 File_ways file_ways= new File_ways();
		 File file = new File(FIX_DATA_PATH + "//" + type);
		 String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
		 int index = (int)(Math.random()*fileNameLists.length);
		 result = file_ways.get_recent_name(FIX_DATA_PATH + "//" + type + "//" + fileNameLists[index], "txt.txt");
		 System.out.println(result);
		 return result;
	}
	// 返回待发送文件夹的路径
	public static String get_random_name(String type)
	{
		String result = "";
		 File file = new File(FIX_DATA_PATH + "//" + type);
		 String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
		 int index = (int)(Math.random()*fileNameLists.length);
		 result = FIX_DATA_PATH + "//" + type + "//" + fileNameLists[index];
		 return result;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		get_sug_str("电影");

	}

}
