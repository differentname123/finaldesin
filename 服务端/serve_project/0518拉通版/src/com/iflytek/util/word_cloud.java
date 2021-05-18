package com.iflytek.util;

import java.awt.Color;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

public class word_cloud {
	private static final String BACKGROUND_PATH = "./word_cloud_background";
	private static final String BACKGROUND_RECORD = "background_record.txt";// 记录最近生成词云轮廓的名字
	private static final String HEART_STYLE = "heart.png";
	private static final String POSTIVE_STYLE = "heart.png";
	private static final String NEGTIVE_STYLE = "heart.png";
	private static final String MID_STYLE = "heart.png";
	private static final String BEST_WORDS_COUNT = "best_words_count.txt";// 轮廓属性文件
	private static final String WORD_CLOUD_SAVEPATH = "./word_cloud_data";
	static int NEGTIVE = -1000;
	static int POSTIVE = 1000;
	static File_ways file_ways = new File_ways();//声明一个文件处理对象
	static get_recent_xinqing xinqing = new get_recent_xinqing();//声明一个心情处理对象
	
	public static String JieXi(String str,int wait_w)// 解析文件名 和字体大小 以字符串返回 以空格分开
	{
		String result = "";
		String str_arr[] = str.split(" ");
		String file_name = str_arr[0];
		int min_f = Integer.parseInt(str_arr[1]);
		int max_f = Integer.parseInt(str_arr[2]);
		int min_w = Integer.parseInt(str_arr[3]);
		int max_w = Integer.parseInt(str_arr[4]);
		int cha_f = (max_f-min_f);
		int cha_w = (max_w-min_w);
		double bili = (double)cha_f/cha_w;
		double cha = bili*(wait_w-min_w);
		int final_f = (int) (max_f - cha);
		result += file_name + " " + final_f;
		return result;
	}
	
	public static String get_background_size(int word_count)// 获取满足词数的轮廓名字和字体大小
	{
		String result = "";
		ArrayList<String> match_count = new ArrayList<String> ();
		String all_str = file_ways.get_string(BACKGROUND_PATH, BEST_WORDS_COUNT);
		JSONObject json_object = new JSONObject();
		json_object = JSON.parseObject(all_str);
		int linecount = json_object.getIntValue("linecount");
		for(int i = 0;i <linecount;i++)
		{
			String line_str = json_object.getString(i+"");
			String str_arr[] = line_str.split(" ");
			int min_w = Integer.parseInt(str_arr[3]);
			int max_w = Integer.parseInt(str_arr[4]);
			if(word_count>min_w&&word_count<max_w)
			{
				match_count.add(line_str);
			}
			
		}
		if(match_count.size() == 0)
		{
			int index = (int)(Math.random()*linecount);
			result = json_object.getString(""+index);
		}
		else
		{
			int index = (int)(Math.random()*match_count.size());
			result = match_count.get(index);
		}
		result = JieXi(result, word_count);
		return result;
	}

