package again;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.dom4j.DocumentException;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.palette.LinearGradientColorPalette;
 
import java.io.IOException;

public class jieshi {
	 public static void main(String[] args) throws IOException {
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
	        Dimension dimension = new Dimension(1152, 648);
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
	        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.pink, Color.BLUE, Color.cyan, 30, 30));
	        wordCloud.setFontScalar(new LinearFontScalar(5, 40));
	        wordCloud.build(wordFrequencies);
	        wordCloud.writeToFile("S:\\031509.png");
	    }
}
