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
    private Button connect,word_cloud,xinqing,future,report,other;//定义按钮变量
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
        /*
        SpringIndicator springIndicator = (SpringIndicator) findViewById(R.id.indicator);
        PagerModelManager manager = new PagerModelManager();
        manager.addCommonFragment(GuideFragment.class, getBgRes(), getTitles());
        viewPager = (ScrollerViewPager) findViewById(R.id.view_pager);
        springIndicator.setViewPager(viewPager);
        */



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
                            socket = new Socket("192.168.171.115", 12345); //服务器IP及端口
                            // 判断客户端和服务器是否连接成功
                            System.out.println(socket.isConnected());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });


            }
        });


        //功能一
        word_cloud = (Button) findViewById(R.id.word_cloud);
        word_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//接收图片数据并且改变界面图片

                Intent intent = new Intent(MainActivity.this,word_cloud.class);
                startActivity(intent);


            }
        });
        //功能二
        xinqing= (Button) findViewById(R.id.xinqing);
        xinqing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,xinqing.class);
                startActivity(intent);

            }
        });
        //功能三
        future = (Button) findViewById(R.id.future);
        future.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,future.class);
                startActivity(intent);
            }
        });
        // 功能四
        report = (Button) findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 接收json数据
                Intent intent = new Intent(MainActivity.this,report.class);
                startActivity(intent);


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
