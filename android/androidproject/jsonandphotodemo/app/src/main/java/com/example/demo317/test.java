package com.example.demo317;
import java.util.Arrays;
import java.util.LinkedList;
import org.angmarch.views.NiceSpinner;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;


public class test extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        niceSpinner.setTextColor(Color.GREEN);

        LinkedList<String> data=new LinkedList<>(Arrays.asList("Zhang", "Phil", "@", "CSDN"));
        niceSpinner.attachDataSource(data);
    }
}