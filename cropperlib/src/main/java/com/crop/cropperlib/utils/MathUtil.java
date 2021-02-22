package com.crop.cropperlib.utils;

class MathUtil {

    /**
     * 计算两点（x1，y1）和（x2，y2）之间的距离。
     */
    static float calculateDistance(float x1, float y1, float x2, float y2) {
        float side1 = x2 - x1;
        float side2 = y2 - y1;
        return (float) Math.sqrt(side1 * side1 + side2 * side2);
    }
}