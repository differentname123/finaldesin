package com.anxpp.io.calculator.bio;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.iflytek.util.total_key_word;
import com.iflytek.util.total_voice_tostring;
/**
 * 测试方法
 * @author yangtao__anxpp.com
 * @version 1.0
 */
public class Test {
	//测试主方法
	//线程池 懒汉式的单例
	private static ExecutorService executorService = Executors.newFixedThreadPool(60);
	public static void main(String[] args) throws InterruptedException {
		//运行服务器
		new Thread(new Runnable() { //文件接收线程
			@Override
			public void run() {
				try {
					ServerBetter.start();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		new Thread(new Runnable() {  //PCM转str线程
			@Override
			public void run() {
				try {
					total_voice_tostring.start();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		new Thread(new Runnable() {  //str提取关键字线程
			@Override
			public void run() {
				try {
					total_key_word.start();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	

	
}
