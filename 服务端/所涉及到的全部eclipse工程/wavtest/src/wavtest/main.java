package wavtest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
 
public class main {
 
    /**
     * @param args
     */
	
	   /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
	 * @throws Exception 
     */
	
    public static int covert1(String content){
        int number=0;
        String [] HighLetter = {"A","B","C","D","E","F"};
        Map<String,Integer> map = new HashMap<>();
        for(int i = 0;i <= 9;i++){
            map.put(i+"",i);
        }
        for(int j= 10;j<HighLetter.length+10;j++){
            map.put(HighLetter[j-10],j);
        }
        String[]str = new String[content.length()];
        for(int i = 0; i < str.length; i++){
            str[i] = content.substring(i,i+1);
        }
        for(int i = 0; i < str.length; i++){
            number += map.get(str[i])*Math.pow(16,str.length-1-i);
        }
        return number;
    }
    
	static int covert(String str)
	{
		return Integer.parseInt(str,16);
	}
    public static void readFileByLines(String fileName) throws Exception {
        File file = new File(fileName);
        BufferedReader reader = null;
        FileOutputStream fileOut = new FileOutputStream("C:\\\\Users\\\\24349\\\\Documents\\\\new1.wav");
        DataOutputStream dataOut = new DataOutputStream(fileOut);
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	System.out.println("line " + line + ": " + tempString);
            	String[] strarray=tempString.split(" ");
            	for(int i=0;i<strarray.length;i++)
            	{
            		if(strarray[i]!="")
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
    static void ReadAndWrite()
    {
        // 1、创建一个文件对象
        File file = new File("C:\\Users\\24349\\Documents\\Track1.wav");
 
        // 2、使用字节流对象读入内存
 
        try {
            InputStream fileIn = new FileInputStream(file);
            //DataInputStream in = new DataInputStream(fileIn);
             
            // 使用缓存区读入对象效率更快
            BufferedInputStream in = new BufferedInputStream(fileIn);
             
             
            FileOutputStream fileOut = new FileOutputStream("C:\\\\Users\\\\24349\\\\Documents\\\\new.wav");
            DataOutputStream dataOut = new DataOutputStream(fileOut);
             
            // 使用缓存区写入对象效率更快
            //BufferedOutputStream dataOut=new BufferedOutputStream(fileOut);
            int temp;
            while ((temp = in.read()) != -1) {
                dataOut.write(temp);
                System.out.print(" " + temp);
            }
 
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 
    }
    
    public static void main(String[] args) throws Exception {
    	//ReadAndWrite();
    	readFileByLines("C:\\Users\\24349\\Documents\\chang1.txt");
    }
    	

 
}