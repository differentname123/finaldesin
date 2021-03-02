package com.iflytek;

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
import com.iflytek.util.DebugLog;


public class MscTest {
	
	private static final String APPID="5fed5ca2";//�������Լ���APPID
	private static MscTest mObject;
	private static StringBuilder mResult=new StringBuilder();
	private String fileName="test.pcm";//����Ҫ���ļ���������Ŀ¼�£�������.pcm�ļ�
	
	//main�������Ƿ���ʾ��־������ʵ�ó�����֤�����id
	public static void main(String[] args) {
		System.out.print("��ʾ����"+args.length);
		if(null!=args&&args.length>0&&args[0].equals("true")) {
			//��ʾ��־
			
			Setting.setShowLog(true);
		}
		//Setting.setShowLog(true);
		//SpeechUtility.createUtility("appid="+APPID);
		SpeechUtility.createUtility( SpeechConstant.APPID +"=" +APPID);
		getVoiceObj().Recognize();
	}
	
	//����ģʽ����������
	private static MscTest getVoiceObj() {
		if(mObject==null) {
			mObject=new MscTest();
		}
		return mObject;
	}
	

	//��������ʶ�����
	private boolean mIsEndOfSpeech=false;
	void Recognize() {
		if(SpeechRecognizer.getRecognizer()==null) {
			SpeechRecognizer.createRecognizer();
		}
		mIsEndOfSpeech=false;
		RecogizePcmFileBite();
	}

	//ʶ����Ƶ�ļ�
	private void RecogizePcmFileBite() {
		//��ȡ����ʶ�����
		SpeechRecognizer recognizer=SpeechRecognizer.createRecognizer();
		//���û�����ʶ�����,������Դ����Ƶ���������Ȼ�����ı�
		recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
		recognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
		//��ʼ����,�����Ǽ���������
		recognizer.startListening(recListener);
		//�����ļ�������
		FileInputStream fis=null;
		//�����ֽ����飬����Ϊ64K
		byte[] data=new byte[64*1024];
		try {
			fis=new FileInputStream(new File("./"+fileName));
			//�ļ�ʣ�೤�����û�У�����ʾû����
			if(0==fis.available()) {
				mResult.append("no audio avaible!");
				//ȡ������ʶ��
				recognizer.cancel();
			//�����������ļ�
			}else {
				int len=data.length;//��ʱΪ64*1024������ô��
				
				while(data.length==len&&!mIsEndOfSpeech) {
					System.out.print("\n11\n");
					//��ȡ�ļ�
					len=fis.read(data);
					//д���ļ�
					recognizer.writeAudio(data, 0, len);
				}
				//ֹͣ����ʶ��
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
	
	//��д������
	private RecognizerListener recListener=new RecognizerListener() {
		@Override
		public void onBeginOfSpeech() {
			DebugLog.Log("onBeginOfSpeech enter");
			DebugLog.Log("*****��ʼ¼��*****");
			
		}
		
		@Override
		public void onVolumeChanged(int volume) {
			DebugLog.Log( "onVolumeChanged enter" );
			if (volume > 0)
				DebugLog.Log("*************����ֵ:" + volume + "*************");
			
		}
		
		@Override
		public void onResult(RecognizerResult result, boolean isLast) {
			DebugLog.Log( "onResult enter" );
			//��ȡ����������ַ���
			mResult.append(result.getResultString());
			//����ǽ�β
			System.out.print("\n66\n");
			//if(isLast) {
				DebugLog.Log("ʶ����Ϊ��"+mResult.toString());
				mIsEndOfSpeech=true;
				mResult.delete(0, mResult.length());
				
			//}
			
		}
		
		@Override
		public void onEvent(int arg0, int arg1, int arg2, String arg3) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onError(SpeechError arg0) {
			// TODO Auto-generated method stub
			DebugLog.Log("*****cuowu*****");
			
		}
		
		@Override
		public void onEndOfSpeech() {
			DebugLog.Log("onEndOfSpeech enter");
			DebugLog.Log("*****����¼��*****");
			mIsEndOfSpeech=true;
			
		}
		
	};
	
	

}

