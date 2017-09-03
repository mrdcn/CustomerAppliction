package com.another.customapplication.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.TextView;

import com.another.customapplication.R;

/**
 * Created by another on 17-3-24.
 */

public class ColorBorderTextView extends android.support.v7.widget.AppCompatTextView {

    private int borderColor = Color.BLACK;
    private int defualtTextSize = 15;
    private int defualtTextColor = Color.BLACK;
    private int defualtPaddingLeft = 5;
    private int defualtPaddingRight = 5;
    private int defualtPaddingTop = 3;
    private int defualtPaddingBotton = 3;
    private int defualtBorderWidth = 2;
    private int defualtBorderColor = Color.BLACK;
    private int defualtRaido = 10;

    public ColorBorderTextView(Context context) {
        super(context);
        init(context, null);
    }

    public ColorBorderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColorBorderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private int lineStock = 1;
    private int borderWidth;
    private int borderRadio = 50;

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorBorderTextView);
            borderWidth = a.getDimensionPixelSize(R.styleable.ColorBorderTextView_borderwidth, defualtBorderWidth);
            borderColor = a.getColor(R.styleable.ColorBorderTextView_bordercolor, defualtBorderColor);
//            TypedArray array = context.obtainStyledAttributes(attrs,com.android.internal.R.styleable.TextView);
        } else {
            borderColor = defualtBorderColor;
            borderWidth = defualtBorderWidth;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (borderWidth > 0) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            gradientDrawable.setCornerRadius(20);
            gradientDrawable.setSize(20,4);
            gradientDrawable.setStroke(5,borderColor);


//            gra
//            gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
//            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
//            Paint paint = gradientDrawable.set
//            paint.setStrokeWidth(5.000000f);
//            paint.setAntiAlias(true);
//            paint.setColor(borderColor);
//            paint.setDither(true);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeCap(Paint.Cap.SQUARE);
//            canvas.drawLine(borderRadio, 0, getWidth() - borderRadio, 0, paint);
//            canvas.drawLine(0, borderRadio, 0, getHeight() - borderRadio, paint);
//            canvas.drawLine(getWidth(), borderRadio, getWidth(), getHeight() - borderRadio, paint);
//            canvas.drawLine(getWidth() - borderRadio, getHeight(), borderRadio, getHeight(), paint);
//            canvas.drawArc(new RectF(0,0,borderRadio * 2,borderRadio *2),180,360,false,paint);
//            canvas.drawArc(new RectF(getWidth() -  borderRadio *2,0,getWidth(),borderRadio *2),270,90,true,paint);
//            canvas.drawArc(new RectF(getWidth() - borderRadio *2,getHeight() - borderRadio *2,getWidth(),getHeight()),0,90,false,paint);
//            canvas.drawArc(new RectF(0,getHeight() - borderRadio *2,borderRadio *2,getHeight()),90,90,false,paint);
            gradientDrawable.draw(canvas);
            setBackgroundDrawable(gradientDrawable);
            canvas.restore();
        }
    }

    public void setBorderColorInt(String color) {
        try {
            borderColor = Color.parseColor(color);

        } catch (Exception e) {
            borderColor = Color.BLACK;
        }
    }

    /**
     * @param lineWidth px
     */
    public void setLineWidth(int lineWidth) {
        this.borderWidth = lineWidth;
    }

}
