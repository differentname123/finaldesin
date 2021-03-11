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
			System.out.println("���������������˿ںţ�" + 1234);
			String curTime = System.currentTimeMillis() / 1000L + "";
			int endtime = Integer.parseInt(curTime);
			
			//get_recent_top_words.get_top_words(endtime, 4);
			Socket socket;
			//ͨ������ѭ�������ͻ�������
			//���û�пͻ��˽��룬��������accept�����ϡ�
				socket = server.accept();
				System.out.println(socket+"���ӳɹ�");
				//�����µĿͻ��˽���ʱ����ִ������Ĵ���
				//Ȼ�󴴽�һ���µ��̴߳�������Socket��·
				//executorService.execute(new ServerHandler(socket));
	        
	        
	        final List<WordFrequency> wordFrequencies = new ArrayList<>();//ֱ�Ӵ�����ʾ����

	        String [] books = {"Springʵս","SpringԴ����Ƚ���","SpringBootʵս",
	                "SpringBoot2����","һ��һ��ѧSpringBoot2","Spring΢����ʵս",
	                "Head First Java","Java�������ʵս","�������Java �����",
	                "Head First Design","effective java","J2EE development without EJB",
	                "TCP/IP��һ"," ��������磺�Զ�����","ͼ��HTTP��ͼ��TCP/IP",
	                "���������","�����������ϵͳ","�ִ�����ϵͳ",
	                "Linux�ں������ʵ��","Unix������","���ݽṹ���㷨",
	                "�㷨����","���ݽṹ���㷨��Java�棩","�㷨ͼ�⣬�����㷨",
	                "��ָoffer","LeetCode"," Java���˼��",
	                "Java���ļ�����һ","�������JVM�����","Java�������ʵս",
	                " Java�����������","Java���ܵ���ָ��","NettyȨ��ָ��",
	                "����JavaWeb������Ļ","How Tomcat Works","Tomcat�ܹ�����",
	                "Springʵս","SpringԴ����Ƚ���","Spring MVCѧϰָ��",
	                "Mavenʵս","sql��֪�ػ�","����ǳ��MySQL",
	                "Spring cloud΢����ʵս","SpringBoot��Docker΢����ʵս","�������SpringBoot��΢����ܹ�"
	        };

	        for (String book : books){
	            wordFrequencies.add(new WordFrequency(book,new Random().nextInt(books.length)));
	        }


	        //�˴������û������������
	        java.awt.Font font = new java.awt.Font("STSong-Light", 2, 18);

	        final Dimension dimension = new Dimension(900, 900);
	        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
	        wordCloud.setPadding(2);
	        wordCloud.setBackground(new CircleBackground(255));
	        wordCloud.setFontScalar(new SqrtFontScalar(12, 42));
	        //���ô�����ʾ��������ɫ��Խ��ǰ���ñ�ʾ��ƵԽ�ߵĴ������ɫ
	        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));

	        wordCloud.setKumoFont(new KumoFont(font));
	        wordCloud.setBackgroundColor(new Color(255, 255, 255));
	        //��Ϊ�����������һ��Բ��,�������Բ�İ뾶
	        wordCloud.setBackground(new CircleBackground(255));

	        wordCloud.build(wordFrequencies);
	        wordCloud.writeToFile("s://6.png");
	    }

}
