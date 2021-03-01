package com.iflytek.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.Setting;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUtility;


public class Voice_to_String {
	
	private static final String APPID="5fed5ca2";//这里是自己的APPID
	private static Voice_to_String mObject;
	private static StringBuilder mResult=new StringBuilder();
	static String mstr ="";
	static String path ="";
	static String fileName="test.pcm";//这里要将文件拷贝至根目录下，必须是.pcm文件
	int count=0;
	//创建字节数组，长度为64K
	byte[] data=new byte[32*1024];
	int len=data.length;//此时为64*1024即有这么长
	static boolean covertflag = false;
	
	//main方法，是否显示日志，语音实用程序验证程序的id
	public static void main(String[] args) {
		if(null!=args&&args.length>0&&args[0].equals("true")) {
			
			Setting.setShowLog(true);
		}

		//SpeechUtility.createUtility( SpeechConstant.APPID +"=" +APPID);
		//getVoiceObj().Recognize();
		convert(APPID,"test.pcm",path);
	}
	
	public static String convert(String appid,String filename,String path1)
	{
		
		fileName = filename;
		path = path1;
		SpeechUtility.createUtility( SpeechConstant.APPID +"=" +appid);
		getVoiceObj().Recognize();
		Thread.currentThread();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	DebugLog.Log("最终结果： "+mstr);
		return mstr;
	}
	//单例模式，创建对象
	private static Voice_to_String getVoiceObj() {
		if(mObject==null) {
			mObject=new Voice_to_String();
		}
		return mObject;
	}
	

	//建立语音识别对象
	private boolean mIsEndOfSpeech=false;
	private void Recognize() {
		if(SpeechRecognizer.getRecognizer()==null) {
			SpeechRecognizer.createRecognizer();
		}
		mIsEndOfSpeech=false;
		RecogizePcmFileBite();
	}

	//识别音频文件
	private void RecogizePcmFileBite() {
		//获取语音识别对象
		SpeechRecognizer recognizer=SpeechRecognizer.createRecognizer();
		//设置基本的识别参数,声音来源是音频，结果是自然语言文本
		recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
		recognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
		//开始监听,参数是监听器对象
		recognizer.startListening(recListener);
		//创建文件输入流
		FileInputStream fis=null;
		
		mstr ="";
		byte[] data=new byte[32*1024];
		int len=data.length;//此时为64*1024即有这么长

		try {
			fis=new FileInputStream(new File(path+"\\"+fileName));
			//文件剩余长度如果没有，就显示没有了
			if(0==fis.available()) {
				mResult.append("no audio avaible!");
				//取消语音识别
				recognizer.cancel();
			//否则有语音文件
			}else {
				
				
				while(data.length==len) {
					if(mIsEndOfSpeech)
					{
						recognizer.startListening(recListener);
						//DebugLog.Log("重新开始");
						mIsEndOfSpeech = false;

					}
					//读取文件
					len=fis.read(data);
					//写出文件
					recognizer.writeAudio(data, 0, len);
					count++;
					//DebugLog.Log(""+"  flag:  "+mIsEndOfSpeech+"number:  "+ count+" shuru  "+len);
					Thread.currentThread();//等待是否出现一句话
					Thread.sleep(1000);
					
				}
				count=0;
				//停止语音识别
				recognizer.stopListening();
				
			}
		
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(null !=fis) {
					fis.close();
					fis=null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	//听写监听器
	private RecognizerListener recListener=new RecognizerListener() {
		@Override
		public void onBeginOfSpeech() {
			DebugLog.Log("onBeginOfSpeech enter");
			DebugLog.Log("*****开始录音*****");
			
		}
		
		@Override
		public void onVolumeChanged(int volume) {
		//	DebugLog.Log( "onVolumeChanged enter" );
			//if (volume > 0)
			//	DebugLog.Log("*************音量值:" + volume + "*************");
			
		}
		
		@Override
		public void onResult(RecognizerResult result, boolean isLast) {
		//	DebugLog.Log( "onResult enter" );
			//获取监听结果的字符串
			mResult.append(result.getResultString());
			//如果是结尾
			if(isLast) {
				//DebugLog.Log("识别结果为："+mResult.toString());
				mstr+=mResult.toString();
				mIsEndOfSpeech=true;
				
				mResult.delete(0, mResult.length());
				
				if(data.length!=len)
				{
					covertflag=true;
				//	DebugLog.Log("标志改变："+len);
				}
				
			}
		}
		
		@Override
		public void onEvent(int arg0, int arg1, int arg2, String arg3) {
			// TODO Auto-generated method stub
			//DebugLog.Log(""+"arg0: "+arg0+"  arg1:  "+arg1+"  arg2:  "+arg2 +"  arg3:  "+arg3);
		}
		
		@Override
		public void onError(SpeechError arg0) {
			// TODO Auto-generated method stub
			DebugLog.Log("*****cuowu*****"+arg0);
			
		}
		
		@Override
		public void onEndOfSpeech() {
			//DebugLog.Log("onEndOfSpeech enter");
			//DebugLog.Log("*****结束录音*****");
			//mIsEndOfSpeech=true;
			
		}
		
	};
	
	

}

