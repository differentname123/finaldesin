package com.fb.standard.ui.mood;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.fb.standard.R;
import com.fb.standard.base.BaseFragment;
import com.fb.standard.base.TongXing;
import com.fb.standard.databinding.FragmentMoodBinding;
import com.github.mikephil.charting.charts.Chart;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import net.sf.json.JSONObject;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MoodFragment extends BaseFragment<FragmentMoodBinding, MoodViewModel> {
    LineChart chart;
    String MoodStr = "";
    private Handler handler=null;//用于线程改变控件
    private ExecutorService mThreadPool;
    private TongXing tongxing = new TongXing();//声明通信的对象
    private List<String> dataset ;
    @Override
    protected void initData() {
        handler=new Handler();
        NiceSpinner niceSpinner = binding.niceSpinner;
        binding.textfenxi.setMovementMethod(ScrollingMovementMethod.getInstance());
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
                                binding.chart.clearValues();
                                huayitian();
                                Log.d("xinqing","功能待添加");
                            }
                        });
                        break;
                    case 1:
                        mThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                binding.chart.clearValues();
                                huayizhou();
                                Log.d("xinqing","功能待添加");
                            }
                        });
                        break;

                    case 2:
                        mThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                binding.chart.clearValues();
                                huayiyue();
                                Log.d("xinqing","功能待添加");
                            }
                        });
                        break;
                }

            }
        });


        //setTitle("RealtimeLineChartActivity");
        chart = binding.chart;
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


        binding.addone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        MoodStr = tongxing.get_mood_str();
                        handler.post(MoodStrUI);

                    }
                });

            }
        });
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                huayitian();

            }
        });


    }

    @Override
    protected void initView(View root) {
        setMenuId(R.menu.home_menu);

    }

    @Override
    protected int getResId() {
        return R.layout.fragment_mood;
    }

    @Override
    protected String setTitle() {
        return getResources().getString(R.string.title_mood);
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
                    getActivity().runOnUiThread(runnable);

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
            Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_SHORT).show();
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
        if(linecount == 1)
        {
            Looper.prepare();
            Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_SHORT).show();
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

    protected void saveToGallery() {
        saveToGallery(chart, "RealtimeLineChartActivity");
    }

    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    protected void saveToGallery(Chart chart, String name) {
        if (chart.saveToGallery(name + "_" + System.currentTimeMillis(), 70))
            makeToast("Saving SUCCESSFUL!");
        else
            makeToast("Saving FAILED!");
    }

    Runnable   MoodStrUI=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            binding.textfenxi.setText(MoodStr);
        }
    };
}