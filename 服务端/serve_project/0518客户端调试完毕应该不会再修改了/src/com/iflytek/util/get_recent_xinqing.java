package com.iflytek.util;

import java.io.File;

import com.alibaba.fastjson.JSONObject;

public class get_recent_xinqing {
	
	private static final String celue2path = "./celue2";
	private static final String MOOD_RECORD_PATH = "./data";
	private static final String MOOD_RECORD_NAME = "mood_record.txt";
	static File_ways file_ways = new File_ways();//声明一个文件处理对象
	public static int get_xinqing_zhi(int time)//获取time时间内的心情总值
	{
		int total = 0;

		String curTime = System.currentTimeMillis() / 1000L + "";
		int endtime = Integer.parseInt(curTime);
		int starttime = endtime - time;
		 File file = new File(celue2path);
		 String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
		 if(fileNameLists==null)
			 return 0;
		 
			for(int i = 0; i < fileNameLists.length; i ++){
				String[] temp = fileNameLists[i].split("\\.");// .号需要进行转义  加上右双斜杠
				int x= Integer.parseInt(temp[0]);
				if(x>=starttime)
				{
					
					String temp1 = file_ways.get_recent_name(celue2path, x+".txt");
					int tempzhi = Integer.parseInt(temp1);
					total +=tempzhi;
					
				}
			}
			
		return total;
	}
	public static String get_xinqing(int time)
	{
		int count=0;
		int flag =0;
		int int_zhi;
		int total_zhi = 0;
		int max_zhi = 0,min_zhi = 100000;// 记录最大和最小值方便计算后续最大差值值
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
					int_zhi = Integer.parseInt(temp1);
					total_zhi += int_zhi;
					if(int_zhi>max_zhi)
					{
						max_zhi = int_zhi;
					}
					if(min_zhi>int_zhi)
					{
						flag = 1;
						min_zhi = int_zhi;
					}
					tempjson.put("time", x);
					tempjson.put("zhi", temp1);
					object.put(""+count, tempjson);
					count++;
				}
			}
			object.put("linecount", count);
			if(flag ==1 )
			file_ways.write_recent_name(MOOD_RECORD_PATH, (max_zhi - min_zhi) + " " + total_zhi/(count+1), MOOD_RECORD_NAME);
			else
			{
				file_ways.write_recent_name(MOOD_RECORD_PATH, (max_zhi - 0) + " " + total_zhi/(count+1), MOOD_RECORD_NAME);
			}
		return object.toString();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String curTime = System.currentTimeMillis() / 1000L + "";
		int Timename = Integer.parseInt(curTime);
		System.out.println(get_xinqing(Timename));

	}

}
