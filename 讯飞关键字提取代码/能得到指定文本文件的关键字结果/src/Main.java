import java.io.UnsupportedEncodingException;

public class Main {

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		System.out.println("开始测试");
		final String appid = "5fed5ca2";
		// �ӿ���Կ
		final String api_key = "31c6c1836c5c2946fdf18ca5a9fddc5d";
		key_word kword=new key_word();
		//System.out.println("���Խ��"+kword.get_string("aa.txt"));
		System.out.println("测试结果"+kword.get_keyword("aa.txt", appid, api_key));
		//  kword.get_keyword("aa.txt", appid, api_key)返回指定文件的每一行的关键字结果，可能会有重复，需要去重处理
	}

}
