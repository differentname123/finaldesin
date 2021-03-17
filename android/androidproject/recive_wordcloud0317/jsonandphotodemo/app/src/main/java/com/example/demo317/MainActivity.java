package com.example.demo317;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private Button connect,topk,xinqing,future,report,other;//定义按钮变量
    private int progress = 0;
    private Toast toast = null;
    private ImageView imageView = null;//定义图片变量
    private ExecutorService mThreadPool;//声明线程池
    private Socket socket;//声明socket变量
    private InputStreamReader isr;
    private String response;
    private InputStream is;
    private TCPSocket tcpsocket;
    private BufferedReader br;
    private OutputStream outputStream;
    public Bitmap bmp = null;
    private Handler handler=null;//用于线程改变控件
    private String uploadFile="/mnt/sdcard/qt.png";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // 1、创建Handler对象（此处创建于主线程中便于更新UI）。

        final TongXing tongxing = new TongXing();//声明通信的对象

        //2、构建Runnable对象，在Runnable中更新界面。
        // 3、在子线程的run方法中向UI线程post，runnable对象来更新UI。
        handler=new Handler();

        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();



        connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 连接服务器
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            // 创建Socket对象 & 指定服务端的IP 及 端口号
                            socket = new Socket("192.168.31.108", 12345); //服务器IP及端口
                            // 判断客户端和服务器是否连接成功
                            System.out.println(socket.isConnected());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });


            }
        });



        topk = (Button) findViewById(R.id.topk);
        topk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//接收图片数据并且改变界面图片

                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        String curTime = System.currentTimeMillis() / 1000L + "";
                        int Timename = Integer.parseInt(curTime);
                        tongxing.get_word_cloud(Timename);
                    }
                });


            }
        });

        xinqing= (Button) findViewById(R.id.xinqing);
        xinqing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String curTime = System.currentTimeMillis() / 1000L + "";
                int Timename = Integer.parseInt(curTime);
                tongxing.get_xinqing(Timename);

            }
        });

        future = (Button) findViewById(R.id.future);
        future.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 接收json数据
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        String curTime = System.currentTimeMillis() / 1000L + "";
                        int Timename = Integer.parseInt(curTime);
                        tongxing.get_future(Timename);
                    }



                });


            }
        });

        report = (Button) findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 接收json数据
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        String curTime = System.currentTimeMillis() / 1000L + "";
                        int Timename = Integer.parseInt(curTime);
                        tongxing.get_report(Timename);
                    }



                });


            }
        });

        other = (Button) findViewById(R.id.other);
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,word_cloud.class);
                startActivity(intent);


            }
        });

    }

    Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            imageView.setImageBitmap(bmp);
            bmp=null;
        }

    };

}
