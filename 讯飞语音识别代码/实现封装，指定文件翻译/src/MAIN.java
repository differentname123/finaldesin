import com.iflytek.util.Voice_to_String;

public class MAIN {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String APPID="5fed5ca2";//这里是自己的APPID
		String fileName="test.pcm";//这里要将文件拷贝至根目录下，必须是.pcm文件
		String temp ="";
		System.out.print("66");
		temp = Voice_to_String.convert(APPID, fileName);//实现指定文件的转文本
		System.out.print(temp);

	}

}
