package com.cocoas.example;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.TextView;

import com.cocoas.example.scaleedittext.R;

/**
 * 自动调整字体文本输入框
 * @author 蒋庆意
 * @date 2015-11-4
 * @time 上午11:02:32
 */
public class AutoAdjustSizeEditText extends EditText
{
    /**
     * 默认文字字体大小最小值(单位：像素)
     */
    private static final float DEFAULT_TEXT_SIZE_MIN = 20;
    
    /**
     * 默认文字字体大小最大值(单位：像素)(貌似用不上)
     */
    @SuppressWarnings("unused")
    private static final float DEFAULT_TEXT_SIZE_MAX = 60;
    
    /**
     * 画笔（用来测量已输入文字的长度）
     */
    private Paint paint;
    
    /**
     * 文字字体大小最小值
     */
    private float minTextSize = 0;
    
    /**
     * 文字字体大小最大值
     */
    private float maxTextSize = 0;
    
    /**
     * 判断输入文本字体是否变小过
     */
    private boolean hasScaleSmall=false;
    
    public AutoAdjustSizeEditText(Context context)
    {
        super(context);
        paint = new Paint();
    }
    
    public AutoAdjustSizeEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        paint = new Paint();
        //读取自定义属性， 获取设置的字体大小范围
        if (null != attrs)
        {
            TypedArray array = context.obtainStyledAttributes(attrs,
                    R.styleable.AutoAdjustTextSize);
            if (null != array)
            {
                minTextSize = array.getDimension(R.styleable.AutoAdjustTextSize_minTextSize,
                        DEFAULT_TEXT_SIZE_MIN);
                //如果未设置字体最大值,则使用当前字体大小作为最大值
                maxTextSize = array.getDimension(R.styleable.AutoAdjustTextSize_maxTextSize,
                        this.getTextSize());
                //回收 TypedArray
                array.recycle();
            }
        }
        //未设置字体最小值,则使用默认最小值
        if (0 == minTextSize)
        {
            minTextSize = DEFAULT_TEXT_SIZE_MIN;
        }
        //未设置字体最大值,则使用当前字体大小作为最大值
        if (0 == maxTextSize)
        {
            //            maxTextSize = DEFAULT_TEXT_SIZE_MAX;
            maxTextSize = this.getTextSize();
        }
        //如果设置的值不正确（例如minTextSize>maxTextSize）,则互换
        if (minTextSize > maxTextSize)
        {
            float minSize = maxTextSize;
            maxTextSize = minTextSize;
            minTextSize = minSize;
        }
        Log.d("AutoScaleSizeEditText",
                "minTextSize=" + String.valueOf(minTextSize));
        Log.d("AutoScaleSizeEditText",
                "maxTextSize=" + String.valueOf(maxTextSize));
    }
    
    /**
     * 调整文本的显示
     * @param text 文本
     * @param textWidth 文本长度
     */
    private void adjustTextSize(TextView textView)
    {
        if (null == textView)
        {
            //参数错误，不与处理
            return;
        }
        //已输入文本
        String text = textView.getText().toString();
        //已输入文本长度
        int textWidth = textView.getWidth();
        if (null == text || text.isEmpty() || textWidth <= 0)
        {
            return;
        }
        //获取输入框总的可输入的文本长度
        float maxInputWidth = textView.getWidth() - textView.getPaddingLeft()
                - textView.getPaddingRight();
        //获取当前文本字体大小
        float currentTextSize = textView.getTextSize();
        Log.d("AutoScaleSizeEditText",
                "currentTextSize=" + String.valueOf(currentTextSize));
        //设置画笔的字体大小
        paint.setTextSize(currentTextSize);
        /*
         * 循环减小字体大小
         * 当  1、文本字体小于最大值
         *     2、可输入文本长度小于已输入文本长度
         * 时   
         */
        while ((currentTextSize > minTextSize)
                && (maxInputWidth < paint.measureText(text)))
        {
            hasScaleSmall=true;
            Log.d("AutoScaleSizeEditText",
                    "TextSizeChange=" + String.valueOf(currentTextSize));
            --currentTextSize;
            if (currentTextSize < minTextSize)
            {
                currentTextSize = minTextSize;
                break;
            }
            //设置画笔字体大小
            paint.setTextSize(currentTextSize);
        }
        /*
         * 循环增大字体大小
         * 当  1、文本字体小于默认值
         *     2、可输入文本长度大于已输入文本长度
         * 时   
         */
        while (hasScaleSmall&&(currentTextSize < maxTextSize)
                && (maxInputWidth > paint.measureText(text)))
        {
            Log.d("AutoScaleSizeEditText",
                    "TextSizeChangeSmall=" + String.valueOf(currentTextSize));
            ++currentTextSize;
            if (currentTextSize > maxTextSize)
            {
                currentTextSize = maxTextSize;
                break;
            }
            //设置画笔字体大小
            paint.setTextSize(currentTextSize);
        }
        //设置文本字体(单位为像素px)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
        Log.d("AutoScaleSizeEditText",
                "currentTextSize2=" + String.valueOf(currentTextSize));
    }
    
    @Override
    protected void onTextChanged(CharSequence text, int start,
            int lengthBefore, int lengthAfter)
    {
        // 根据需要调整字体大小
        adjustTextSize(this);
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        // 根据需要调整字体大小
        if (w != oldw)
        {
            adjustTextSize(this);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