	public static boolean build_cloud(int time,int xinqing,String backpath,String savename)
	{
		boolean result = false;
		final List<WordFrequency> wordFrequencies = new ArrayList<>();//直接处理显示词语
		//String curTime = System.currentTimeMillis() / 1000L + "";
		//time = Integer.parseInt(curTime);
		Map<String, Double> hashMap = get_recent_top_words.get_top_words(time, 100);
		int word_count = hashMap.size();
		String shuxing = get_background_size(word_count);
		String str_arr[] = shuxing.split(" ");
		int font_size = Integer.parseInt(str_arr[1]);
		String file_name = str_arr[0];
		JSONObject jsonobject = new JSONObject();
		 Set<Entry<String, Double>> set = hashMap.entrySet();
	        Iterator<Entry<String, Double>> iterator = set.iterator();
	        while(iterator.hasNext()) {
	            Entry<String, Double> entry = iterator.next();
	            String key = (String) entry.getKey();
	            double value =entry.getValue();
	            jsonobject.put(key, value);
	            int temp = (int)value *10;
	            wordFrequencies.add(new WordFrequency(key,temp));
	        }
	        file_ways.creat_path(WORD_CLOUD_SAVEPATH);
	        build_word_cloud_size(wordFrequencies,550,550,BACKGROUND_PATH+"\\"+file_name,savename, font_size);
	        file_ways.write_recent_name(BACKGROUND_PATH, file_name, BACKGROUND_RECORD);
	        
	      //  System.out.println(jsonobject);
		
		return result;
	}
	public static void test(int count,String background)//用于生成以指定词数指定背景的词云文件位置在当前文件的word_cloud_data文件夹
	{
		//file_ways.creat_path("./test");
		String temp[] = background.split("\\.");
		double bili = 0.4;
		
		String curTime = System.currentTimeMillis() / 1000L + "";
        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        // 返回字数限制
        frequencyAnalyzer.setWordFrequenciesToReturn(count);
        // 最短字符
        frequencyAnalyzer.setMinWordLength(2);
        // 引入中文解析器
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
        // 设置词汇文本
        
            try {
    			final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load("C:\\Users\\24349\\Desktop\\temp\\test.txt");
    			for(int l =10;l<50;l++)
    			{
    				for(int i=0;i<2;i++)
    		        {
    					test_build_word_cloud(wordFrequencies,550,550,background,l+"_"+count+"_"+i+".png", l);
    		        }
    			}
    			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
	}
	public static void test_build_word_cloud(List wordFrequencies,int chang,int kuan,String backstyle,String savename, int  size1)//输入数据list和背景及大小生成词云
	{
        // 生成图片尺寸 width 1920  height 1080‘
		
        Dimension dimension = new Dimension(chang, kuan);
        int size = 0,s,n;
        
        
        n = wordFrequencies.size();
        //s = (int) (chang*kuan*bili);
        size = size1;
        //size = (int) Math.sqrt(s/3/n);//计算平均字体size大小
        // 生产词云形状
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        // 词与词的间距
        wordCloud.setPadding(size/6);
        // 设置中文字体样式
        java.awt.Font font = new java.awt.Font("STSong-Light", 5, 16);
        // 设置背景颜色
        wordCloud.setBackgroundColor(new Color(250, 255, 247));
        // 生成字体
        wordCloud.setKumoFont(new KumoFont(font));
        //wordCloud.setBackground(new RectangleBackground(dimension));
        //wordCloud.setBackground(new CircleBackground(255));
        try {
			wordCloud.setBackground(new PixelBoundryBackground(backstyle));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // 生成字体颜色
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.pink, Color.BLUE, Color.cyan, 130, 310));
        wordCloud.setFontScalar(new LinearFontScalar((int) (0.7*size), (int) (1.3*size)));//设置字体大小范围
        wordCloud.build(wordFrequencies);
        //wordCloud.writeToFile(WORD_CLOUD_SAVEPATH+"\\"+savename);
        file_ways.creat_path("F:\\temp\\final_desine\\服务端\\word_cloud\\测试最优的参数"+"\\" + backstyle);
        wordCloud.writeToFile("F:\\temp\\final_desine\\服务端\\word_cloud\\测试最优的参数"+"\\" + backstyle+ "\\" +savename);
        //pngtojpg(savename);
        System.out.println("词数: "+wordFrequencies.size()+"  填充面积: "+"  字体大小: "+size);
	}
	public static void build_word_cloud_size(List wordFrequencies,int chang,int kuan,String backstyle,String savename, int size)//输入数据list和背景及大小和字体大小生成词云
	{
        // 生成图片尺寸 width 1920  height 1080‘
		
        Dimension dimension = new Dimension(chang, kuan);
        int s,n;
        
        
        n = wordFrequencies.size();
        s = (int) (chang*kuan*0.65);
        // 生产词云形状
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        // 词与词的间距
        wordCloud.setPadding(size/6);
        // 设置中文字体样式
        java.awt.Font font = new java.awt.Font("STSong-Light", 5, 16);
        // 设置背景颜色
        wordCloud.setBackgroundColor(new Color(250, 255, 247));
        // 生成字体
        wordCloud.setKumoFont(new KumoFont(font));
        //wordCloud.setBackground(new RectangleBackground(dimension));
        //wordCloud.setBackground(new CircleBackground(255));
        try {
			wordCloud.setBackground(new PixelBoundryBackground(backstyle));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // 生成字体颜色
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.pink, Color.BLUE, Color.cyan, 130, 310));
        wordCloud.setFontScalar(new LinearFontScalar((int) (0.7*size), (int) (1.3*size)));//设置字体大小范围
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile(WORD_CLOUD_SAVEPATH+"\\"+savename);
        //file_ways.creat_path("F:\\temp\\final_desine\\服务端\\word_cloud\\测试最优的参数"+"\\" + backstyle);
       // wordCloud.writeToFile("F:\\temp\\final_desine\\服务端\\word_cloud\\测试最优的参数"+"\\" + backstyle+ "\\" +savename);
        pngtojpg(savename);
        System.out.println("词数: "+wordFrequencies.size()+"  填充面积: "+s+"  字体大小: "+size);
	}
	public static void build_word_cloud(List wordFrequencies,int chang,int kuan,String backstyle,String savename)//输入数据list和背景及大小生成词云
	{
        // 生成图片尺寸 width 1920  height 1080‘
		
        Dimension dimension = new Dimension(chang, kuan);
        int size = 0,s,n;
        
        
        n = wordFrequencies.size();
        s = (int) (chang*kuan*0.65);
        size = (int) Math.sqrt(s/3/n);//计算平均字体size大小
        // 生产词云形状
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        // 词与词的间距
        wordCloud.setPadding(size/6);
        // 设置中文字体样式
        java.awt.Font font = new java.awt.Font("STSong-Light", 5, 16);
        // 设置背景颜色
        wordCloud.setBackgroundColor(new Color(250, 255, 247));
        // 生成字体
        wordCloud.setKumoFont(new KumoFont(font));
        //wordCloud.setBackground(new RectangleBackground(dimension));
        //wordCloud.setBackground(new CircleBackground(255));
        try {
			wordCloud.setBackground(new PixelBoundryBackground(backstyle));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // 生成字体颜色
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.pink, Color.BLUE, Color.cyan, 130, 310));
        wordCloud.setFontScalar(new LinearFontScalar((int) (0.7*size), (int) (1.3*size)));//设置字体大小范围
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile(WORD_CLOUD_SAVEPATH+"\\"+savename);
        //file_ways.creat_path("F:\\temp\\final_desine\\服务端\\word_cloud\\测试最优的参数"+"\\" + backstyle);
       // wordCloud.writeToFile("F:\\temp\\final_desine\\服务端\\word_cloud\\测试最优的参数"+"\\" + backstyle+ "\\" +savename);
        pngtojpg(savename);
        System.out.println("词数: "+wordFrequencies.size()+"  填充面积: "+s+"  字体大小: "+size);
	}
	public static void pngtojpg(String filename)//将词云目录下的filename图片转为jpg格式
	{
		 BufferedImage bufferedImage;
		 
		    try {
		 
		      //read image file
		      bufferedImage = ImageIO.read(new File(WORD_CLOUD_SAVEPATH+"\\"+filename));
		      String temp[] = filename.split("\\.");
		     // System.out.println(filename+temp);
		      // create a blank, RGB, same width and height, and a white background
		      BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
		            bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		 
		     //TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位
		 
		      newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
		 
		      // write to jpeg file
		      ImageIO.write(newBufferedImage, "jpg", new File(WORD_CLOUD_SAVEPATH+"\\"+temp[0]+".jpg"));
		 
		      System.out.println("Done");
		 
		    } catch (IOException e) {
		 
		      e.printStackTrace();
		 
		    }
		 

	}
	public static String word_cloud_produce(int time)//生成time时间内的词云，并返回词云图片名字
	{
		String curTime = System.currentTimeMillis() / 1000L + "";
		build_cloud(time,100,BACKGROUND_PATH+"\\"+MID_STYLE,curTime+".png");
        return curTime+".jpg";
	}
	public static void main(String[] args) throws IOException {
		/*
		//建立词频分析器，设置词频，以及词语最短长度，此处的参数配置视情况而定即可
	        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
	        // 返回字数限制
	        frequencyAnalyzer.setWordFrequenciesToReturn(600);
	        // 最短字符
	        frequencyAnalyzer.setMinWordLength(2);
	        // 引入中文解析器
	        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
	        // 设置词汇文本
	        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load("C:\\Users\\24349\\Desktop\\temp\\test.txt");
	        // 生成图片尺寸 width 1920  height 1080
	        Dimension dimension = new Dimension(1152, 1152);
	        // 生产词云形状
	        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
	        // 词与词的间距
	        wordCloud.setPadding(1);
	        // 设置中文字体样式
	        java.awt.Font font = new java.awt.Font("STSong-Light", 2, 80);
	        // 设置背景颜色
	        wordCloud.setBackgroundColor(new Color(250, 255, 241));
	        // 生成字体
	        wordCloud.setKumoFont(new KumoFont(font));
	        //wordCloud.setBackground(new RectangleBackground(dimension));
	        //wordCloud.setBackground(new CircleBackground(255));
	        wordCloud.setBackground(new PixelBoundryBackground("S:\\test1.png"));
	        // 生成字体颜色
	        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.pink, Color.BLUE, Color.cyan, 130, 310));
	        wordCloud.setFontScalar(new LinearFontScalar(5, 40));
	        wordCloud.build(wordFrequencies);
	        wordCloud.writeToFile("S:\\031510.png");
	        */
			//String curTime = System.currentTimeMillis() / 1000L + "";
			//int time = Integer.parseInt(curTime);
	        //build_cloud(time,100,BACKGROUND_PATH+"\\"+"piaochong.png",curTime+".png");
		
		/*   //单个轮廓图片测试
		String curTime = System.currentTimeMillis() / 1000L + "";
		int time = Integer.parseInt(curTime);
		String name = "bear_hand2.png";
		build_cloud(time,100,BACKGROUND_PATH+"\\"+name,name + curTime+".png");
		*/
		
		
		
		
			int number = 40;
			File file = new File(BACKGROUND_PATH);
			String fileName[] = file.list();
			for(int index = 0; index < fileName.length; index++)
			{
			
				String name = fileName[index];
				for(int i = 1 ;i<13;i++)
					test(number +3*i,BACKGROUND_PATH+"\\"+name);
				
			}
			
			
			
	    }
}
