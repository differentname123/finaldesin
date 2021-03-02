package com.iflytek.util;

import java.io.File;



public class find_best_zuhe {
	static String PCMDATA_PATH = "./pcmdata";//存储pcm文件的路径
	static String TESTDATA_PATH = "./testdata";//存储pcm文件的路径
	private static final String APPID="5fed5ca2";//这里是自己的APPID
	static Voice_to_String pcmtostr = new Voice_to_String();
	static File_ways file_ways = new File_ways();//声明一个文件处理对象
	static int XUNHUAN = 5;
	
	public static  void fun1()
	{
		int delay = 1000;
		int size = 64;
		int i,j;
	    File file = new File(PCMDATA_PATH);
	    String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
	    String retemp;
	    
		String swaitime = System.currentTimeMillis() / 1000L + "";
		int swaitimenu = Integer.parseInt(swaitime);
		int neijiange,waijiange,totalnu;
		totalnu = 0;
	    for(j=0;j<XUNHUAN;j++)
	    {
			String sneitime = System.currentTimeMillis() / 1000L + ""; //单位是秒
			int sneitimenu = Integer.parseInt(sneitime);
	    	retemp = "";
			for(i = 0; i < fileNameLists.length; i ++){
				String[] temp = fileNameLists[i].split("\\.");// .号需要进行转义  加上右双斜杠
				int x= Integer.parseInt(temp[0]);//  提取到不含后缀的文件名对应的整数
				retemp+=pcmtostr.convert_test(APPID, x+".pcm", PCMDATA_PATH, delay, size)+"\n\r";
				
			}
			String eneitime = System.currentTimeMillis() / 1000L + "";
			int eneitimenu = Integer.parseInt(eneitime);
			neijiange = eneitimenu -sneitimenu ;
			totalnu+=retemp.length();
			file_ways.write_string(TESTDATA_PATH,retemp,j+"长度 "+retemp.length()+" 延时 "+delay+" 分片 "+size+" 耗时 "+neijiange+".txt");
	    }
		String ewaitime = System.currentTimeMillis() / 1000L + "";
		int ewaitimenu = Integer.parseInt(ewaitime);
		waijiange = ewaitimenu - swaitimenu;
		System.out.println("totaltime: "+waijiange+"total str"+totalnu);


	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		fun1();

	}

}