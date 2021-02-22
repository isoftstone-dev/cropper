package com.crop.cropperlib.utils;

import ohos.agp.render.Paint;
import ohos.agp.utils.Color;

/**
 * 用于绘制CropOverlayView
 */
public class PaintUtil {
    private static final int BORDER_COLOR = 0xAAFFFFFF;
    private static final int GUIDELINE_COLOR = 0xAAFFFFFF;
    private static final int SURROUNDING_AREA_COLOR = 0xB0000000;
    private static final float FLOAT_BORDER = 3;
    private static final float FLOAT_GUIDELINE = 1;

    /**
     * 创建绘制裁剪窗口边框的绘制对象
     */
    public static Paint getBorderPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE_STYLE);
        paint.setStrokeWidth(FLOAT_BORDER);
        paint.setColor(new Color(BORDER_COLOR));
        return paint;
    }

    /**
     * 创建绘制裁剪窗口准则的绘制对象
     */
    public static Paint getGuidelinePaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE_STYLE);
        paint.setStrokeWidth(FLOAT_GUIDELINE);
        paint.setColor(new Color(GUIDELINE_COLOR));
        return paint;
    }

    /**
     * 创建用于在裁剪窗口外绘制半透明覆盖的绘制对象.
     *
     */
    public static Paint getSurroundingAreaPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_STYLE);
        paint.setColor(new Color(SURROUNDING_AREA_COLOR));
        return paint;
    }
}