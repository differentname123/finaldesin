package com.iflytek.util;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

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
	private static final String HEART_STYLE = "heart.png";
	private static final String WORD_CLOUD_SAVEPATH = "./word_cloud_data";
	static File_ways file_ways = new File_ways();//声明一个文件处理对象
	public static boolean build_cloud(int time,int xinqing,String backpath,String savename)
	{
		boolean result = false;
		final List<WordFrequency> wordFrequencies = new ArrayList<>();//直接处理显示词语
		String curTime = System.currentTimeMillis() / 1000L + "";
		time = Integer.parseInt(curTime);
		Map<String, Double> hashMap = get_recent_top_words.get_top_words(time, 100);
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
	        build_word_cloud(wordFrequencies,500,500,backpath,savename);
	        
	      //  System.out.println(jsonobject);
		
		return result;
	}
	public static void build_word_cloud(List wordFrequencies,int chang,int kuan,String backstyle,String savename)//输入数据list和背景及大小生成词云
	{
        // 生成图片尺寸 width 1920  height 1080‘
		
        Dimension dimension = new Dimension(chang, kuan);
        int size = 0,s,n;
        
        
        n = wordFrequencies.size();
        s = (int) (chang*kuan);
        size = (int) Math.sqrt(s/3/n);//计算平均字体size大小
        // 生产词云形状
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        // 词与词的间距
        wordCloud.setPadding(size/6);
        // 设置中文字体样式
        java.awt.Font font = new java.awt.Font("STSong-Light", 5, 16);
        // 设置背景颜色
        wordCloud.setBackgroundColor(new Color(250, 255, 241));
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
        System.out.println("词数: "+wordFrequencies.size()+"  填充面积: "+s+"  字体大小: "+size);
	}
	public static String word_cloud_produce(int time)//生成time时间内的词云，并返回词云图片名字
	{
		String curTime = System.currentTimeMillis() / 1000L + "";
        build_cloud(time,100,BACKGROUND_PATH+"\\"+HEART_STYLE,curTime+".png");
        return curTime+".png";
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
			String curTime = System.currentTimeMillis() / 1000L + "";
	        build_cloud(100,100,BACKGROUND_PATH+"\\"+HEART_STYLE,curTime+".png");
	    }
}
