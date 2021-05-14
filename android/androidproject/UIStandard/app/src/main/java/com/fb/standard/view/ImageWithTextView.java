package com.fb.standard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fb.standard.R;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ImageWithTextView extends ConstraintLayout {

    private ImageView mImgView;
    private TextView mTitle;
    private TextView mContent;
    private Context mContext;

    public ImageWithTextView(Context context) {
        this(context,null);
    }

    public ImageWithTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ImageWithTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("调用-----------","调用");
        View view = LayoutInflater.from(context).inflate(R.layout.view_image_with_text, this, true);
        this.mContext = context;
        this.mImgView = (ImageView)view.findViewById(R.id.image);
        this.mTitle = (TextView)view.findViewById(R.id.title);
        this.mContent = (TextView)view.findViewById(R.id.content);
    }

    /*设置图片接口*/
    public void setImageResource(int resId){
        mImgView.setImageResource(resId);
    }

    /*设置文字接口*/
    public void setTitle(String str){
        mTitle.setText(str);
    }

    public void setContent(String str){
        mContent.setText(str);
    }

}
