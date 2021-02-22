package com.crop.cropperlib.utils;

/**
 * 用于处理涉及固定纵横比的计算的实用程序类
 */
public class AspectRatioUtil {

    /**
     * 计算给定矩形的纵横比
     */
    public static float calculateAspectRatio(float left, float top, float right, float bottom) {
        float width = right - left;
        float height = bottom - top;
        return width / height;
    }

    /**
     * 计算给定矩形其他边和纵横比的左边缘的x坐标
     */
    public static float calculateLeft(float top, float right, float bottom, float targetAspectRatio) {
        return right - (targetAspectRatio * (bottom - top));
    }

    /**
     * 计算给定矩形其他边和纵横比的顶边的y坐标
     */
    public static float calculateTop(float left, float right, float bottom, float targetAspectRatio) {
        return bottom - ((right - left) / targetAspectRatio);
    }

    /**
     * 在给定矩形其他边和纵横比的情况下，计算右边缘的x坐标。
     */
    public static float calculateRight(float left, float top, float bottom, float targetAspectRatio) {
        return (targetAspectRatio * (bottom - top)) + left;
    }

    /**
     * 计算给定矩形其他边和纵横比的底边的y坐标
     */
    public static float calculateBottom(float left, float top, float right, float targetAspectRatio) {
        return ((right - left) / targetAspectRatio) + top;
    }

    /**
     * 计算给定上、下边缘和纵横比的矩形的宽度
     */
    public static float calculateWidth(float height, float targetAspectRatio) {
        return targetAspectRatio * height;
    }

    /**
     * 计算给定左右边和纵横比的矩形的高度
     */
    public static float calculateHeight(float width, float targetAspectRatio) {
        return width / targetAspectRatio;
    }
}