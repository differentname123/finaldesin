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

public class test1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	      //������Ƶ�����������ô�Ƶ���Լ�������̳��ȣ��˴��Ĳ��������������������
        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(600);
        frequencyAnalyzer.setMinWordLength(2);
 
        //�������Ľ�����
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
	//ָ���ı��ļ�·�������ɴ�Ƶ����
        List<WordFrequency> wordFrequencyList = null;
		try {
			wordFrequencyList = frequencyAnalyzer.load("C:\\Users\\24349\\Desktop\\temp\\test.txt");//û�н��зִ�
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//����ͼƬ�ֱ���
        Dimension dimension = new Dimension(1920,1080);
	//�˴������ò������ó������ɣ����ɴ��ƶ���
        WordCloud wordCloud = new WordCloud(dimension,CollisionMode.PIXEL_PERFECT);
        //���ñ߽缰����
	wordCloud.setPadding(2);
        java.awt.Font font = new java.awt.Font("STSong-Light", 2, 20);
	//���ô�����ʾ��������ɫ��Խ��ǰ���ñ�ʾ��ƵԽ�ߵĴ������ɫ
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));
        wordCloud.setKumoFont(new KumoFont(font));
	//���ñ���ɫ
        wordCloud.setBackgroundColor(new Color(255,255,255));
	//���ñ���ͼƬ
        //wordCloud.setBackground(new PixelBoundryBackground("E:\\����/google.jpg"));
	//���ñ���ͼ��ΪԲ��
	wordCloud.setBackground(new CircleBackground(255));
        wordCloud.setFontScalar(new SqrtFontScalar(12, 45));
	//���ɴ���
        wordCloud.build(wordFrequencyList);
        wordCloud.writeToFile("s://7.png");
	}

}
