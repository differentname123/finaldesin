package com.iflytek.util;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;




public class get_recent_top_words {

	static String KEYDATA_PATH = "./keydata";//存储string字符串文件的路径
	static File_ways file_ways = new File_ways();//声明一个文件处理对象
	public static Map<String, Double> get_top_words(int time,int k)//获取最近time时间内的前k大的关键字
	{
		int i,j;
		Map<String, Double> maps = new HashMap<>();
		Map<String, Double> resu = new LinkedHashMap<>();
		JSONObject tempjson = new JSONObject();
		String curTime = System.currentTimeMillis() / 1000L + "";
		int endtime = Integer.parseInt(curTime);
		int starttime = endtime - time;
		
	    File file = new File(KEYDATA_PATH);
	    String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
	    if(fileNameLists==null)
	    	return null;
	    for(i=0;i<fileNameLists.length;i++)
	    {
			String[] temp = fileNameLists[i].split("\\.");// .号需要进行转义  加上右双斜杠
			int x= Integer.parseInt(temp[0]);
			if(x>=starttime)
			{
				String tempstr = file_ways.get_string(KEYDATA_PATH, x+".txt");
				tempjson = JSONObject.parseObject(tempstr);
				int linecount = tempjson.getIntValue("linecount");
				for(j=0;j<linecount;j++)
				{
					String strtemp = tempjson.getString(""+j);
					String splitstr[] = strtemp.split(":");
					double bili = Double.parseDouble(splitstr[1]);
					if(maps.containsKey(splitstr[0]))
					{
						bili+=maps.get(splitstr[0]);
						maps.replace(splitstr[0], bili);
					}
					else
					{
						maps.put(splitstr[0], bili);
					}
					
				}
				
			}
	    	
	    }
	    
	    //进行排序操作
        List<Map.Entry<String,Double>> list = new ArrayList<Map.Entry<String,Double>>(maps.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Double>>() {
            //降序排序
            public int compare(Entry<String, Double> o2,
                    Entry<String, Double> o1) {
                return o1.getValue().compareTo(o2.getValue());
            }

        });
       i=0;
        for(Map.Entry<String,Double> mapping:list){   //进行元素访问
               if(i<k)
               {
            	   resu.put(mapping.getKey(), mapping.getValue());//进行结果的生成
            	   System.out.println(mapping.getKey()+" : "+mapping.getValue()); 
            	   i++;
               }
               else
               {
            	   break;
               }
          } 
        System.out.println( "最终结果"+resu);
     
	    
		
		return resu;
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String, Double> hashMap= get_top_words(1,2);
		
		
		 Set<Entry<String, Double>> set = hashMap.entrySet();
	        Iterator<Entry<String, Double>> iterator = set.iterator();
	        while(iterator.hasNext()) {
	            Entry<String, Double> entry = iterator.next();
	            String key = (String) entry.getKey();
	            double value =entry.getValue();
	            System.out.println("key:" + key + ", value:" + value);
	        }


	}

}
