package com.fb.standard.ui.cloud;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fb.standard.R;
import com.fb.standard.base.BaseFragment;
import com.fb.standard.base.TCPSocket;
import com.fb.standard.base.TongXing;
import com.fb.standard.databinding.FragmentCloudBinding;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CloudFragment extends BaseFragment<FragmentCloudBinding, CloudViewModel> {
    private Button button,word_cloud,Receive,disconnect;//定义按钮变量
    private int progress = 0;
    Typeface typeface;
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
    public Bitmap bmp_background = null;
    private Handler handler=null;//用于线程改变控件
    TongXing tongxing = new TongXing();//声明通信的对象
    @Override
    protected void initData() {
        word_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//接收图片数据并且改变界面图片

                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {


                        Looper.prepare();
                        Toast.makeText(getContext(), "正在生成请稍后", Toast.LENGTH_SHORT).show();

                        //接收图片数据
                        String curTime = System.currentTimeMillis() / 1000L + "";
                        int Timename = Integer.parseInt(curTime);
                        bmp=tongxing.get_word_cloud(Timename);
                        bmp_background = tongxing.get_word_cloud_background();
                        Log.d("TCP2", String.valueOf(bmp));
                        while(bmp==null);
                        Log.d("TCP4", String.valueOf(bmp));

                        //显示图片
                        handler.post(runnableUi);//使用主线程改变控件imageview

                        Log.d("TCP5", String.valueOf(bmp));
                        Looper.loop();

                    }
                });


            }
        });
    }

    @Override
    protected void initView(View root) {
        imageView = binding.imageView;
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        handler=new Handler();

        word_cloud = binding.wordCloud;
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_cloud;
    }

    @Override
    protected String setTitle() {
        return getResources().getString(R.string.title_cloud);
    }
    Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            binding.lunkuo.setImageBitmap(bmp_background);
            binding.textView.setText("词云背景图");

            imageView.setImageBitmap(bmp);
            binding.textView1.setText("关键词词云");
            bmp=null;
            bmp_background = null;
        }

    };
}