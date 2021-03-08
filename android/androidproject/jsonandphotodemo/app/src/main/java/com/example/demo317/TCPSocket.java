package com.example.demo317;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

//import org.json.JSONObject;
import net.sf.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TCPSocket {
    private ExecutorService mThreadPool;
    public Socket socket;
    private InputStreamReader isr;
    private String response;
    private InputStream is;
    private BufferedReader br;
    public Bitmap bmp = null;
    private ImageView imageView = null;//定义图片变量
    private OutputStream outputStream;
    private Lock lock = new ReentrantLock();

    public TCPSocket() throws IOException {


        // 初始化线程池
        lock.lock();//上锁
        mThreadPool = Executors.newCachedThreadPool();
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    Log.d("TCP", String.valueOf(socket));
                    // 创建Socket对象 & 指定服务端的IP 及 端口号
                    socket = new Socket("127.0.0.1", 12345); //服务器IP及端口
                    Log.d("TCP", "11" + String.valueOf(socket));
                    // 判断客户端和服务器是否连接成功
                    System.out.println(socket.isConnected());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        lock.unlock();
    }

    public synchronized void tcpsendstring(final String mes) {//发送字符串

                try {
                    Log.d("TCP77", String.valueOf(socket));
                    while (socket == null)//等待初始化完成
                    {

                    }
                    // 步骤1：从Socket 获得输出流对象OutputStream
                    // 该对象作用：发送数据
                    outputStream = socket.getOutputStream();

                    // 步骤2：写入需要发送的数据到输出流对象中
                    outputStream.write((mes + "\n").getBytes());
                    // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞

                    // 步骤3：发送数据到服务端
                    outputStream.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }



    }


    public void tcpjsonrecieve() throws IOException {
        
        isr=new InputStreamReader(socket.getInputStream());
        br=new BufferedReader(isr);
        String str=br.readLine();

    }


    public synchronized Bitmap tcprecievephoto() {
                    bmp=null;
                try {

                    Log.d("TCP1", "88");
                    tcpsendstring("4442");
                    while(socket==null);
                    DataInputStream dataInput = new DataInputStream(socket.getInputStream());
                    int size = dataInput.readInt();
                    Log.d("TCP1", String.valueOf(size));
                    byte[] data = new byte[size];
                    int len = 0;
                    while (len < size) {
                        len += dataInput.read(data, len, size - len);
                    }
                    ByteArrayOutputStream outPut = new ByteArrayOutputStream();
                    bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, outPut);
                    dataInput.close();
                    outPut.close();

                    //imageView.setImageBitmap(bmp);
                    // handler.post(runnableUi);//使用主线程改变控件imageview
                    //Bitmap bitmap = BitmapFactory.decodeStream(dataInput);
                    //myHandler.obtainMessage().sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                }

        Log.d("TCP6", String.valueOf(bmp));
        return bmp;

            }

    public synchronized void tcprecievejson() {
        bmp=null;
        try {

            Log.d("TCP1", "进入json接收");
            tcpsendstring("01");
            while(socket==null);
            Log.d("TCP1", "进入");
            DataInputStream in;
            String str;
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            str=in.readUTF();

            System.out.println("tcp输入信息为："+str);

            JSONObject js =JSONObject.fromObject(str);
            Log.d("TCP1jiesh", "进入json接收"+js.getString("string"));
/*
          DataInputStream dataInput = new DataInputStream(socket.getInputStream());
            int size = dataInput.readInt();
            Log.d("TCP1", String.valueOf(size));
            byte[] data = new byte[size];
            int len = 0;
            while (len < size) {
                len += dataInput.read(data, len, size - len);
            }

            Log.d("TCP1接收到的数据", ""+data);
            ByteArrayOutputStream outPut = new ByteArrayOutputStream();
            System.out.println("类容 = " + data);
            JSONObject jsonobject =JSONObject.fromObject(data.toString());
            Log.d("TCP1jiesh", "进入json接收"+jsonobject.getString("string"));
            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outPut);
            dataInput.close();
            outPut.close();*/

            //imageView.setImageBitmap(bmp);
            // handler.post(runnableUi);//使用主线程改变控件imageview
            //Bitmap bitmap = BitmapFactory.decodeStream(dataInput);
            //myHandler.obtainMessage().sendToTarget();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("TCP6", String.valueOf(bmp));

    }



    public synchronized void tcpreceive() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {


                    try {
                        while(socket==null);
                        tcpsendstring("01");
                        // 步骤1：创建输入流对象InputStream
                        is = socket.getInputStream();

                        // 步骤2：创建输入流读取器对象 并传入输入流对象
                        // 该对象作用：获取服务器返回的数据
                        isr = new InputStreamReader(is);
                        br = new BufferedReader(isr);
                        Log.d("TCP准备读取", "response");
                        // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                        response = br.readLine();

                        // 步骤4:通知主线程,将接收的消息显示到界面
                        Log.d("TCP数据", response);
                        is.close();
                        isr.close();
                        br.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }




            }
        });




    }
}

