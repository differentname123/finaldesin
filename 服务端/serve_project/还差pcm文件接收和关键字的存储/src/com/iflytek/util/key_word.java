package com.iflytek.util;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


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
	
	public static void main(String[] args) throws IOException {
		System.out.println(TEXT.length());
		Map<String, String> header = buildHttpHeader();
		String result = HttpUtil.doPost1(WEBTTS_URL, header, "text=" + URLEncoder.encode(TEXT, "utf-8"));
		System.out.println("itp 接口调用结果：" + result);
		
	}
	public String get_string(String filename)
	{
		JSONObject object = new JSONObject();
		String result = "";
		BufferedReader reader = null;
        try {

        	reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
            //System.out.println("以行为单位读取文件内容，一次读一整行：");
            String tempString = null;
            int line = 0;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	if(tempString.length()!=0)//判断是否为空换行
            	{
                	object.put(""+line, tempString);
                    //System.out.println("line " + line +"size: "+tempString.length()+ ": " + tempString);
                    line++;
            	}

            }
            reader.close();
            object.put("linecount", line);
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
        result = object.toString();
		return result;
		
	}
	public String get_keyword(String filename,String appid,String api_key,String path) throws UnsupportedEncodingException
	{
		String text;
		int i;
		JSONObject jsonobject = new JSONObject();
		JSONObject rejsonobject = new JSONObject();
		JSONObject kejsonobject = new JSONObject();
		JSONObject keyjsonobject = new JSONObject();
		JSONObject finaljsonobject = new JSONObject();
		String redata;
		int linecount;
		String result= "";
		text =get_string(path+filename);
		jsonobject = JSON.parseObject(text);
		linecount = jsonobject.getIntValue("linecount");
		Map<String, String> header = buildHttpHeader();
		for(i=0;i<linecount;i++)
		{
			result += HttpUtil.doPost1(WEBTTS_URL, header, "text=" + URLEncoder.encode(jsonobject.getString(""+i), "utf-8"))+" ";
			rejsonobject = JSON.parseObject(result);
			kejsonobject = JSON.parseObject(rejsonobject.getString("data"));
			redata = kejsonobject.getString("ke");
			String[] re = redata.split(",");
			改利用正则表达式来进行每一项的提取
			for(int j =0;j<re.length;j++)
			{
				System.out.println("分割后"+re[j]);
			}
			System.out.println("长度"+"关键字部分"+kejsonobject.getString("ke"));
			
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
