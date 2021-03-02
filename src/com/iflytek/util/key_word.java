package com.iflytek.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;





public class key_word {
	// webapi接口地址
	private static final String WEBTTS_URL = "http://ltpapi.xfyun.cn/v1/ke";
	// 应用ID
	private static final String APPID = "5fed5ca2";
	// 接口密钥
	private static final String API_KEY = "31c6c1836c5c2946fdf18ca5a9fddc5d";
	// 文本
	private static final String TEXT = "打开页面,进行物联网平台的连接，玩游戏";
	

	private static final String TYPE = "dependent";
	
	private static final String celue1path = "./celue1";
	private static final String celue2path = "./celue2";
	
	File_ways file_ways = new File_ways();//声明一个文件处理对象
	
	public static void main(String[] args) throws IOException {
		System.out.println(TEXT.length());
		Map<String, String> header = buildHttpHeader();
		String result = HttpUtil.doPost1(WEBTTS_URL, header, "text=" + URLEncoder.encode(TEXT, "utf-8"));
		System.out.println("itp 接口调用结果：" + result);
		
	}

	
	//进行关键字的提取和策略一的进行
	public String get_keyword(String filename,String appid,String api_key,String path,String savefilename,String savepath) throws UnsupportedEncodingException
	{
		String text;
		int i;
		total_strdeal strdeal =new total_strdeal();
		
		
		
		JSONObject jsonobject = new JSONObject();
		JSONObject rejsonobject = new JSONObject();
		JSONObject kejsonobject = new JSONObject();
		String redata;
		int linecount;
		String result= "";
		text =file_ways.get_string(path,filename);
		jsonobject = JSON.parseObject(text);
		linecount = jsonobject.getIntValue("linecount");
		Map<String, String> header = buildHttpHeader();
		
		file_ways.init(savepath, savefilename);
		
		for(i=0;i<linecount;i++)
		{
			System.out.println("开始进行策略1");
			String future = strdeal.Judge1(jsonobject.getString(""+i));
			String curTime = System.currentTimeMillis() / 1000L + "";
			int Timename = Integer.parseInt(curTime);
			Timename = Timename/86400;
			if(future.length()>1)
			{
				System.out.println(future+ "识别成功");
				file_ways.write_data(celue1path,future,Timename+".txt");
			}
			System.out.println("开始进行策略2");
			//进行策略二的实现
			String result11 = strdeal.sa(jsonobject.getString(""+i));;
			JSONObject saobject = new JSONObject();
			saobject = JSON.parseObject(result11);
			String sascore = saobject.getString("score");
			String sasentiment = saobject.getString("sentiment");
			double satemp =  Double.parseDouble(sascore);
			int sasenttemp = Integer.parseInt(sasentiment);
			int tempre =  (int) (10000*satemp*sasenttemp);
			//该进行存储了，还有就是规范路径
			
			file_ways.write_data(celue2path,tempre+"",curTime+".txt");//按照   关键字：权重的格式一行一行写入文件
			System.out.println(sascore+" "+sasentiment);
			
			
			
			System.out.println("开始进行关键字提取");
			//进行关键词的提取和存储
			result = HttpUtil.doPost1(WEBTTS_URL, header, "text=" + URLEncoder.encode(jsonobject.getString(""+i), "utf-8"))+" ";
			System.out.println("提取结果为"+ result);
			rejsonobject = JSON.parseObject(result);
			kejsonobject = JSON.parseObject(rejsonobject.getString("data"));
			redata = kejsonobject.getString("ke");
			String[] re = redata.split(",");
			//改利用正则表达式来进行每一项的提取
			String pattern = "score";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(redata);
			while(m.find())
			{
				int start = m.start();
				int index = start;
				int ii,jj;
				String score="";
				String word ="";
				for(ii = start+8;redata.charAt(ii)!='\"';ii++)
				{
					score+=redata.charAt(ii);
				}
				jj=ii+10;
				for(ii = jj;redata.charAt(ii)!='\"';ii++)
				{
					word+=redata.charAt(ii);
				}
				file_ways.write_data(savepath,word+":"+score,savefilename);//按照   关键字：权重的格式一行一行写入文件
				System.out.println(word+": "+score+"存入成功");
			}
		}

		return result;
	}
	/**
	 * 组装http请求头
	 */
	private static Map<String, String> buildHttpHeader() throws UnsupportedEncodingException {
		String curTime = System.currentTimeMillis() / 1000L + "";
		String param = "{\"type\":\"" + TYPE +"\"}";
		String paramBase64 = new String(Base64.encodeBase64(param.getBytes("UTF-8")));
		String checkSum = DigestUtils.md5Hex(API_KEY + curTime + paramBase64);
		Map<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		header.put("X-Param", paramBase64);
		header.put("X-CurTime", curTime);
		header.put("X-CheckSum", checkSum);
		header.put("X-Appid", APPID);
		return header;
	}
}