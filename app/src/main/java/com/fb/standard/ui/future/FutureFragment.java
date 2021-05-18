package com.fb.standard.ui.future;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.fb.standard.R;
import com.fb.standard.base.BaseFragment;
import com.fb.standard.base.TongXing;
import com.fb.standard.databinding.FragmentFutureBinding;
import com.fb.standard.view.ImageWithTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FutureFragment extends BaseFragment<FragmentFutureBinding, FutureViewModel> {
    public Bitmap bmp = null;
    private Handler handler=null;//用于线程改变控件
    String sug_str = "";
    int KongjianCount = 3;
    ArrayList<String> strArray = new ArrayList<String> ();
    ArrayList<Bitmap> bmpArray = new ArrayList<Bitmap> ();
    ImageWithTextView[] ImageWithTextView;
    String juzi = "我姓黄，红绿灯的黄",novel_juzi;

    public String get_uri(String str)// 解析并返回网址
    {
        String result;
        String TempStr[] = str.split("\\?\\?");
        if(TempStr.length<2)
            return "把握当下";
        result = TempStr[1];
        return result;

    }
    public String get_title(String str)// 解析并返回网址
    {

        String result;
        String TempStr[] = str.split("\\?\\?");
        if(TempStr.length<3)
            return "把握当下";
        result = TempStr[2];

        return result;

    }
    public String get_str(String str)// 解析并返回做了长度处理的字符串
    {
        int max_len = 40; //设置最大显示的字符长度
        String result;
        String TempStr[] = str.split("\\?\\?");
        result = TempStr[0];
        if(max_len<result.length())
        {
            result = result.substring(0,max_len);
            result += "...";
        }

        return result;

    }
    @Override
    protected void initData() {
        ExecutorService mThreadPool;//声明线程池
        mThreadPool = Executors.newCachedThreadPool();
        handler=new Handler();
        TongXing tongxing = new TongXing();//声明通信的对象
        binding.juzi.setMovementMethod(ScrollingMovementMethod.getInstance());
        String tempJuzi = "";//接收到的句子
        binding.fenge.setMovementMethod(ScrollingMovementMethod.getInstance());
//        binding.imageWithText.setTitle("Title");
//        binding.imageWithText.setContent("Content");
        ImageWithTextView =new ImageWithTextView[]{binding.imageWithText1,binding.imageWithText2,binding.imageWithText3};

        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                strArray.clear();
                bmpArray.clear();
                //接收图片数据
                String curTime = System.currentTimeMillis() / 1000L + "";
                int Timename = Integer.parseInt(curTime);
                String tempJuzi=tongxing.get_juzi("");
                String types = tongxing.get_future(Timename);
                types = types.substring(1,types.length()-1);
                String TempStr[] = types.split(",");
                int len = 5;
                if(TempStr.length >KongjianCount)
                    len = TempStr.length;
                for(int i = 0;i<len;i++)
                {
                    String str_temp = TempStr[i%KongjianCount];
                    String final_str[] = str_temp.split(":");

                    if(final_str[1].length() > 1)
                    {
                        String type = final_str[1];
                        sug_str = tongxing.get_sug(type);
                        strArray.add(sug_str);
                        bmp = tongxing.get_sug_photo(type);
                        bmpArray.add(bmp);
                    }

                }
                juzi=tongxing.get_juzi("");
                handler.post(JuZi);  // 更新笑话
                handler.post(runnableUi);// 更行推荐的图片和文字

                // 分割的显示句子
                //novel_juzi=tongxing.get_novel_juzi();
                //handler.post(adjust_novel);



            }
        });
        binding.fenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        juzi=tongxing.get_juzi("");
                        handler.post(JuZi);

                    }
               });

            }
        });
        /*
        binding.fenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        novel_juzi=tongxing.get_novel_juzi();
                        handler.post(adjust_novel);

                    }
                });

            }
        });
        */
        binding.imageWithText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "https://www.cnblogs.com/kori/p/12496197.html";
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            }
        });
        for(int i = 0;i<KongjianCount;i++)
        {
            int finalI = i;
            ImageWithTextView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = get_uri(strArray.get(finalI));
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);

                }
            });
        }
    }

    @Override
    protected void initView(View root) {

    }

    @Override
    protected int getResId() {
        return R.layout.fragment_future;
    }

    @Override
    protected String setTitle() {
        return getResources().getString(R.string.title_future);
    }
    Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            for(int i = 0;i<KongjianCount;i++)
            {
                ImageWithTextView[i].setContent(get_str(strArray.get(i)));
                if(bmpArray.get(i) != null)
                    ImageWithTextView[i].setImageResource(bmpArray.get(i));
                ImageWithTextView[i].setTextResource(get_title(strArray.get(i)));
            }

        }
    };

    Runnable   JuZi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            binding.juzi.setText(juzi);
        }
    };
    Runnable   adjust_novel=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            binding.fenge.setText(novel_juzi);
        }
    };
}