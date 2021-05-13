package com.example.demo317;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
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


public class word_cloud extends AppCompatActivity {
    private Button button,word_cloud,Receive,disconnect;//定义按钮变量
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
    TongXing tongxing = new TongXing();//声明通信的对象
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_cloud);
        imageView = (ImageView) findViewById(R.id.imageView);
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        handler=new Handler();

        word_cloud = (Button) findViewById(R.id.word_cloud);
        word_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//接收图片数据并且改变界面图片

                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        //接收图片数据
                        String curTime = System.currentTimeMillis() / 1000L + "";
                        int Timename = Integer.parseInt(curTime);
                        bmp=tongxing.get_word_cloud(Timename);
                        Log.d("TCP2", String.valueOf(bmp));
                        while(bmp==null);
                        Log.d("TCP4", String.valueOf(bmp));

                        //显示图片
                        handler.post(runnableUi);//使用主线程改变控件imageview

                        Log.d("TCP5", String.valueOf(bmp));

                    }
                });


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
