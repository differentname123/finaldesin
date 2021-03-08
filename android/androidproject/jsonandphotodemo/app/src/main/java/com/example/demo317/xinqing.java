package com.example.demo317;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.angmarch.views.NiceSpinner;

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

public class xinqing extends DemoBase implements
        OnChartValueSelectedListener {
    private LineChart chart;
    private ExecutorService mThreadPool;
    TongXing tongxing = new TongXing();//声明通信的对象
    List<String> dataset ;
    Button addone,addbyself,xinqingother;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_xinqing);

        NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        dataset = new LinkedList<>(Arrays.asList("日视图", "周视图", "月视图"));
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setTextSize(10);
        niceSpinner.setBackgroundColor(Color.rgb(255, 242, 226));
        niceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch(i)
                {
                    case 0:
                        mThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                chart.clearValues();
                                huayitian();
                                Log.d("xinqing","功能待添加");
                            }
                        });
                        break;
                    case 1:
                        mThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                chart.clearValues();
                                huayizhou();
                                Log.d("xinqing","功能待添加");
                            }
                        });
                        break;

                    case 2:
                        mThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                chart.clearValues();
                                huayiyue();
                                Log.d("xinqing","功能待添加");
                            }
                        });
                        break;
                }

            }
        });


        //setTitle("RealtimeLineChartActivity");
        chart = findViewById(R.id.chart1);
        //chart.setOnChartValueSelectedListener(this);

        // enable description text
        chart.getDescription().setEnabled(true);
        mThreadPool = Executors.newCachedThreadPool();//初始化线程池
        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.rgb(255, 242, 226));//设置背景颜色
        //描述文字的操作
        Description de = new Description();
        de.setText("情绪状态图");
        chart.setDescription(de);

        LineData data = new LineData();
            data.setValueTextColor(Color.BLACK);

        // add empty data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
       // l.setTypeface(tfLight);
        l.setTextColor(Color.BLACK);

        //上面的横坐标
        XAxis xl = chart.getXAxis();
       // xl.setTypeface(tfLight);
        xl.setTextColor(Color.BLACK);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawGridLines(false);
        xl.setTextSize(15f);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);


        YAxis leftAxis = chart.getAxisLeft();
       // leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(15f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);//不绘制Y轴网格线

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        addone = (Button) findViewById(R.id.addone);
        addone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEntry();

            }
        });

        addbyself = (Button) findViewById(R.id.addbyself);
        addbyself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedMultiple();
            }
        });


        xinqingother = (Button) findViewById(R.id.xinqingother);
        xinqingother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        huayizhou();
                        Log.d("xinqing","功能待添加");
                    }
                });

            }
        });

    }
    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "心情指标");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());//设置连线颜色
        set.setCircleColor(ColorTemplate.getHoloBlue());//设置坐标点的颜色
        set.setLineWidth(4f);//设置连线宽度

        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);//设置线段是否是平滑
        set.setCircleRadius(6f);//设置坐标点大小
        set.setFillAlpha(65);
        set.setFillColor(Color.BLACK);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.YELLOW);
        set.setValueTextSize(40f);
        set.setDrawValues(false);
        return set;
    }

    private Thread thread;

    private void feedMultiple() {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry();
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {

                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }
    private float covert(int now,float befor,float xishu)//对收到的心情值进行映射  使它们既能体现信息也好看
    {
        int zhi =50;
        float buchang = 0;
        float  xinqingzhi;
        float huigui;
        huigui = 50 - befor;
        if(befor>50)
        {
            huigui = befor - 50;
            if(now<0)
                buchang =1;
        }
        if(now>0)
            buchang =1;
        huigui = 1- huigui/50;
        xinqingzhi = now /2000;
        xinqingzhi = befor + (huigui+buchang)*xinqingzhi;
        return xinqingzhi ;
    }
    private void huayizhou()//绘制一周的情绪图
    {
        int timecount = 7*24*60*60;
        float xishu = 2,beforzhi = 50;
        String result = "";
        result = tongxing.get_xinqing(timecount);
        Log.d("xinqing",result);
        int linecount;
        JSONObject tempjson = new JSONObject();
        JSONObject js =JSONObject.fromObject(result); //string转json格式
        linecount = js.getInt("linecount");
        if(linecount == 0)
        {
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        Log.d("xinqing",linecount+"");
        for(int i = 0;i<linecount;i++)
        {
            String temp = js.getString(""+i);
            JSONObject jstemp =JSONObject.fromObject(temp); //string转json格式
            String zhi = jstemp.getString("zhi");
            float xinqingzhi ;
            xinqingzhi = covert(Integer.parseInt(zhi),beforzhi,xishu);
            beforzhi = xinqingzhi;
            addzhi(xinqingzhi);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private void huayitian()//绘制一天的情绪图
    {
        int timecount = 1*24*60*60;
        float xishu = 2,beforzhi = 50;
        String result = "";
        result = tongxing.get_xinqing(timecount);
        Log.d("xinqing",result);
        int linecount;
        JSONObject tempjson = new JSONObject();
        JSONObject js =JSONObject.fromObject(result); //string转json格式
        linecount = js.getInt("linecount");
        if(linecount == 0)
        {
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        Log.d("xinqing",linecount+"");
        for(int i = 0;i<linecount;i++)
        {
            String temp = js.getString(""+i);
            JSONObject jstemp =JSONObject.fromObject(temp); //string转json格式
            String zhi = jstemp.getString("zhi");
            float xinqingzhi ;
            xinqingzhi = covert(Integer.parseInt(zhi),beforzhi,xishu);
            beforzhi = xinqingzhi;
            addzhi(xinqingzhi);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private void huayiyue()//绘制一个月的情绪图
    {
        int timecount = 30*24*60*60;
        float xishu = 2,beforzhi = 50;
        String result = "";
        result = tongxing.get_xinqing(timecount);
        Log.d("xinqing",result);
        int linecount;
        JSONObject tempjson = new JSONObject();
        JSONObject js =JSONObject.fromObject(result); //string转json格式
        linecount = js.getInt("linecount");
        if(linecount == 0)
        {
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        Log.d("xinqing",linecount+"");
        for(int i = 0;i<linecount;i++)
        {
            String temp = js.getString(""+i);
            JSONObject jstemp =JSONObject.fromObject(temp); //string转json格式
            String zhi = jstemp.getString("zhi");
            float xinqingzhi ;
            xinqingzhi = covert(Integer.parseInt(zhi),beforzhi,xishu);
            beforzhi = xinqingzhi;
            addzhi(xinqingzhi);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private void addzhi(float zhi)
    {
        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), zhi), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(7);//设置横坐标最大显示的值
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }

    }
    private void addEntry() {

        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(15);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "RealtimeLineChartActivity");
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

}
