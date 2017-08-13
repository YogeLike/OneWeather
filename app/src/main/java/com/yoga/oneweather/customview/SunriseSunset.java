package com.yoga.oneweather.customview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.yoga.oneweather.R;
import com.yoga.oneweather.util.MiscUtil;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by wyg on 2017/8/3.
 */

public class SunriseSunset extends View {
    private Context mContext;
    private static final float DEFAULT_SIZE = 300f;
    private int mDefaultSize;
    private int sweepAngle = 140;
    private final int STARTANGLE = 200 - (sweepAngle - 140)/2;
    private double  halfRemianAngle = (180 - sweepAngle)/2/180*PI;
    private int width,height;
    private Point mCenterPoint;

    private Paint mArcPaint;
    private RectF mRectF;
    private float mRadius;
    private  float lineYLocate;
    private ObjectAnimator mAnimator;
    private Paint mSunPaint;
    private Paint mTimePaint;

    private int lineColor;
    private int sunColor;
    private int timeTextColor;
    private float timeTextSize;
    private String mSunriseTime = "6:00";
    private String mSunsetTime = "19:00";

    private Bitmap mSunriseBitmap;
    private Bitmap mSunsetBitmap;
    private float percent = 0;
    private int mTotalTime;
    private boolean isSetTime = false;
    private int mNowTime;

    private static final String TAG = "SunriseSunset";
    public SunriseSunset(Context context) {
        this(context,null);
    }

