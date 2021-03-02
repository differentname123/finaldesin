package com.iflytek.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.iflytek.util.total_strdeal;

public class total_key_word {
	static String RECENT_PATH = "./data";//存储pcm转字符串记录和str数据处理记录的路径
	static String STRDATA_JILU = "str_record.txt";//存储pcm转字符串记录和str数据处理记录的路径
	static String STRDATA_PATH = "./stringdata";//存储string字符串文件的路径
	static String KEYDATA_PATH = "./keydata";//存储string字符串文件的路径
	static String PCMDATA_JILU = "pcm_record.txt";//存储pcm记录的文件名
	
	final static String appid = "5fed5ca2";
	final static String api_key = "31c6c1836c5c2946fdf18ca5a9fddc5d";
	static boolean flag;
	
	
	static key_word kword=new key_word();//声明一个关键字提取对象
	static File_ways file_ways = new File_ways();//声明一个文件处理对象
	
	public static void start() throws IOException{
		//使用默认值
		System.out.println("关键字提取线程开始");
		String recentname ="";
		file_ways.init1(RECENT_PATH, STRDATA_JILU);
		while(true)
		{
			recentname = file_ways.get_recent_name(RECENT_PATH,STRDATA_JILU);//获取最近的转换记录，方便下一次转换
			flag = str_zhuanhuan(recentname);
			if(flag)
				System.out.println(" 等待新的str文件进行提取");
		}
	}
	static boolean str_zhuanhuan(String recentname) throws UnsupportedEncodingException//判断是否这个文件以经是最新文件
	{
		boolean flag = false;
		String str_re = null;
		String pcmrec = file_ways.get_recent_name(RECENT_PATH,PCMDATA_JILU);//获取最近的转换记录，方便下一次转换
		
		int texx = Integer.parseInt(pcmrec);
		int nowtime = Integer.parseInt(recentname);
	    File file = new File(STRDATA_PATH);
	    String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
	    if(fileNameLists==null)
	    	return false;
		for(int i = 0; i < fileNameLists.length; i ++){
			String[] temp = fileNameLists[i].split("\\.");// .号需要进行转义  加上右双斜杠
			int x= Integer.parseInt(temp[0]);
			//System.out.println(nowtime + "另一个" + x);
			//System.out.println(nowtime+" 读取的值为"+pcmrec);
			if(texx<=nowtime)//等待2秒，避免pcm还未转化完成
			{
				Thread.currentThread();//等待是否出现一句话
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(nowtime < x)
			{

				flag = true;
				System.out.println(" 开始对"+x+"进行文字处理");
				kword.get_keyword(fileNameLists[i],appid,api_key,STRDATA_PATH,x+".txt",KEYDATA_PATH);
				file_ways.write_recent_name(RECENT_PATH,""+x,STRDATA_JILU);//该进行记录的回写
				nowtime = x;

				//转换后内容的写入
			}
		}
		return flag;
	}


}