package com.iflytek.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class today_report {
	static String STRDATA_PATH = "./stringdata";//存储string字符串文件的路径
	static File_ways file_ways = new File_ways();//声明一个文件处理对象
	static total_strdeal strdeal = new total_strdeal();//声明一个字符串处理对象
	static int Max=0;
	static String MaxStr = "";
	static int MaxJiange = 0;
	static int MaxJiangeStart = 0;
	static int MaxDuohuaStart = 0;
	static String Freword = "";
	static int Frequence = 0;
	public static String fenxi(int time)
	{
		String result = "";
		int count = 0;
		int jiange =0;
		int befor = 0;
		int linestrnu=0,totalnu=0;
		Map<String, Integer> maps = new HashMap<>();//记录词性的个数
		Map<String, Integer> numaps = new HashMap<>();//记录词的个数
		JSONObject resultjsonobject = new JSONObject();
		int j,k,flag = 0;
		Set<String> hs = new HashSet<String>();
		String curTime = System.currentTimeMillis() / 1000L + "";
		JSONObject jsonobject = new JSONObject();
		int endtime = Integer.parseInt(curTime);
		int starttime = endtime - time;
		MaxJiangeStart = starttime;
	    File file = new File(STRDATA_PATH);
	    String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
	    
		for(int i = 0; i < fileNameLists.length; i ++){
			String[] temp = fileNameLists[i].split("\\.");// .号需要进行转义  加上右双斜杠
			int x= Integer.parseInt(temp[0]);//  提取到不含后缀的文件名对应的整数
			//System.out.println(nowtime + "读取文件顺序" + x);
			if(starttime <= x)
			{
				if(flag==1)
				{
					jiange = x - befor ;
					
					if(jiange > MaxJiange)
					{
						MaxJiange = jiange;//最长的沉默时间长度
						MaxJiangeStart = befor;//最长的沉默时间起始
					}
				}
				befor = x;
				flag = 1;
				linestrnu = 0;
				result = "";
				String luostr = file_ways.get_string(STRDATA_PATH,x+".txt");
				
				resultjsonobject = JSON.parseObject(luostr);
				count = resultjsonobject.getIntValue("linecount");
				for(j = 0; j<count; j++)
				{
					String linestr = resultjsonobject.getString(""+j);
					linestrnu+=linestr.length();			//记录一个文件的字数长度
					
					String cws[] = strdeal.cws(linestr);//进行中文分词
					String pos[] = strdeal.pos(linestr);//进行词性识别
					for(k = 0; k<pos.length; k++)
					{
						if(cws[k].length()>1)
						if(numaps.containsKey(cws[k]))
						{
							numaps.replace(cws[k], numaps.get(cws[k])+1);
						}
						else
						{
							numaps.put(cws[k], 1);//总的词统计
						}
						
						if(maps.containsKey(pos[k]))
						{
							maps.replace(pos[k], maps.get(pos[k])+1);
						}
						else
						{
							maps.put(pos[k], 1);//总的词性统计
						}
					}
					result += linestr;
				}
				if(linestrnu>Max)
				{
					Max = linestrnu;
					MaxStr = result;//说的最长的话
					MaxDuohuaStart = x;//话最多的时间
				}
				totalnu += linestrnu;//总字数
				
			}

		}
		JSONObject jsontongji = new JSONObject();//用于装词性统计的json对象
		JSONObject jsonresult = new JSONObject();//用于装所有数据的对象
        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(maps.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
            //降序排序
            public int compare(Entry<String, Integer> o2,
                    Entry<String, Integer> o1) {
                return o1.getValue().compareTo(o2.getValue());
            }

        });
        
        List<Map.Entry<String,Integer>> nulist = new ArrayList<Map.Entry<String,Integer>>(numaps.entrySet());
        Collections.sort(nulist,new Comparator<Map.Entry<String,Integer>>() {
            //降序排序
            public int compare(Entry<String, Integer> o2,
                    Entry<String, Integer> o1) {
                return o1.getValue().compareTo(o2.getValue());
            }

        });
        
        for(Map.Entry<String,Integer> mapping:nulist){   //进行元素访问
        	
        	Freword = mapping.getKey();
        	Frequence = mapping.getValue();
        	break;
        }
        j = 0;
        for(Map.Entry<String,Integer> mapping:list){   //进行元素访问
        	JSONObject tempjson = new JSONObject();//用于装所有数据的对象
        	tempjson.put(mapping.getKey(), mapping.getValue());//进行结果的生成
        	jsonresult.put(""+j, tempjson);
        	j++;
         	//System.out.println(mapping.getKey()+" : "+mapping.getValue()); 
       } 
        jsonresult.put("linecount", j);//代表词性总数
        jsonresult.put("MaxDuohuaStart", MaxDuohuaStart);//代表最长句子的开始时间
        jsonresult.put("MaxStr", MaxStr);//代表最长话的内容
        jsonresult.put("MaxJiange", MaxJiange);//代表最大的沉默时间
        jsonresult.put("MaxJiangeStart", MaxJiangeStart);//代表最长沉默时间的起始时间
        jsonresult.put("totalnu", totalnu);//代表总共说的词语数量
        jsonresult.put("Freword", Freword); //表示频率最高的词语
        jsonresult.put("Frequence", Frequence);//表示最高频率的次数
        System.out.println("分析结果为"+jsonresult);
		return jsonresult.toString();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String curTime = System.currentTimeMillis() / 1000L + "";
		int Timename = Integer.parseInt(curTime);
		fenxi(Timename);
	}

}
