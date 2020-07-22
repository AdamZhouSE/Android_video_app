package com.example.videoapp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.videoapp.R;

/**
 * 绘制双击后出现的爱心
 *
 */

public class LoveView extends View {

    private Paint paint;

    // 点击坐标
    private float posX = 100f;
    private  float posY = 100f;

    // 偏离值(图片距离点击位置有偏离，原因不明)
    private float offSetX = 140.0f;
    private float offSetY = 200.0f;

    private Bitmap bitmap;

    public LoveView(Context context) {
        super(context);
        init(context, null);
    }

    public LoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        Matrix matrix = new Matrix();
        matrix.postScale(2.5f, 2.5f);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.love5);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        // 在点击位置绘制爱心图片
        canvas.drawBitmap(bitmap, posX - offSetX, posY - offSetY, paint);
    }

    public void setXY(float x, float y) {
        posX = x;
        posY = y;
    }
}
