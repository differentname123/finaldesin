package com.fb.standard.ui.report;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.TextView;
import android.widget.Toast;

import com.fb.standard.R;
import com.fb.standard.adapter.ViewPagerAdapter;
import com.fb.standard.base.BaseFragment;
import com.fb.standard.base.TongXing;
import com.fb.standard.databinding.FragmentReportBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.viewpager.widget.ViewPager;

public class ReportFragment extends BaseFragment<FragmentReportBinding, ReportViewModel> {

    private ViewPagerAdapter adapter;
    String ReportStr = "";
    String ReportJuzi = "";
    TextView Report_juzi;
    Map<String, String> cixin_map = new HashMap<String,String>(); // 用于存储词性的英文和中文对照关系
    int Index = 0;
    double Min_Zhanshi = 0.027; // 饼状图能展示的最小比例
    int WaitTime = 3000;
    int TextCount = 3;// 待修改text的个数
    List<TextView> text_views = new ArrayList<>();
    private Handler handler=null;//用于线程改变控件

    @Override
    protected void initData() {


    }

    @Override
    protected void initView(View root) {
        text_views.clear();
        adapter = new ViewPagerAdapter();
        binding.viewPager.setAdapter(adapter);
        ExecutorService mThreadPool;//声明线程池
        handler=new Handler();
        TongXing tongxing = new TongXing();//声明通信的对象
        mThreadPool = Executors.newCachedThreadPool();
        List<View> views = new ArrayList<>();
        init_map();
        View page1 = LayoutInflater.from(getContext()).inflate(R.layout.page_text1,null);
        View page2 = LayoutInflater.from(getContext()).inflate(R.layout.page_text2,null);
        View page3 = LayoutInflater.from(getContext()).inflate(R.layout.page_text3,null);
        View page4 = LayoutInflater.from(getContext()).inflate(R.layout.page_pie,null);

        TextView text1 = page1.findViewById(R.id.text);
        TextView text2 = page2.findViewById(R.id.text);
        TextView text3 = page3.findViewById(R.id.text);
        text_views.add(page1.findViewById(R.id.text));
        text_views.add(page2.findViewById(R.id.text));
        text_views.add(page3.findViewById(R.id.text));
        PieChart pieChart = page4.findViewById(R.id.pieChart);
        Report_juzi = page4.findViewById(R.id.reportJuzi);
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();
                Toast.makeText(getContext(), "正在生成请稍后", Toast.LENGTH_SHORT).show();

                String curTime = System.currentTimeMillis() / 1000L + "";
                int Timename = Integer.parseInt(curTime);
                ReportStr=tongxing.get_report(Timename);
                ReportJuzi = tongxing.get_novel_juzi();
                handler.post(adjust_text);
                handler.post(JuZi);




                // 进行饼状图的生成
                String TempStr[] = ReportStr.split("\\.");
                String pie_data = TempStr[3];
                double total_num = Double.parseDouble(TempStr[4]);
                String single_date[] = pie_data.split(",");
                pieChart.setEntryLabelTextSize(20);
                List<PieEntry> pieEntries = new ArrayList<>();
                for(int i=0;i<single_date.length;i++)
                {
                    String key_value[] = single_date[i].substring(1,single_date[i].length()-1).split(":");
                    String dic_name = key_value[0];
                    int dic_count =  Integer.parseInt(key_value[1]);
                    if(dic_count/total_num > Min_Zhanshi)
                        pieEntries.add(new PieEntry(dic_count,GetChinese(dic_name)));
                }

                PieDataSet dataSet = new PieDataSet(pieEntries,"词性详细统计图");
                dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                dataSet.setValueLineColor(Color.BLACK);
                dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                dataSet.setValueTextSize(13);

                PieData data = new PieData();
                data.setValueTextColor(Color.WHITE);
                data.setDataSet(dataSet);
                pieChart.setData(data);
                pieChart.notifyDataSetChanged();
                pieChart.setTouchEnabled(false);

                Looper.loop();

            }
        });

        Report_juzi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        ReportJuzi=tongxing.get_novel_juzi();
                        handler.post(JuZi);

                    }
                });

            }
        });
        //while(ReportStr.split("\\.").length!=4);// 等待数据的接收完成 不然后面访问会出错


        views.add(page1);
        views.add(page2);
        views.add(page3);
        views.add(page4);

        adapter.setViews(views);
        adapter.notifyDataSetChanged();
        binding.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                clearDot();
                String TempStr[] = ReportStr.split("\\.");
                AnimationSet animationSet = new AnimationSet(true);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                switch (position){
                    case 0:
                        startAnimation(text1);

                        Index =0;
                        text1.setText(TempStr[Index]);
                        alphaAnimation.setDuration(WaitTime);
                        animationSet.addAnimation(alphaAnimation);
                        text1.startAnimation(animationSet);


                        binding.dot1.setImageResource(R.drawable.shape_circle_black);
                        break;
                    case 1:
                        startAnimation(text2);

                        Index = 1;
                        text2.setText(TempStr[Index]);
                        alphaAnimation.setDuration(WaitTime);
                        animationSet.addAnimation(alphaAnimation);
                        text2.startAnimation(animationSet);


                        binding.dot2.setImageResource(R.drawable.shape_circle_black);
                        break;
                    case 2:
                        startAnimation(text3);

                        Index = 2;
                        text3.setText(TempStr[Index]);
                        alphaAnimation.setDuration(WaitTime);
                        animationSet.addAnimation(alphaAnimation);
                        text3.startAnimation(animationSet);


                        binding.dot3.setImageResource(R.drawable.shape_circle_black);
                        break;
                    case 3:
                        startAnimation(pieChart);
                        binding.dot4.setImageResource(R.drawable.shape_circle_black);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public String GetChinese(String str)
    {
        String key = str.substring(1,str.length()-1);// 删除多余的两个引号
        String result = cixin_map.get(key);
        if (result == null)
        {
            result =str;
        }
        return result;
    }
    private void clearDot() {
        binding.dot1.setImageResource(R.drawable.shape_circle_gray);
        binding.dot2.setImageResource(R.drawable.shape_circle_gray);
        binding.dot3.setImageResource(R.drawable.shape_circle_gray);
        binding.dot4.setImageResource(R.drawable.shape_circle_gray);
    }


    @Override
    protected int getResId() {
        return R.layout.fragment_report;
    }

    @Override
    protected String setTitle() {
        return getResources().getString(R.string.title_report);
    }

    public void startAnimation(View view){
        PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofFloat("alpha",0f,1f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view,alphaHolder);
        objectAnimator.setDuration(200);
        objectAnimator.start();

    }
    public void init_map()
    {
        cixin_map.put("v","动词");
        cixin_map.put("r","代词");
        cixin_map.put("n","名词");
        cixin_map.put("ns","地名");
        cixin_map.put("wp","标点");
        cixin_map.put("k","后缀");
        cixin_map.put("h","前缀");
        cixin_map.put("u","助词");
        cixin_map.put("c","连词");
        cixin_map.put("p","介词");
        cixin_map.put("d","副词");
        cixin_map.put("q","量词");
        cixin_map.put("nh","人名");
        cixin_map.put("m","数词");
        cixin_map.put("e","语气词");
        cixin_map.put("b","状态词");
        cixin_map.put("a","形容词");
        cixin_map.put("nd","方位词");
        cixin_map.put("ni","处所词");
        cixin_map.put("o","拟声词");
        cixin_map.put("nt","时间词");
        cixin_map.put("nz","其他专名");
        cixin_map.put("nl","机构团体");
        cixin_map.put("i","成语");
        cixin_map.put("j","缩写词");
        cixin_map.put("ws","外来词");
        cixin_map.put("g","词素");
        cixin_map.put("x","非词位");

    }

    Runnable   JuZi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            Report_juzi.setText(ReportJuzi);
        }
    };
    Runnable   adjust_text=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            String TempStr[] = ReportStr.split("\\.");
            for(int i =0;i< TextCount;i++)
            {
                text_views.get(i).setText(TempStr[i]);
            }
            Report_juzi.setText(ReportJuzi);
        }
    };
    Runnable   adjust_text_zhiding=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            String TempStr[] = ReportStr.split("\\.");
            text_views.get(Index).setText(TempStr[Index]);
        }
    };
}