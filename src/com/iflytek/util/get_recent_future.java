package com.iflytek.util;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class get_recent_future {
	private static final String celue1path = "./celue1";
	static File_ways file_ways = new File_ways();//声明一个文件处理对象
	public static String get_future(int time)
	{
		int count = 0;
		JSONObject resultjsonobject = new JSONObject();
		int j;
		Set<String> hs = new HashSet<String>();
		String curTime = System.currentTimeMillis() / 1000L + "";
		JSONObject jsonobject = new JSONObject();
		int endtime = Integer.parseInt(curTime);
		int starttime = endtime - time;
		starttime = starttime/86400;
	    File file = new File(celue1path);
	    String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
	    
		for(int i = fileNameLists.length -1; i >=0; i --){ //逆序就能找到最新的值
			String[] temp = fileNameLists[i].split("\\.");// .号需要进行转义  加上右双斜杠
			int x= Integer.parseInt(temp[0]);//  提取到不含后缀的文件名对应的整数
			//System.out.println(nowtime + "读取文件顺序" + x);
			if(starttime <= x)
			{
				String futurestr = file_ways.get_string(celue1path,x+".txt");
				jsonobject = JSON.parseObject(futurestr);
				int linecount = jsonobject.getIntValue("linecount");
				for(j=0;j< linecount ; j++)
				{
					String word = jsonobject.getString(""+j);
					hs.add(word);
					
				}
			}
			if(hs.size()>2)// 只需要找三个关键词就行了因为客户端只有三个推荐位
			{
				
				 break;
			}
		}
		
        Iterator<String> it = hs.iterator();
        while (it.hasNext()) {
        	String tete = it.next();
        	resultjsonobject.put(count+"",tete);
            count++;
        }
        resultjsonobject.put("linecount", count);
		System.out.println("捕捉的事物为"+resultjsonobject);
		return resultjsonobject.toString();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String curTime = System.currentTimeMillis() / 1000L + "";
		int Timename = Integer.parseInt(curTime);
		get_future(Timename);

	}

}
