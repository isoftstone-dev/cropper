package com.crop.cropperlib.utils;

import com.crop.cropperlib.handle.Handle;
import ohos.agp.utils.Point;

public class HandleUtil {
    /**
     * 确定在给定触摸坐标、边界框和触摸半径的情况下按下哪个控制柄（如果有）
     *
     * @param x            触点X坐标
     * @param y            触点X坐标
     * @param left         左边界的x坐标
     * @param top          上界的y坐标
     * @param right        右边界的x坐标
     * @param bottom       下边界的y坐标
     * @param targetRadius 以像素为单位的目标半径
     *
     * @return Handle
     */
    public static Handle getPressedHandle(float x,
                                          float y,
                                          float left,
                                          float top,
                                          float right,
                                          float bottom,
                                          float targetRadius) {

        // 找到离接触点最近的角把手。
        // 如果接触点位于最近手柄的目标区域，则这是按下的手柄。
        // 否则，请检查是否有任何边位于接触点的目标区域。
        // 否则，请检查触摸点是否在裁剪窗口范围内；如果在范围内，请选择中心控制柄。
        Handle closestHandle = null;
        float closestDistance = Float.POSITIVE_INFINITY;

        final float distanceToTopLeft = MathUtil.calculateDistance(x, y, left, top);
        if (distanceToTopLeft < closestDistance) {
            closestDistance = distanceToTopLeft;
            closestHandle = Handle.TOP_LEFT;
        }

        final float distanceToTopRight = MathUtil.calculateDistance(x, y, right, top);
        if (distanceToTopRight < closestDistance) {
            closestDistance = distanceToTopRight;
            closestHandle = Handle.TOP_RIGHT;
        }

        final float distanceToBottomLeft = MathUtil.calculateDistance(x, y, left, bottom);
        if (distanceToBottomLeft < closestDistance) {
            closestDistance = distanceToBottomLeft;
            closestHandle = Handle.BOTTOM_LEFT;
        }

        final float distanceToBottomRight = MathUtil.calculateDistance(x, y, right, bottom);
        if (distanceToBottomRight < closestDistance) {
            closestDistance = distanceToBottomRight;
            closestHandle = Handle.BOTTOM_RIGHT;
        }

        if (closestDistance <= targetRadius) {
            return closestHandle;
        }

        // 如果我们到达这一点，没有一个角处理器在触摸目标区域，我们需要检查边缘。
        if (HandleUtil.isInHorizontalTargetZone(x, y, left, right, top, targetRadius)) {
            return Handle.TOP;
        } else if (HandleUtil.isInHorizontalTargetZone(x, y, left, right, bottom, targetRadius)) {
            return Handle.BOTTOM;
        } else if (HandleUtil.isInVerticalTargetZone(x, y, left, top, bottom, targetRadius)) {
            return Handle.LEFT;
        } else if (HandleUtil.isInVerticalTargetZone(x, y, right, top, bottom, targetRadius)) {
            return Handle.RIGHT;
        }

        // 如果我们到达这一点，没有一个角落或边缘是在触摸目标区。
        // 检查触摸点是否在裁剪窗口的范围内。如果是，请选择中心控制柄。
        if (isWithinBounds(x, y, left, top, right, bottom)) {
            return Handle.CENTER;
        }
        return Handle.CENTER;
    }

    /**
     * 计算接触点相对于指定控制柄的精确位置的偏移
     *
     * 偏移量将在“touchOffsetOutput”参数中返回；x偏移量将是第一个值，y偏移量将是第二个值。
     */
    public static void getOffset(Handle handle,
                                 float x,
                                 float y,
                                 float left,
                                 float top,
                                 float right,
                                 float bottom,
                                 Point touchOffsetOutput) {

        float touchOffsetX = 0;
        float touchOffsetY = 0;

        // 计算与相应控制柄的偏移
        switch (handle) {

            case TOP_LEFT:
                touchOffsetX = left - x;
                touchOffsetY = top - y;
                break;
            case TOP_RIGHT:
                touchOffsetX = right - x;
                touchOffsetY = top - y;
                break;
            case BOTTOM_LEFT:
                touchOffsetX = left - x;
                touchOffsetY = bottom - y;
                break;
            case BOTTOM_RIGHT:
                touchOffsetX = right - x;
                touchOffsetY = bottom - y;
                break;
            case LEFT:
                touchOffsetX = left - x;
                touchOffsetY = 0;
                break;
            case TOP:
                touchOffsetX = 0;
                touchOffsetY = top - y;
                break;
            case RIGHT:
                touchOffsetX = right - x;
                touchOffsetY = 0;
                break;
            case BOTTOM:
                touchOffsetX = 0;
                touchOffsetY = bottom - y;
                break;
            case CENTER:
                final float centerX = (right + left) / 2;
                final float centerY = (top + bottom) / 2;
                touchOffsetX = centerX - x;
                touchOffsetY = centerY - y;
                break;
        }

        touchOffsetOutput.modify(touchOffsetX, touchOffsetY);
    }

    /**
     * 确定指定的坐标是否位于水平条控制柄的目标触摸区域中
     *
     * @param x            触点x坐标
     * @param y            触点Y坐标
     * @param handleXStart 水平滑动条的左x坐标
     * @param handleXEnd   水平滑动条的右x坐标
     * @param handleY      水平滑动条的Y坐标
     * @param targetRadius 以像素为单位的目标半径
     *
     * @return 如果接触点位于目标接触区，则为true；否则为false
     */
    private static boolean isInHorizontalTargetZone(float x,
                                                    float y,
                                                    float handleXStart,
                                                    float handleXEnd,
                                                    float handleY,
                                                    float targetRadius) {

        return (x > handleXStart && x < handleXEnd && Math.abs(y - handleY) <= targetRadius);
    }

    /**
     * 确定指定的坐标是否位于垂直条控制柄的目标触摸区域中
     *
     * @param x            触点x坐标
     * @param y            触点Y坐标
     * @param handleX      垂直滑动条的Y坐标
     * @param handleYStart 垂直滑动条的顶部Y坐标
     * @param handleYEnd   垂直滑动条的底部Y坐标
     * @param targetRadius 以像素为单位的目标半径
     *
     * @return 如果接触点位于目标接触区，则为true；否则为false
     */
    private static boolean isInVerticalTargetZone(float x,
                                                  float y,
                                                  float handleX,
                                                  float handleYStart,
                                                  float handleYEnd,
                                                  float targetRadius) {

        return (Math.abs(x - handleX) <= targetRadius && y > handleYStart && y < handleYEnd);
    }

    private static boolean isWithinBounds(float x, float y, float left, float top, float right, float bottom) {
        return x >= left && x <= right && y >= top && y <= bottom;
    }
}