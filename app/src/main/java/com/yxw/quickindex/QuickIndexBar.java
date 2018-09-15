package com.yxw.quickindex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class QuickIndexBar extends View {
    private static final String[] LETTERS = new String[]{
            "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X",
            "Y", "Z"};
    private Paint mPaint;
    private int cellWidth;
    private float cellHeight;
    private OnLetterUpdateListener listener;
    private int touchIndex = -1;
    private WindowManager mWM;
    //悬浮窗
    WindowManager.LayoutParams params;
    TextView view;

    /**
     * 暴露一个字母的监听
     */
    public interface OnLetterUpdateListener {
        void onLetterUpdate(String letter);
    }

    public OnLetterUpdateListener getListener() {
        return listener;
    }

    /**
     * 设置字母更新监听
     *
     * @param listener
     */
    public void setListener(OnLetterUpdateListener listener) {
        this.listener = listener;
    }

    public QuickIndexBar(Context context) {
        this(context, null);
    }

    public QuickIndexBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//传入的参数表示抗锯齿，空参的时候，画出来的文本内容边缘有点锯齿
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(40);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);//设置为默认的粗体
        //悬浮窗
        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= 26) {
            //8.0用的
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        int winWidth = mWM.getDefaultDisplay().getWidth();
        int winHeight = mWM.getDefaultDisplay().getHeight();
        params.x = winWidth / 2 - 150;
        params.y = winHeight / 2 - 125;
        params.gravity = Gravity.LEFT + Gravity.TOP;
        view = new TextView(context);
        view.setWidth(300);
        view.setHeight(250);
        view.setGravity(Gravity.CENTER);
        view.setTextColor(Color.BLACK);//字体黑色
        view.setBackgroundColor(Color.GRAY);//背景灰色
        view.setTextSize(40);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        for (int i = 0; i < LETTERS.length; i++) {
            String text = LETTERS[i];
            // 计算坐标，用画笔测量文本的宽度
            int x = (int) (cellWidth / 2.0f - mPaint.measureText(text) / 2.0f);
            // 获取文本的高度
            Rect bounds = new Rect();// 矩形
            mPaint.getTextBounds(text, 0, text.length(), bounds);
            int textHeight = bounds.height();
            int y = (int) (cellHeight / 2.0f + textHeight / 2.0f + i * cellHeight);
            // 绘制文本A-Z，坐标(x,y)指的是text所处位置的左下角
            canvas.drawText(text, x, y, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取当前触摸到的字母索引
                index = (int) (event.getY() / cellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    if (listener != null) {
                        listener.onLetterUpdate(LETTERS[index]);
                    }
                    touchIndex = index;
                    //点击显示悬浮窗
                    showWindow(LETTERS[index]);
                    //点击将背景显示为灰色
                    this.setBackgroundColor(Color.GRAY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                index = (int) (event.getY() / cellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    // 判断是否跟上一次触摸到的一样，一样就不需要操作了
                    if (index != touchIndex) {
                        if (listener != null) {
                            listener.onLetterUpdate(LETTERS[index]);
                        }
                        touchIndex = index;
                        updateWindow(LETTERS[index]);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //重置一下
                touchIndex = -1;
                //抬起移除悬浮窗
                deleteWindow();
                //将背景重新显示为透明
                this.setBackgroundColor(Color.TRANSPARENT);
                break;
            default:
                break;
        }
        //改动了颜色，需要重绘，加上该语句
        invalidate();
        return true;
    }

    private void showWindow(String letter) {
        view.setText(letter);
        mWM.addView(view, params);
    }

    private void updateWindow(String letter) {
        view.setText(letter);
        mWM.updateViewLayout(view, params);
    }

    private void deleteWindow() {
        if (mWM != null && view != null) {
            mWM.removeView(view);
        }
    }


    /**
     * 获取控件宽高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();
        cellHeight = mHeight * 1.0f / LETTERS.length;
    }
}
