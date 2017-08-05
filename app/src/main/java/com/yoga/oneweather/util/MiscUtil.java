package com.yoga.oneweather.util;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by wyg on 2017/7/30.
 */

public class MiscUtil {
    /**
     * 测量 view
     * @param measureSpec
     * @param defaultSize View的默认大小
     * @return
     */
    public static int measure(int measureSpec ,int defaultSize){
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        if(specMode == View.MeasureSpec.EXACTLY){
            result = specSize;
        }else if(specMode == View.MeasureSpec.AT_MOST){
            result = Math.min(result,specSize);
        }
        return result;
    }

    /**
     * dp 转 px
     * @param context
     * @param dip
     * @return
     */
    public static int dipToPx(Context context, float dip){
        float density  = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }


    /**
     * 获取数值精度格式化字符串
     * @param precision
     * @return
     */
    public static String getPrecisionFormat(int precision){
        return "%."+precision+"f" ;
    }



    /**
     * 测量文字高度
     */
    public static float measureTextHeight(Paint paint){
        Paint .FontMetrics fontMetrics = paint.getFontMetrics();
        return (Math.abs(fontMetrics.ascent) + fontMetrics.descent);
    }


}
