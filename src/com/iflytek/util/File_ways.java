package com.iflytek.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.alibaba.fastjson.JSONObject;

public class File_ways {
	
	
	
	public String get_random_line(String path)// 获得指定路径下随机一个文件的一行数据
	{
		String result = "";
		 File file = new File(path);
		 String[] fileNameLists = file.list();  //这是不带绝对路径的文件名
		 int index = (int)(Math.random()*fileNameLists.length);
		 result = get_recent_name(path, fileNameLists[index]);
		return result ;
		
	}
	public void build_many_file(String originFile, String DesPath)// 将原文件的多行内容生成多行文件
	{
		creat_path(DesPath);
		
		
		String filename = originFile;
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
                	String TempStr[] = tempString.split("\\.");
                	String str[] = TempStr[1].split("，");
                	write_string(DesPath,TempStr[1], str[0] + ".txt");
                	
            	}

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
	public void creat_path(String path)//创建一个文件夹，如果存在就忽略
	{
		File folder = new File(path);
		if (!folder.exists()) {

		    folder.mkdirs();

		    System.out.println("创建文件夹");

		} else {

		    System.out.println("文件夹已存在");

		}
	}
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
	private static int[] method(int num) {
		int re[] = new int[8];
		if(num == 1 || num == 0) {
			re[0] = num ;
		}else{
			method(num/2);
			System.out.print(num%2);
		}
		return re;
		
	}

	public static  void write_byte(String path,String data,String Filename)//往指定文件添加写入data不换行 不覆盖原来内容
	{
		String filename = path +"\\"+Filename;
		File folder = new File(path);
		if (!folder.exists()) {

		    folder.mkdirs();

		  //  System.out.println("创建文件夹");

		} else {

		   // System.out.println("文件夹已存在");

		}
		
		try {
            File writeName = new File(filename); // 相对路径，如果没有则要建立一个新的output.txt文件
            if(!writeName.exists())
            {
            	try {
					writeName.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 创建新文件
            }
            File file = writeName;    //1、建立连接
            OutputStream os = null;
            try {
                //2、选择输出流,以追加形式(在原有内容上追加) 写出文件 必须为true 否则为覆盖
                os = new FileOutputStream(file,true);    
//                //和上一句功能一样，BufferedInputStream是增强流，加上之后能提高输出效率，建议
//                os = new BufferedOutputStream(new FileOutputStream(file,true));
                byte[] databuff = data.getBytes();    //将字符串转换为字节数组,方便下面写入
               // System.out.println("文件长度: "+databuff.length);
                os.write(databuff, 0, databuff.length);    //3、写入文件
                os.flush();    //将存储在管道中的数据强制刷新出去
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("文件没有找到！");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("写入文件失败！");
            }finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("关闭输出流失败！");
                    }
                }
            }
        }finally
		{
        	
		}
    }
	
	public static String get_data(String path,String filename)//一行一行读取指定文件
	{
		String result = "";
		filename = path+"\\"+filename;
		String te = "";
		byte [] bytes;
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
            		String tempbu[] = tempString.split(" ");
            		for(int k = 0;k<tempbu.length;k++)
            		{
            			te = tempbu[k];
            			bytes = te.getBytes();
            			//("C:\\Users\\24349\\eclipse-workspace\\voice_serve1.0\\test",bytes,"test.txt");
            			System.out.println(bytes.length);
            		}
            	}

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
   
		return result;
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int input = -10;
		get_data("C:\\Users\\24349\\Documents","test11.txt");
		
	}

}