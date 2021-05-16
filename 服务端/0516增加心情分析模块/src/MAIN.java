import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iflytek.util.Voice_to_String;
import com.iflytek.util.key_word;

public class MAIN {

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		/*
		String path = "C:\\\\Users\\\\24349\\\\eclipse-workspace\\\\voice_serve1.0\\\\pcmdata\\\\";
		String APPID="5fed5ca2";//这里是自己的APPID
		String fileName="1.pcm";//这里要将文件拷贝至根目录下，必须是.pcm文件
		String temp ="";
		System.out.print("66");
		temp = Voice_to_String.convert(APPID, fileName,path);//实现指定文件的转文本
		System.out.print(temp);
		*/
	
	 	String line = "[{\"score\":\"0.743\",\"word\":\"音频\"},{\"score\":\"0.721\",\"word\":\"录制\"},{\"score\":\"0.712\",\"word\":\"文件\"},{\"score\":\"0.648\",\"word\":\"进行\"},{\"score\":\"0.500\",\"word\":\"134\"}]";
		String pattern = "score";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(line);
		

		while(m.find())
		{
			int start = m.start();
			int index = start;
			int j,i;
			String score="";
			String word ="";
			System.out.println("第一个匹配值"+index);
			for(i = start+8;line.charAt(i)!='\"';i++)
			{
				score+=line.charAt(i);
			}
			j=i+10;
			for(i = j;line.charAt(i)!='\"';i++)
			{
				word+=line.charAt(i);
			}
			System.out.println(word+"    提取值"+score);
		}
		
		
		/*
		public int start()
		返回以前匹配的初始索引。
		2	public int start(int group)
		 返回在以前的匹配操作期间，由给定组所捕获的子序列的初始索引
		3	public int end()
		返回最后匹配字符之后的偏移量。
		4	public int end(int group)
		返回在以前的匹配操作期间，由给定组所捕获子序列的最后字符之后的偏移量。
		*/
		
		
		System.out.println("开始测试");
		final String appid = "5fed5ca2";
		final String api_key = "31c6c1836c5c2946fdf18ca5a9fddc5d";
		key_word kword=new key_word();
		//System.out.println("测试结果"+kword.get_keyword("134.txt", appid, api_key,"C:\\\\Users\\\\24349\\\\eclipse-workspace\\\\voice_serve1.0\\\\txtdata\\\\"));
		
		
		
	}

}
