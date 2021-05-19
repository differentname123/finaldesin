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
	static String RECENT_PATH = "./data";//存储pcm转字符串记录和str数据处理记录的路径
	static String PCMDATA_PATH = "./pcmdata";//存储pcm文件的路径
	static String PCMDATA_JILU = "pcm_record.txt";//存储pcm记录的文件名
	static String STRDATA_PATH = "./stringdata";//存储string字符串文件的路径
	
	static File_ways file_ways = new File_ways();//声明一个文件处理对象
	
	
	public static void start() throws IOException{
		//使用默认值
		System.out.println("pcm转String线程开始");
		String recentname ="";
		boolean flag;
		file_ways.init1(RECENT_PATH, PCMDATA_JILU);
		while(true)
		{
			recentname = file_ways.get_recent_name(RECENT_PATH,PCMDATA_JILU);//获取最近的转换记录，方便下一次转换
			flag = judge_zhuanhuan(recentname);
			if(flag)
				System.out.println(" 等待新的pcm文件进行转换");
		}
		
	}
	
	static boolean judge_zhuanhuan(String recentname)//判断是否这个文件以经是最新文件
	{
		boolean flag = false;
		String str_re,str_re1;
		
		
		int nowtime = Integer.parseInt(recentname);
	    File file = new File(PCMDATA_PATH);
	    String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
	    
		for(int i = 0; i < fileNameLists.length; i ++){
			if (i == fileNameLists.length) {// 如果正在进行最新pcm数据的转换要等待一段时间，避免转换的是空值
				Thread.currentThread();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String[] temp = fileNameLists[i].split("\\.");// .号需要进行转义  加上右双斜杠
			int x= Integer.parseInt(temp[0]);//  提取到不含后缀的文件名对应的整数
			//System.out.println(nowtime + "读取文件顺序" + x);
			if(nowtime < x)
			{
				flag =true;
				System.out.println(x+" 开始从pcm转换为String");
				str_re = Voice_to_String.convert(APPID, fileNameLists[i],PCMDATA_PATH);
				/*两次转换优化策略
				System.out.println(x+" 第一次转换结果" + str_re);
				str_re1 = Voice_to_String.convert(APPID, fileNameLists[i],PCMDATA_PATH);//多加一次识别增加准确性
				if(str_re.length()<str_re1.length())
				{
					str_re = str_re1;
				}
				System.out.println(x+" 最终转换结果" + str_re);
				*/
				if(str_re != null)
				{
					file_ways.write_recent_name(RECENT_PATH,""+x,PCMDATA_JILU);//该进行记录的回写
					file_ways.write_string(STRDATA_PATH,str_re,x+".txt");
				}

				nowtime = x;
				System.out.println(x+" 转换完成:" + str_re);
				//转换后内容的写入
			}
			
		}
		return flag;
	}




}
