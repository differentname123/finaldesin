package com.iflytek.util;

import java.io.File;

public class suggest {
	private static final String FIX_DATA_PATH = "./fix_path/关键词推荐数据";
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		get_sug_str("电影");

	}

}
