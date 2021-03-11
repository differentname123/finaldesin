package again;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

public class again {

	 public static void main(String[] args) throws IOException {

	        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
	        frequencyAnalyzer.setWordFrequenciesToReturn(600);
	        frequencyAnalyzer.setMinWordLength(2);
	        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
	        
	        ServerSocket server;
			server = new ServerSocket(1234);
			System.out.println("服务器已启动，端口号：" + 1234);
			String curTime = System.currentTimeMillis() / 1000L + "";
			int endtime = Integer.parseInt(curTime);
			
			//get_recent_top_words.get_top_words(endtime, 4);
			Socket socket;
			//通过无线循环监听客户端连接
			//如果没有客户端接入，将阻塞在accept操作上。
				socket = server.accept();
				System.out.println(socket+"连接成功");
				//当有新的客户端接入时，会执行下面的代码
				//然后创建一个新的线程处理这条Socket链路
				//executorService.execute(new ServerHandler(socket));
	        
	        
	        final List<WordFrequency> wordFrequencies = new ArrayList<>();//直接处理显示词语

	        String [] books = {"Spring实战","Spring源码深度解析","SpringBoot实战",
	                "SpringBoot2精髓","一步一步学SpringBoot2","Spring微服务实战",
	                "Head First Java","Java并发编程实战","深入理解Java 虚拟机",
	                "Head First Design","effective java","J2EE development without EJB",
	                "TCP/IP卷一"," 计算机网络：自顶向下","图解HTTP和图解TCP/IP",
	                "计算机网络","深入理解计算机系统","现代操作系统",
	                "Linux内核设计与实现","Unix网络编程","数据结构与算法",
	                "算法导论","数据结构与算法（Java版）","算法图解，啊哈算法",
	                "剑指offer","LeetCode"," Java编程思想",
	                "Java核心技术卷一","深入理解JVM虚拟机","Java并发编程实战",
	                " Java并发编程艺术","Java性能调优指南","Netty权威指南",
	                "深入JavaWeb技术内幕","How Tomcat Works","Tomcat架构解析",
	                "Spring实战","Spring源码深度解析","Spring MVC学习指南",
	                "Maven实战","sql必知必会","深入浅出MySQL",
	                "Spring cloud微服务实战","SpringBoot与Docker微服务实战","深入理解SpringBoot与微服务架构"
	        };

	        for (String book : books){
	            wordFrequencies.add(new WordFrequency(book,new Random().nextInt(books.length)));
	        }


	        //此处不设置会出现中文乱码
	        java.awt.Font font = new java.awt.Font("STSong-Light", 2, 18);

	        final Dimension dimension = new Dimension(900, 900);
	        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
	        wordCloud.setPadding(2);
	        wordCloud.setBackground(new CircleBackground(255));
	        wordCloud.setFontScalar(new SqrtFontScalar(12, 42));
	        //设置词云显示的三种颜色，越靠前设置表示词频越高的词语的颜色
	        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));

	        wordCloud.setKumoFont(new KumoFont(font));
	        wordCloud.setBackgroundColor(new Color(255, 255, 255));
	        //因为我这边是生成一个圆形,这边设置圆的半径
	        wordCloud.setBackground(new CircleBackground(255));

	        wordCloud.build(wordFrequencies);
	        wordCloud.writeToFile("s://6.png");
	    }

}
