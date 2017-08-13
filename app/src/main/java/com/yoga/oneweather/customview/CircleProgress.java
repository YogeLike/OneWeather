package com.yoga.oneweather.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yoga.oneweather.BuildConfig;
import com.yoga.oneweather.R;
import com.yoga.oneweather.util.Constant;
import com.yoga.oneweather.util.MiscUtil;

/**
 * Created by wyg on 2017/7/30.
 */

public class CircleProgress extends View{
    private static final String TAG = CircleProgress.class.getSimpleName();
    private Context mContext;

    //默认大小
    private int mDefaultSize;
    //是否开启抗锯齿
    private boolean antiAlias;
    //绘制提示
    private TextPaint mHintPaint;
    private CharSequence mHint;
    private int mHintColor;
    private float mHintSize;
    private float mHintOffset;

    //绘制单位
    private TextPaint mUnitPaint;
    private CharSequence mUnit;
    private int mUnitColor;
    private float mUnitSize;
    private float mUnitOffset;

    //绘制数值
    private TextPaint mValuePaint;
    private float mValue;
    private float mValueOffset;
    private int mPrecision;
    private String mPrecisionFormat;
    private int mValueColor;
    private float mValueSize;

    //绘制最大值和最小值
    private TextPaint mMaxAndMinPaint;
    private int mMaxValue;
    private float mMaxAndMinXOffset;
    private float mMaxAndMinYOffset;
    private float mMaxXLocate;
    private float mMaxYLocate;
    private float mMinXLocate;
    private float mMinYLocate;
    private int mMaxAndMinColor;
    private float mMaxAndMinSize;




    //绘制圆弧
    private Paint mArcPaint;
    private float mArcWidth;
    private float mStartAngle,mSweepAngle;
    private RectF mRectF;
    //渐变的角度是360度，如果只显示270度，会缺失一段颜色
    private SweepGradient mSweepGradient;
    private int[] mGradientColors = {Color.GREEN,Color.YELLOW,Color.RED};
    //当前进度
    private float mPercent;
    //动画时间
    private long mAnimTime;
    //属性动画
    private ValueAnimator mAnimator;

    //绘制背景圆弧
    private Paint mBgArcPaint;
    private int mBgArcColor;
    private float mBgArcWidth;

    //圆心坐标，半径
    private Point mCenterPoint;
    private float mRadius;
    private float mTextOffsetPercentInRadius;

    public CircleProgress(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mContext = context;
        mDefaultSize = MiscUtil.dipToPx(mContext, Constant.DEFAULT_SIZE);
        mAnimator = new ValueAnimator();
        mRectF = new RectF();
        mCenterPoint = new Point();
        initAttrs(attrs);
        initPaint();
        setValue(mValue);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        antiAlias = typedArray.getBoolean(R.styleable.CircleProgressBar_antiAlias,Constant.ANTI_ALIAS);
        mHint = typedArray.getString(R.styleable.CircleProgressBar_hint);
        mHintColor = typedArray.getColor(R.styleable.CircleProgressBar_hintColor,Color.BLACK);
        mHintSize = typedArray.getDimension(R.styleable.CircleProgressBar_hintSize,Constant.DEFAULT_HINT_SIZE);


        mValue = typedArray.getFloat(R.styleable.CircleProgressBar_value, Constant.DEFAULT_VALUE);
        //内容数值精度格式
        mPrecision = typedArray.getInt(R.styleable.CircleProgressBar_precision, 0);
        mPrecisionFormat = MiscUtil.getPrecisionFormat(mPrecision);
        mValueColor = typedArray.getColor(R.styleable.CircleProgressBar_valueColor, Color.BLACK);
        mValueSize = typedArray.getDimension(R.styleable.CircleProgressBar_valueSize, Constant.DEFAULT_VALUE_SIZE);

        mMaxValue = typedArray.getInt(R.styleable.CircleProgressBar_maxValue, Constant.DEFAULT_MAX_VALUE);
        mMaxAndMinColor = typedArray.getColor(R.styleable.CircleProgressBar_maxAndMinValueColor,Color.GRAY);
        mMaxAndMinSize = typedArray.getDimension(R.styleable.CircleProgressBar_maxAndMinValueSize,Constant.DEFAULT_TEXT_SIZE);
        mMaxAndMinXOffset = typedArray.getFloat(R.styleable.CircleProgressBar_maxAndMinXOffsetPercent,0.70f);
        mMaxAndMinYOffset = typedArray.getFloat(R.styleable.CircleProgressBar_maxAndMinYOffsetPercent,0.90f);


        mUnit = typedArray.getString(R.styleable.CircleProgressBar_unit);
        mUnitColor = typedArray.getColor(R.styleable.CircleProgressBar_unitColor, Color.BLACK);
        mUnitSize = typedArray.getDimension(R.styleable.CircleProgressBar_unitSize, Constant.DEFAULT_UNIT_SIZE);

        mArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_arcWidth, Constant.DEFAULT_ARC_WIDTH);
        mStartAngle = typedArray.getFloat(R.styleable.CircleProgressBar_startAngle, Constant.DEFAULT_START_ANGLE);
        mSweepAngle = typedArray.getFloat(R.styleable.CircleProgressBar_sweepAngle, Constant.DEFAULT_SWEEP_ANGLE)+2;

