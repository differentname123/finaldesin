package com.example.demo317;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class future extends AppCompatActivity {
    private TextView juzi,word_cloud,Receive,disconnect;//定义按钮变量
    private ExecutorService mThreadPool;//声明线程池
    TongXing tongxing = new TongXing();//声明通信的对象
    private Handler handler=null;//用于线程改变控件
    String tempJuzi = "";//接收到的句子
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future);
        mThreadPool = Executors.newCachedThreadPool();
        juzi = (TextView) findViewById(R.id.juzi);
        handler=new Handler();
        juzi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//接收图片数据并且改变界面图片

                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        //接收图片数据
                        String curTime = System.currentTimeMillis() / 1000L + "";
                        int Timename = Integer.parseInt(curTime);
                         tempJuzi=tongxing.get_juzi("");
                        handler.post(runnableUi);

                    }
                });



            }
        });
    }
   Runnable   runnableUi=new  Runnable(){
        @Override
       public void run(){
             juzi.setText(tempJuzi);
        }
   };

}
