package com.iflytek.util;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by YUAN on 2016-09-17.
 */
public class pcm_client {
    
    private Socket socket;
    private SocketAddress address;

    public pcm_client() {
        try {
            socket = new Socket();
            address = new InetSocketAddress("127.0.0.1", 5020);
            socket.connect(address, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void pcmsend(String filename,String path) {
    	File_ways file_ways = new File_ways();//声明一个文件处理对象
        try {

            //使用DataInputStream封装输入流
        	FileInputStream fis = new FileInputStream(path+filename);
            InputStream os = new DataInputStream(fis);//用于读出待发送的文件
            
            byte [] b = new byte[1];
            byte [] xxx = new byte[1];
            //DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); //用于发送给服务端
            while (-1 != os.read(b)) {
              //  dos.write(b); // 发送给客户端
            	xxx[0] = b[0];
            	System.out.print(b);
            	//file_ways.write_byte("C:\\\\Users\\\\24349\\\\eclipse-workspace\\\\voice_serve1.0\\\\test",new String(xxx), "125.txt");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
 
        }
    }
    public void talk() {
    	
        try {

            //使用DataInputStream封装输入流
        	FileInputStream fis = new FileInputStream("C:\\Users\\24349\\eclipse-workspace\\voice_serve1.0\\test\\test.pcm");
            InputStream os = new DataInputStream(fis);
            
            byte [] b = new byte[1];
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            while (-1 != os.read(b)) {
                dos.write(b); // 发送给客户端
                
            }
            
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
    }

    public static void main(String[] args) {
    	//pcm_client client = new pcm_client();
        pcmsend("125.pcm","C:\\Users\\24349\\eclipse-workspace\\voice_serve1.0\\pcmdata\\");
    }

}