        mBgArcColor = typedArray.getColor(R.styleable.CircleProgressBar_bgArcColor, Color.WHITE);
        mBgArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_bgArcWidth, Constant.DEFAULT_ARC_WIDTH);
        mTextOffsetPercentInRadius = typedArray.getFloat(R.styleable.CircleProgressBar_textOffsetPercentInRadius, 0.43f);

        //mPercent = typedArray.getFloat(R.styleable.CircleProgressBar_percent, 0);
        mAnimTime = typedArray.getInt(R.styleable.CircleProgressBar_animTime, Constant.DEFAULT_ANIM_TIME);


        int gradientArcColors = typedArray.getResourceId(R.styleable.CircleProgressBar_arcColors,0);
        if(gradientArcColors != 0 ){
            int[] gradientColors = getResources().getIntArray(gradientArcColors);
            if(gradientColors.length == 0){//如果渐变色数组为0，则以单色读取
                int color = ContextCompat.getColor(mContext,gradientArcColors);
                mGradientColors = new int[2];
                mGradientColors[0] = color;
                mGradientColors[1] = color;
            }else if(gradientColors.length == 1){//如果只有一种颜色，默认设置为两种相同颜色
                mGradientColors = new int[2];
                mGradientColors[0] = gradientColors[0];
                mGradientColors[1] = gradientColors[0];
            }else {
                mGradientColors = gradientColors;
            }
        }

        typedArray.recycle();
    }


    private void initPaint() {
        mHintPaint = new TextPaint();
        //设置抗锯齿
        mHintPaint.setAntiAlias(antiAlias);
        //绘制文字大小
        mHintPaint.setTextSize(mHintSize);
        //设置画笔颜色
        mHintPaint.setColor(mHintColor);
        //从中间向两边绘制，不用再次计算文字
        mHintPaint.setTextAlign(Paint.Align.CENTER);

        mValuePaint = new TextPaint();
        mValuePaint.setAntiAlias(antiAlias);
        mValuePaint.setTextSize(mValueSize);
        mValuePaint.setColor(mValueColor);
        // 设置Typeface对象，即字体风格，包括粗体，斜体以及衬线体，非衬线体等
        mValuePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        mUnitPaint = new TextPaint();
        mUnitPaint.setAntiAlias(antiAlias);
        mUnitPaint.setTextSize(mUnitSize);
        mUnitPaint.setColor(mUnitColor);
        mUnitPaint.setTextAlign(Paint.Align.CENTER);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(antiAlias);
        //设置画笔样式，FILL,FILL_OR_STROKE,STROKE
        mArcPaint.setStyle(Paint.Style.STROKE);
        //设置画笔粗细
        mArcPaint.setStrokeWidth(mArcWidth);
        //当画笔样式为STROKE 或 FILL_OR_STROKE ，设置笔刷图形样式
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mMaxAndMinPaint = new TextPaint();
        mMaxAndMinPaint.setColor(mMaxAndMinColor);
        mMaxAndMinPaint.setAntiAlias(antiAlias);
        mMaxAndMinPaint.setTextSize(mMaxAndMinSize);
        mMaxAndMinPaint.setTextAlign(Paint.Align.CENTER);


        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(antiAlias);
        mBgArcPaint.setColor(mBgArcColor);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setStrokeWidth(mBgArcWidth);
        mBgArcPaint.setStrokeCap(Paint.Cap.ROUND);

    }


    public void startAnimation() {
        Log.d(TAG, "startAnimator: "+"invoke...");
        mAnimator = ValueAnimator.ofFloat(0,mValue/(float)mMaxValue);
        mAnimator.setDuration((long) (2000+20*mValue));
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onAnimationUpdate: percent = " + mPercent
                            + ";currentAngle = " + (mSweepAngle * mPercent)
                            + ";value = " + mValue);
                }
                invalidate();//重绘view
            }
        });
        mAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MiscUtil.measure(widthMeasureSpec,mDefaultSize),MiscUtil.measure(heightMeasureSpec,mDefaultSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //求圆弧和圆弧背景的最大宽度
        float maxArcWidth = Math.max(mArcWidth,mBgArcWidth);
        //求最小值作为实际值
        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight()- 2*(int)maxArcWidth,w - getPaddingTop() - getPaddingBottom()- 2*(int)maxArcWidth);
        mRadius = minSize/2;
        //获取圆的参数
        mCenterPoint.x = w/2;
        mCenterPoint.y = h/2;
        //绘制圆弧边界,画圆弧的大小
        mRectF.left = mCenterPoint.x - mRadius - maxArcWidth/2 ;//?不除以2的话会有一半圆弧跑出去，recf指定的是外边界圆弧的中点吧
        mRectF.top = mCenterPoint.y - mRadius - maxArcWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + maxArcWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + maxArcWidth / 2;
        //计算文字的baseline
        //由于文字的baseline,descent,ascent等属性只与textSize和typeface 有关，所以此时可以直接计算
        //若value，hint,unit 由同一个画笔绘制或者需要动态设置文字的大小，则需要在每次更新后再次绘制
        mValueOffset = mCenterPoint.y + getBaselineOffsetFromY(mValuePaint)/2;
        mHintOffset = mCenterPoint.y - mRadius * mTextOffsetPercentInRadius;
        mUnitOffset = mCenterPoint.y + mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mUnitPaint);
        mMaxXLocate = mCenterPoint.x + mRadius * mMaxAndMinXOffset;
        mMaxYLocate = mCenterPoint.y + mRadius * mMaxAndMinYOffset + getBaselineOffsetFromY(mMaxAndMinPaint);
        mMinXLocate = mCenterPoint.x - mRadius * mMaxAndMinXOffset;
        mMinYLocate = mCenterPoint.y + mRadius * mMaxAndMinYOffset + getBaselineOffsetFromY(mMaxAndMinPaint);

        updateArcPaint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: "+"invoke");
        drawText(canvas);
        drawArc(canvas);
    }
    private void drawText(Canvas canvas) {
        //计算文字宽度，由于Paint已设置为设置为居中绘制，故此处不需要重新计算
        canvas.drawText(String.format(mPrecisionFormat,mValue),mCenterPoint.x,mValueOffset,mValuePaint);
        canvas.drawText(mMaxValue+"",mMaxXLocate,mMaxYLocate,mMaxAndMinPaint);
        canvas.drawText("0",mMinXLocate,mMinYLocate,mMaxAndMinPaint);
        if(mHint != null){
            canvas.drawText(mHint.toString(),mCenterPoint.x,mHintOffset,mHintPaint);
        }
        if(mUnit != null){
            canvas.drawText(mUnit.toString(),mCenterPoint.x,mUnitOffset,mUnitPaint);
        }

    }
    private void drawArc(Canvas canvas) {
        //绘制圆弧背景
        //从进度圆弧结束的地方重新开始绘制，优化性能
        canvas.save();
        float currentAngle = mSweepAngle * mPercent;
        canvas.rotate(mStartAngle,mCenterPoint.x,mCenterPoint.y);//旋转,以stratAngle 为起点
        canvas.drawArc(mRectF,currentAngle,mSweepAngle-currentAngle,false,mBgArcPaint);//这里最好画个图理解一下
        //第二个参数：startAngel 起始角度，第三个参数：圆弧度数，
        //3点钟方向为0度，顺时针递增，startAngle超过取360 或小于0 与360 取余
        //useCenter 如果为true 时 ，绘制圆弧将圆心包含，通常用来绘制扇形
        canvas.drawArc(mRectF,0,currentAngle,false,mArcPaint);
        Log.d(TAG, "drawArc: "+currentAngle);
        canvas.restore();
    }

    /**
     * 设置值
     * @param value
     */
    public void setValue(float value) {
        Log.d(TAG, "setValue: "+value);
        if(value > mMaxValue){
            value = mMaxValue;
        }
        mValue = value;

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //释放资源
    }

    /**
     * 更新圆弧画笔
     */
    private void updateArcPaint() {
        //设置渐变
        //int[] mGradientColors = {Color.GREEN,Color.YELLOW,Color.RED};
        mSweepGradient = new SweepGradient(mCenterPoint.x,mCenterPoint.y,mGradientColors,null);
        mArcPaint.setShader(mSweepGradient);
    }

    private float getBaselineOffsetFromY(Paint paint) {
        return MiscUtil.measureTextHeight(paint)/2;
    }



    public boolean isAntiAlias() {
        return antiAlias;
    }

    public CharSequence getHint() {
        return mHint;
    }

    public void setHint(CharSequence hint) {
        mHint = hint;
    }

    public CharSequence getUnit() {
        return mUnit;
    }

    public void setUnit(CharSequence unit) {
        mUnit = unit;
    }

    public float getValue() {
        return mValue;
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int mMaxValue) {
        this.mMaxValue = mMaxValue;
    }

    public int getPrecision() {
        return mPrecision;
    }

    public void setPrecision(int mPrecision) {
        this.mPrecision = mPrecision;
        mPrecisionFormat = MiscUtil.getPrecisionFormat(mPrecision);
    }

    public void setAnimTime(long mAnimTime) {
        this.mAnimTime = mAnimTime;
    }

    public long getAnimTime() {
        return mAnimTime;
    }

    public void setmGradientColors(int[] mGradientColors) {
        this.mGradientColors = mGradientColors;
        updateArcPaint();
    }

    public int[] getmGradientColors() {
        return mGradientColors;
    }



}
