package com.iflytek.util;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class send_recive {

	private static final String WORD_CLOUD_SAVEPATH = "./word_cloud_data";
	private static final String RECENT_PATH = "./data";
	private static final String WAVDATA_JILU = "wav_record.txx";
	
	private static final String WAVFILENAME_PATH = "./wavfilename";
	private static final String FIX_PATH = "./fix_path";
	
	private static final String TUIJIAN_RECORD = "./tuijian_record";// 存储最近发送推荐文字的路径
	static File_ways file_ways = new File_ways();
	
	public static void return_wav_time(Socket socket)//向终端返回最新的wav文件名
	{
		String result = "-1";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
    	String now_day = df.format(new Date());//获取当前时间
		String recentname = file_ways.get_recent_name(RECENT_PATH,WAVDATA_JILU);//获取最近的转换记录，方便下一次转换
		int recentname_num= Integer.parseInt(recentname);
		String file_temp = file_ways.get_string(WAVFILENAME_PATH, now_day + ".txt");
		JSONObject file_json = new JSONObject();
		file_json = JSON.parseObject(file_temp);
		for(int i = 0;i<file_json.getIntValue("linecount");i++) {
			String file_name = file_json.getString(""+i);
			int file_name_num= Integer.parseInt(file_name);
			if(file_name_num>recentname_num)
			{
				result = "" + file_name_num;
				break;
			}
			
		}
		System.out.print("待发送文件名为：" + result);
		send_str(socket,result);
		
	}
	
	public static void return_now__time(Socket socket)//向终端发送当前时间值
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
    	String now_day = df.format(new Date());//获取当前时间
		String curTime = System.currentTimeMillis() / 1000L + "";
		file_ways.write_data(WAVFILENAME_PATH, curTime, now_day + ".txt");// 将wav文件名写入以日期为名的文件
		System.out.print("返回时间为:" + curTime);
		send_str(socket,curTime);
		
	}
	   public static String bytesToHexString(byte[] src) {
	        StringBuilder stringBuilder = new StringBuilder("");
	        if (src == null || src.length <= 0) {
	            return null;
	        }
	        for (int i = 0; i < src.length; i++) {
	            int v = src[i] & 0xFF;
	            String hv = Integer.toHexString(v);
	            if (hv.length() < 2) {
	                stringBuilder.append(0);
	            }
	            stringBuilder.append(hv);
	        }
	        return stringBuilder.toString();
	    }
	    public static void doSomething(String ret) {
	        System.out.println(ret);
	    }
	public static boolean IsEnd(String LineStr)// 判断接收的wav串口文件是不是最后一行
	{
		return LineStr.contains("++AT");
	}
	public static void recive_wav(Socket socket, String filename, String path) throws Exception
	{
		System.out.println("文件名：" + filename);
		wav_deal wavdeal = new wav_deal();
		WavToPcm wavtopcm = new WavToPcm();
		int temp1;
		String tempStr;
		file_ways.creat_path(path);
		int count = 0;
		 {
            try {
            	 DataInputStream inFromClient = new DataInputStream(socket.getInputStream());
            	// 装饰流BufferedReader封装输入流（接收客户端的流）
                BufferedInputStream bis = new BufferedInputStream(
                        socket.getInputStream());
                FileOutputStream fs =new FileOutputStream(path + "//" + filename);
                DataOutputStream da = new DataOutputStream(fs);//用于写入收到的pcm数据
                DataInputStream dis = new DataInputStream(bis);
                byte[] bytes = new byte[1]; // 一次读取一个byte
                String ret = "";
                
                while((tempStr=dis.readLine())!=null)
                {
                	count++;
                	da.writeBytes(tempStr+"\n");
                	if (IsEnd(tempStr))
                		break;
                	//System.out.println(tempStr);
                }
                da.flush();
                da.close();
                System.out.println("接收完成：" + socket.getRemoteSocketAddress());
                wavdeal.readFileByLines(filename);
                String StrTemp[] = filename.split("\\.");
                file_ways.write_recent_name(RECENT_PATH,""+StrTemp[0],WAVDATA_JILU);//该进行记录的回写
                System.out.println("wav转换完成：" + filename);
                wavtopcm.WavToPcm(filename);
                System.out.println("pcm转换完成：" + filename);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                	
                    socket.close();
                
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
	}
	public static void photo_send(String str,Socket socket){
		//发送图片
		System.out.println("发送图片的地址为" + str);
		DataOutputStream dos;
		try {
			dos = new DataOutputStream(socket.getOutputStream());
			FileInputStream fis = new FileInputStream(str);
	        int size = fis.available();
			System.out.println("size = " + size);
	        byte[] data = new byte[size];
	        fis.read(data);
	        System.out.println("size = " + data.toString());
	        dos.writeInt(size);
	        dos.write(data);
	        dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
	}
	public static void send_photo(String filename,Socket socket)//想客户端发送filename的图片
	{
        try {

            //使用DataInputStream封装输入流
        	FileInputStream fis = new FileInputStream(filename);
            InputStream os = new DataInputStream(fis);//用于读出待发送的文件
            
            byte [] b = new byte[1];
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); //用于发送给客户端
            while (-1 != os.read(b)) {
                dos.write(b); // 发送给客户端
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
 
        }
	}
	public static void return_keyword(int time,int k,Socket socket)//向终端以降序json格式发送关键字
	{
		Map<String, Double> hashMap = get_recent_top_words.get_top_words(time, k);
		JSONObject jsonobject = new JSONObject();
		 Set<Entry<String, Double>> set = hashMap.entrySet();
	        Iterator<Entry<String, Double>> iterator = set.iterator();
	        while(iterator.hasNext()) {
	            Entry<String, Double> entry = iterator.next();
	            String key = (String) entry.getKey();
	            double value =entry.getValue();
	            jsonobject.put(key, value);
	        }
		System.out.println(jsonobject);
		send_str(socket,jsonobject.toString());
		
	}
	public static void return_word_cloud(int time,Socket socket)//向终端以图片形式发送词云
	{
		String result = word_cloud.word_cloud_produce(time);
		photo_send(WORD_CLOUD_SAVEPATH+"\\"+result,socket);
		//photo_send("./word_cloud_data\\1615965939.jpg",socket);
		
	}
	public static void return_juzi(String type,Socket socket)//向终端返回指定类型句子
	{
		Http_test http_test = new Http_test();
		String result = http_test.GetJuzi(type);
		send_str(socket,result);
		System.out.println("发送的句子为:" + result);

		
	}
	public static void return_novel_juzi(Socket socket)//向终端返回指定类型句子
	{
		Http_test http_test = new Http_test();
		String result = http_test.GetJuziNovel();
		send_str(socket,result);
		System.out.println("发送的句子为:" + result);

		
	}
	public static void return_mood_str(Socket socket)//向终端返回最近心情分析
	{
		mood_analysis xx = new mood_analysis();
		String result = xx.GetMoodAnalysis();
		send_str(socket,result);
		System.out.println("发送的句子为:" + result);

		
	}
	public static void return_sug(String type,Socket socket)//表示客户端向云端访问指定事情的推荐描述
	{
		String path = suggest.get_random_name(type);
		file_ways.write_recent_name(FIX_PATH, path, TUIJIAN_RECORD);
		String Str = file_ways.get_recent_name(path, "txt.txt");
		send_str(socket,Str);
		System.out.println("发送的句子为:" + Str);
	}
	public static void return_sug_photo(String path,Socket socket)//表示客户端向云端访问指定事情的推荐图片
	{
		path = file_ways.get_recent_name(FIX_PATH, TUIJIAN_RECORD);
		photo_send(path+"//"+"jpg (1).jpg",socket);
		System.out.println("发送的推荐图片地址为:" + path);
	}
	public static void return_xinqing(int time,Socket socket)//向终端以json嵌套json格式发送心情值
	{
		String result = get_recent_xinqing.get_xinqing(time);
		send_str(socket,result);
		
	}
	public static void return_future(int time,Socket socket)//向终端以json格式发送捕捉值
	{
		String result = get_recent_future.get_future(time);
		send_str(socket,result);
		
	}
	// 处理整体的分析数据，得到最终的展示字符串
	public static String DealStr(String str)
	{
		String result = "";
		String pie_data ="";
		String temp;
		JSONObject quest = new JSONObject();
		quest = JSON.parseObject(str);
		for(int i = 0;i<quest.getIntValue("linecount");i++)
		{
			pie_data += quest.getString(""+i) + ',';
			System.out.print(quest.getString(""+i) + ',');
		}
		
		int total_num = quest.getIntValue("totalnu");
		temp = "今天你一共说了 " + total_num + " 个词汇"; 
		result += temp + ".";
		
		
		
		
		String MaxStr = quest.getString("MaxStr");
		temp = "遇到投缘的人了吧！一口气说了 " + MaxStr + " 这么长的一句话";
		result += temp + ".";
		
		
		String fre_word = quest.getString("Freword");
		int fre_count = quest.getIntValue("Frequence");
		temp = "你应该很喜欢 " + fre_word + " 吧!" + "你一共提起了" + fre_count + "次";
		result += temp + ".";
		
		result += pie_data + ".";
		result += total_num;// 加上总的词数，方便计算饼状图比例
		return result;
		
	}
	public static void return_report(int time,Socket socket)//向终端以json格式发送捕捉值
	{
		String result = today_report.fenxi(time);
		result = DealStr(result);
		send_str(socket,result);
		
	}
	public static String produce_order(String order,String ID,int time, int k)//生成指定请求帧
	{
		JSONObject jsonobject = new JSONObject();
		jsonobject.put("from", 0);
		jsonobject.put("ID", ID);
		jsonobject.put("order", order);
		jsonobject.put("time", time);
		jsonobject.put("k", k);
		return jsonobject.toString();
	}
	public static void send_str(Socket socket,String data)
	{
		PrintWriter out = null;
		try {
			out = new PrintWriter(socket.getOutputStream(),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println(data);
		
		if(out != null){
			out.close();
			out = null;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Socket socket = build_link(DEFAULT_SERVER_IP,DEFAULT_SERVER_PORT);
		//send_str(socket,"number1");

	}

}
