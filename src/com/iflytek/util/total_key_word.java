package com.iflytek.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class total_key_word {
	static String recent_name_path = "C:\\Users\\24349\\eclipse-workspace\\voice_serve1.0\\data\\";//存储pcm转字符串记录的路径
	static String pcmdata_path = "C:\\\\Users\\\\24349\\\\eclipse-workspace\\\\voice_serve1.0\\\\pcmdata\\\\";//存储pcm文件的路径
	static String txtdata_path = "C:\\\\Users\\\\24349\\\\eclipse-workspace\\\\voice_serve1.0\\\\txtdata\\\\";//存储string字符串文件的路径
	static String keydata_path = "C:\\\\Users\\\\24349\\\\eclipse-workspace\\\\voice_serve1.0\\\\keydata\\\\";//存储string字符串文件的路径
	static key_word kword=new key_word();
	final static String appid = "5fed5ca2";
	final static String api_key = "31c6c1836c5c2946fdf18ca5a9fddc5d";
	static boolean flag;
	public static void start() throws IOException{
		//使用默认值
		System.out.println("关键字提取线程开始");
		String recentname ="";
		while(true)
		{
			recentname = get_recent_strname(recent_name_path);
			flag = str_zhuanhuan(recentname);
			if(flag)
				System.out.println(" 等待新的str文件进行提取");
		}
	}
	static boolean str_zhuanhuan(String recentname) throws UnsupportedEncodingException//判断是否这个文件以经是最新文件
	{
		boolean flag = false;
		String str_re = null;
		String path = txtdata_path;
		int nowtime = Integer.parseInt(recentname);
	    File file = new File(path);
	    String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
	    
		for(int i = 0; i < fileNameLists.length; i ++){
			String[] temp = fileNameLists[i].split("\\.");// .号需要进行转义  加上右双斜杠
			int x= Integer.parseInt(temp[0]);
			//System.out.println(nowtime + "另一个" + x);
			if(nowtime < x)
			{
				flag = true;
				System.out.println("开始对"+x+ "进行关键字提取");
				kword.get_keyword(fileNameLists[i],appid,api_key,path,x+".txt",keydata_path);
				write_recent_name(recent_name_path,""+x);//该进行记录的回写
				nowtime = x;

				//转换后内容的写入
			}
		}
		return flag;
	}
	
	private static void write_string(String path,String data,String name)
	{
		String filename = path + name;
		try {
            File writeName = new File(filename); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
            	String[] temp = data.split(" ");// .号需要进行转义  加上右双斜杠
            	for(int i=0;i<temp.length;i++)
            	{
            		out.write(temp[i]+"\r\n"); // \r\n即为换行
            	}
                
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	private static void write_recent_name(String path,String data)
	{
		String filename = path + "str_record.txt";
		try {
            File writeName = new File(filename); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(data+"\r\n"); // \r\n即为换行
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	private static String get_recent_strname(String path)
	{
		String result ="";
		String filename = path + "str_record.txt";//记录最近转换完成的文件名
		BufferedReader reader = null;
        try {

        	reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
            // 一次读入一行，直到读入null为文件结束
            result = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
		return result;
	}
}