    public SunriseSunset(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SunriseSunset(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mDefaultSize = MiscUtil.dipToPx(mContext,DEFAULT_SIZE);//默认300
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if(attrs != null){
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs,R.styleable.SunriseSunset);
            lineColor = typedArray.getColor(R.styleable.SunriseSunset_lineColor, Color.WHITE);
            sunColor = typedArray.getColor(R.styleable.SunriseSunset_sunColor,Color.YELLOW);
            timeTextColor = typedArray.getColor(R.styleable.SunriseSunset_timeTextColor,Color.GRAY);
            timeTextSize = typedArray.getDimension(R.styleable.SunriseSunset_timeTextSize,40);
            typedArray.recycle();
        }
        mCenterPoint = new Point();
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(lineColor);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(3);
        mArcPaint.setPathEffect(new DashPathEffect(new float[]{20,18},20));
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mRectF = new RectF();


        mSunPaint = new Paint();
        mSunPaint.setAntiAlias(true);
        mSunPaint.setColor(sunColor);
        mSunPaint.setStyle(Paint.Style.STROKE);
        mSunPaint.setStrokeWidth(3);
        mSunPaint.setPathEffect(new DashPathEffect(new float[]{20,18},20));
        mSunriseBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_sunrise);
        mSunsetBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_sunset);
        mSunPaint.setColorFilter( new PorterDuffColorFilter(sunColor, PorterDuff.Mode.SRC_ATOP)) ;//设置后bitmap才会填充颜色

        mTimePaint = new Paint();
        mTimePaint.setColor(timeTextColor);
        mTimePaint.setTextSize(timeTextSize);
        mTimePaint.setTextAlign(Paint.Align.CENTER);



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MiscUtil.measure(widthMeasureSpec,mDefaultSize),MiscUtil.measure(heightMeasureSpec, (int) (mDefaultSize * 0.55)));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w - getPaddingLeft() - getPaddingRight();
        height = h - getPaddingBottom() - getPaddingTop();
        mRadius = ((width / 2) < height ? (width/2) : height) - MiscUtil.dipToPx(mContext,15);//留出一定空间
        mCenterPoint.x = width/2;
        mCenterPoint.y = height;
        mRectF.left = mCenterPoint.x - mRadius;
        mRectF.top = mCenterPoint.y - mRadius;
        mRectF.right = mCenterPoint.x + mRadius;
        mRectF.bottom = mCenterPoint.y + mRadius;
        lineYLocate = (float) (mCenterPoint.y - mRadius * sin(PI/180*(180 - sweepAngle)/2));


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        canvas.save();
        drawArc(canvas);
        drawText(canvas);
        canvas.restore();
        Log.d(TAG, "onDraw: ");
    }

    private void drawText(Canvas canvas) {
        canvas.drawText(mSunriseTime,mCenterPoint.x - mRadius + timeTextSize,lineYLocate + timeTextSize + 15,mTimePaint);//y轴可能需要微调一下
        canvas.drawText(mSunsetTime,mCenterPoint.x + mRadius - timeTextSize,lineYLocate + timeTextSize + 15,mTimePaint);
        mTimePaint.setTextSize(40);
        canvas.drawText("日出日落",mCenterPoint.x ,lineYLocate - timeTextSize ,mTimePaint);
    }

    private void drawArc(Canvas canvas) {
        Log.d(TAG, "drawArc: "+percent);
        float nowAngle;
        if(percent == 0){
            nowAngle = 0 ;
            canvas.drawBitmap(mSunsetBitmap, (float) (mCenterPoint.x - mRadius* cos(halfRemianAngle)) ,lineYLocate - mSunsetBitmap.getHeight() ,mSunPaint);//太阳
        }else if(percent == 1){
            nowAngle = sweepAngle;
            canvas.drawBitmap(mSunsetBitmap,(float) (mCenterPoint.x + mRadius* cos(halfRemianAngle)) - mSunsetBitmap.getWidth() ,lineYLocate - mSunsetBitmap.getHeight() ,mSunPaint);
        }else {
            nowAngle = sweepAngle * percent;

            canvas.drawBitmap(mSunriseBitmap,(float)(mCenterPoint.x - mRadius* cos((nowAngle+18)*(PI/180) + halfRemianAngle )) - mSunriseBitmap.getWidth()/2,(float)(mCenterPoint.y - mRadius* sin((nowAngle+18)*(PI/180)+(halfRemianAngle))) - mSunriseBitmap.getHeight()/2 ,mSunPaint);//18这个数字是我测试出来计算过程中损失的各种度数
        }
        canvas.drawArc(mRectF,STARTANGLE + nowAngle,sweepAngle - nowAngle,false,mArcPaint);//弧
        canvas.drawLine(0,lineYLocate,width,lineYLocate,mArcPaint);//线
        canvas.drawArc(mRectF,STARTANGLE,nowAngle,false,mSunPaint);




    }

    public void startAnimation(){

        if(isSetTime){
            float nowPercent = (float) (mNowTime)/(float) mTotalTime;//这个float一定要转啊，不然会一致都是0
            Log.d(TAG, "startAnimator: ");
            if(nowPercent<0.02){//如果太阳只升起来一点就不画动画了,没出也不画
                setPercent(0);
            }else if(nowPercent>0.98){
                setPercent(1);
            }else {
                mAnimator = ObjectAnimator.ofFloat(this,"percent",0,nowPercent);
                mAnimator.setDuration((long) (2000+3000*nowPercent));
                mAnimator.setInterpolator(new LinearInterpolator());
                mAnimator.start();
            }

        }

    }
    public void stopAnimator(){
        clearAnimation();
    }

    public void setTime(String mSunriseTime ,String mSunsetTime ,String nowTime){
        this.mSunriseTime = mSunriseTime;
        this.mSunsetTime  = mSunsetTime ;
        mTotalTime = transToMinuteTime(mSunsetTime)-transToMinuteTime(mSunriseTime);
        this.mNowTime = transToMinuteTime(nowTime)-transToMinuteTime(mSunriseTime);
        isSetTime = true;

    }

    public void setPercent(float percent) {
        this.percent = percent;
        if(percent>0 && percent < 1){//这里控制日出前日落后不重绘//可以删除，因为前面过滤了
            invalidate();
        }

    }

    private int transToMinuteTime(String time){//"00:00"
        String[] s = time.split(":");
        return Integer.parseInt(s[0])*60 + Integer.parseInt(s[1]);
    }

}
