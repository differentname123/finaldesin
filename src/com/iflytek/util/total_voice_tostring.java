package com.iflytek.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class total_voice_tostring {
	static String APPID="5fed5ca2";
	///规范文件路径的命名
	static String recent_name_path = "C:\\Users\\24349\\eclipse-workspace\\voice_serve1.0\\data\\";//存储pcm转字符串记录的路径
	static String pcmdata_path = "C:\\\\Users\\\\24349\\\\eclipse-workspace\\\\voice_serve1.0\\\\pcmdata\\\\";//存储pcm文件的路径
	static String txtdata_path = "C:\\\\Users\\\\24349\\\\eclipse-workspace\\\\voice_serve1.0\\\\txtdata\\\\";//存储string字符串文件的路径
	public static void start() throws IOException{
		//使用默认值
		System.out.println("pcm转String线程开始");
		String recentname ="";
		boolean flag;
		while(true)
		{
			recentname = get_recent_name(recent_name_path);
			flag = judge_zhuanhuan(recentname);
			if(flag)
				System.out.println(" 等待新的pcm文件进行转换");
		}
		
	}
	
	static boolean judge_zhuanhuan(String recentname)//判断是否这个文件以经是最新文件
	{
		boolean flag = false;
		String str_re;
		String path = pcmdata_path;
		int nowtime = Integer.parseInt(recentname);
	    File file = new File(path);
	    String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
	    
		for(int i = 0; i < fileNameLists.length; i ++){
			String[] temp = fileNameLists[i].split("\\.");// .号需要进行转义  加上右双斜杠
			int x= Integer.parseInt(temp[0]);//  提取到不含后缀的文件名对应的整数
			//System.out.println(nowtime + "读取文件顺序" + x);
			if(nowtime < x)
			{
				flag =true;
				System.out.println(x+" 开始从pcm转换为String");
				str_re = Voice_to_String.convert(APPID, fileNameLists[i],path);
				System.out.println(x+" 转换结果" + str_re);
				write_recent_name(recent_name_path,""+x);//该进行记录的回写
				write_string(txtdata_path,str_re,x+".txt");
				nowtime = x;
				System.out.println(x+" 转换完成");
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
		String filename = path + "pcm_record.txt";
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
		
	
	private static String get_recent_name(String path)
	{
		String result ="";
		String filename = path + "pcm_record.txt";//记录最近转换完成的文件名
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
