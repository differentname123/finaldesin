import java.io.UnsupportedEncodingException;

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
		
		
		
		System.out.println("开始测试");
		final String appid = "5fed5ca2";
		final String api_key = "31c6c1836c5c2946fdf18ca5a9fddc5d";
		key_word kword=new key_word();
		System.out.println("测试结果"+kword.get_keyword("134.txt", appid, api_key,"C:\\\\Users\\\\24349\\\\eclipse-workspace\\\\voice_serve1.0\\\\txtdata\\\\"));
		
		
	}

}
