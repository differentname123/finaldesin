package com.iflytek.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSONObject;

public class File_ways {

	
	public void write_string(String path,String data,String name)//往指定文件覆盖的写入数据
	{
		String filename = path +"\\" + name;
		
		File folder = new File(path);
		if (!folder.exists()) {

		    folder.mkdirs();

		    System.out.println("创建文件夹");

		} else {

		    System.out.println("文件夹已存在");

		}
		
		try {
            File writeName = new File(filename); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
            	String[] temp = data.split(" ");// .号需要进行转义  加上右双斜杠
            	for(int i=0;i<temp.length;i++)
            	{
            		out.write(temp[i]+"\r\n"); // \r\n即为换行
            	}
                
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public String init(String path,String Filename)//创建一个空的指定文件
	{
		String result ="";
		String filename = path + "\\"+Filename;//记录最近转换完成的文件名
		
		
		File folder = new File(path);
		if (!folder.exists()) {

		    folder.mkdirs();

		    System.out.println("创建文件夹");

		} else {

		    System.out.println("文件夹已存在");

		}
		File file = new File(filename);
		if(file.exists())
		{
			file.delete();
		}// 创建新文件,有同名的文件的话直接覆盖
		return result;
		
	}
	
	public String init1(String path,String Filename)//创建一个空的指定文件
	{
		String result ="";
		String filename = path + "\\"+Filename;//记录最近转换完成的文件名
		
		
		File folder = new File(path);
		if (!folder.exists()) {

		    folder.mkdirs();

		    System.out.println("创建文件夹");

		} else {

		    System.out.println("文件夹已存在");

		}
		return result;
		
	}

	public String get_string(String path,String filename)//一行一行读取指定文件，并以json格式存放，最后返回json转的string类型
	{
		JSONObject object = new JSONObject();
		String result = "";
		filename = path+"\\"+filename;
		BufferedReader reader = null;
        try {

        	reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
            //System.out.println("以行为单位读取文件内容，一次读一整行：");
            String tempString = null;
            int line = 0;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	if(tempString.length()!=0)//判断是否为空换行
            	{
                	object.put(""+line, tempString);
                    //System.out.println("line " + line +"size: "+tempString.length()+ ": " + tempString);
                    line++;
            	}

            }
            reader.close();
            object.put("linecount", line);
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
        result = object.toString();
		return result;
		
	}
	
	public String get_recent_name(String path,String Filename)//读取指定路径下文件的第一行内容
	{
		String result ="";
		String filename = path + "\\"+Filename;//记录最近转换完成的文件名
		
		
		File folder = new File(path);
		if (!folder.exists()) {

		    folder.mkdirs();

		    System.out.println("创建文件夹");

		} else {

		   // System.out.println("文件夹已存在");

		}
		
		File fi = new File(filename);
		if(!fi.exists())
		{
			return "0";
		}
		BufferedReader reader = null;
        try {

        	reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
            // 一次读入一行，直到读入null为文件结束
            result = reader.readLine();
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
        if(result==null)
        	return "0";
		return result;
	}
	public void write_recent_name(String path,String data,String Filename)//以覆盖的方式写入新的数据
	{
		String filename = path +"\\"+Filename;
		
		
		
		File folder = new File(path);
		if (!folder.exists()) {

		    folder.mkdirs();

		    System.out.println("创建文件夹");

		} else {

		    System.out.println("文件夹已存在");

		}
		try {
            File writeName = new File(filename); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(data+"\r\n"); // \r\n即为换行
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public  void write_data(String path,String data,String Filename)//往指定文件添加写入data并换行 不覆盖原来内容
	{
		String filename = path +"\\"+Filename;

		File folder = new File(path);
		if (!folder.exists()) {

		    folder.mkdirs();

		    System.out.println("创建文件夹");

		} else {

		    System.out.println("文件夹已存在");

		}
		
		try {
            File writeName = new File(filename); // 相对路径，如果没有则要建立一个新的output.txt文件
            if(!writeName.exists())
            {
            	writeName.createNewFile(); // 创建新文件
            }
            
            try (FileWriter writer = new FileWriter(writeName,true);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(data+"\r\n"); // \r\n即为换行
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
