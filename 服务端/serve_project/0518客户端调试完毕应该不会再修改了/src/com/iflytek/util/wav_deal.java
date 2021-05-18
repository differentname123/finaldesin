package com.iflytek.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

// 主要完成单片机传来的wav串口数据到wav文件的转换
public class wav_deal {
	static String WAV_ORIGIN_DATA_PATH = "./wav_origin_data";//存储原始文件的路径
	static String WAV_DATA_PATH = "./wav_data";//存储真正wav文件的路径
	static int covert(String str)
	{
		return Integer.parseInt(str,16);
	}
    public static void readFileByLines(String OriginFileName) throws Exception {
        File file = new File(WAV_ORIGIN_DATA_PATH + "//"+ OriginFileName);
        BufferedReader reader = null;
        String tempStr[] =OriginFileName.split("\\.");
        String wavName = tempStr[0] + ".wav";
        FileOutputStream fileOut = new FileOutputStream(WAV_DATA_PATH + "//"+ wavName);
        DataOutputStream dataOut = new DataOutputStream(fileOut);
        try {
            //System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	//System.out.println("line " + line + ": " + tempString);
            	String[] strarray=tempString.split(" ");
            	for(int i=0;i<strarray.length;i++)
            	{
            		if(strarray[i]!=""&&(strarray[i].length() == 2))
            			dataOut.write(covert(strarray[i]));
            		//System.out.println(covert(strarray[i]));
            	}
                
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

}
