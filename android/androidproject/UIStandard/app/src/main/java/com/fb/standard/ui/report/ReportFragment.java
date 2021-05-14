package com.fb.standard.ui.report;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fb.standard.R;
import com.fb.standard.adapter.ViewPagerAdapter;
import com.fb.standard.base.BaseFragment;
import com.fb.standard.databinding.FragmentReportBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.viewpager.widget.ViewPager;

public class ReportFragment extends BaseFragment<FragmentReportBinding, ReportViewModel> {

    private ViewPagerAdapter adapter;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View root) {
        adapter = new ViewPagerAdapter();
        binding.viewPager.setAdapter(adapter);

        List<View> views = new ArrayList<>();
        View page1 = LayoutInflater.from(getContext()).inflate(R.layout.page_text,null);
        View page2 = LayoutInflater.from(getContext()).inflate(R.layout.page_text,null);
        View page3 = LayoutInflater.from(getContext()).inflate(R.layout.page_text,null);
        View page4 = LayoutInflater.from(getContext()).inflate(R.layout.page_pie,null);

        TextView text1 = page1.findViewById(R.id.text);
        TextView text2 = page2.findViewById(R.id.text);
        TextView text3 = page3.findViewById(R.id.text);
        PieChart pieChart = page4.findViewById(R.id.pieChart);

        text1.setText("Text1");
        text2.setText("Text2");
        text3.setText("Text3");

        pieChart.setEntryLabelTextSize(30);
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pieEntries.add(new PieEntry(20,"参数"+i));
        }
        PieDataSet dataSet = new PieDataSet(pieEntries,"label");
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
                switch (position){
                    case 0:
                        startAnimation(text1);
                        binding.dot1.setImageResource(R.drawable.shape_circle_black);
                        break;
                    case 1:
                        startAnimation(text2);
                        binding.dot2.setImageResource(R.drawable.shape_circle_black);
                        break;
                    case 2:
                        startAnimation(text3);
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
        objectAnimator.setDuration(1000);
        objectAnimator.start();
    }
}