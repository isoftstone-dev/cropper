package com.crop.cropperlib.border;

import com.crop.cropperlib.utils.AspectRatioUtil;
import ohos.agp.utils.RectFloat;

/**
 * 表示裁剪窗口中的边的枚举
 */
public enum Frame {

    LEFT,
    TOP,
    RIGHT,
    BOTTOM;

    // 一条边到另一条边的最小距离
    // 这是一个任意值，可以简单地防止裁剪窗口变得太小
    public static final int MIN_CROP_LENGTH_PX = 40;

    // 此边的坐标值.
    // 这将是左右边缘的x坐标，以及上边缘和下边缘的y坐标
    private float mCoordinate;

    /**
     * 设置框架的坐标。该坐标将表示左、右边缘的x坐标，以及上、下边缘的y坐标
     *
     * @param coordinate 边缘的位置
     */
    public void setCoordinate(float coordinate) {
        mCoordinate = coordinate;
    }

    /**
     * 将给定数量的像素添加到此帧的当前坐标位置
     *
     * @param distance 要添加的像素数
     */
    public void offset(float distance) {
        mCoordinate += distance;
    }

    /**
     * 获取边的坐标
     *
     * @return 边缘坐标（左右边缘的x坐标和上下边缘的y坐标）
     */
    public float getCoordinate() {
        return mCoordinate;
    }

    /**
     * 将边设置为给定的x-y坐标，但也调整为捕捉到图像边界和父视图边界约束
     *
     * @param x               x坐标
     * @param y               y坐标
     * @param imageRect       图像的边框
     * @param imageSnapRadius 边缘应捕捉到图像的半径
     */
    public void adjustCoordinate(float x, float y, RectFloat imageRect, float imageSnapRadius, float aspectRatio) {
        switch (this) {
            case LEFT:
                mCoordinate = adjustLeft(x, imageRect, imageSnapRadius, aspectRatio);
                break;
            case TOP:
                mCoordinate = adjustTop(y, imageRect, imageSnapRadius, aspectRatio);
                break;
            case RIGHT:
                mCoordinate = adjustRight(x, imageRect, imageSnapRadius, aspectRatio);
                break;
            case BOTTOM:
                mCoordinate = adjustBottom(y, imageRect, imageSnapRadius, aspectRatio);
                break;
        }
    }

    /**
     * 调整此帧位置，使生成的窗口具有给定的纵横比。
     *
     * @param aspectRatio 要达到的纵横比
     */
    public void adjustCoordinate(float aspectRatio) {
        float left = Frame.LEFT.getCoordinate();
        float top = Frame.TOP.getCoordinate();
        float right = Frame.RIGHT.getCoordinate();
        float bottom = Frame.BOTTOM.getCoordinate();

        switch (this) {
            case LEFT:
                mCoordinate = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);
                break;
            case TOP:
                mCoordinate = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);
                break;
            case RIGHT:
                mCoordinate = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);
                break;
            case BOTTOM:
                mCoordinate = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);
                break;
        }
    }

    /**
     * 将此帧捕捉到给定的图像边界
     *
     * @param imageRect 要捕捉到的图像的边框
     *
     * @return 此坐标的更改量（以像素为单位）（即新坐标减去旧坐标值）
     */
    public float snapToRect(RectFloat imageRect) {
        float oldCoordinate = mCoordinate;
        switch (this) {
            case LEFT:
                mCoordinate = imageRect.left;
                break;
            case TOP:
                mCoordinate = imageRect.top;
                break;
            case RIGHT:
                mCoordinate = imageRect.right;
                break;
            case BOTTOM:
                mCoordinate = imageRect.bottom;
                break;
        }
        return mCoordinate - oldCoordinate;
    }

    /**
     * 返回snapToRect的潜在捕捉偏移，而不更改坐标。
     *
     * @param imageRect 要捕捉到的图像的边框
     *
     *@return 此坐标更改的量（以像素为单位）（即新坐标减去旧坐标值）
     */
    public float snapOffset(RectFloat imageRect) {
        float oldCoordinate = mCoordinate;
        float newCoordinate;

        switch (this) {
            case LEFT:
                newCoordinate = imageRect.left;
                break;
            case TOP:
                newCoordinate = imageRect.top;
                break;
            case RIGHT:
                newCoordinate = imageRect.right;
                break;
            default: // BOTTOM
                newCoordinate = imageRect.bottom;
                break;
        }
        return newCoordinate - oldCoordinate;
    }

    /**
     * 获取裁剪窗口的当前宽度
     */
    public static float getWidth() {
        return Frame.RIGHT.getCoordinate() - Frame.LEFT.getCoordinate();
    }

    /**
     * 获取裁剪窗口的当前高度
     */
    public static float getHeight() {
        return Frame.BOTTOM.getCoordinate() - Frame.TOP.getCoordinate();
    }


    /**
     * 返回是否可以根据边缘是否超出边界来重新缩放图像。检查所有边缘是否有跳出边界的可能。
     *
     * @param frame       即将扩展的边缘
     * @param imageRect   图片的矩形
     * @param aspectRatio 图片所需的侧面
     *
     * @return 新图像是否超出范围.
     */
    public boolean isNewRectangleOutOfBounds(Frame frame, RectFloat imageRect, float aspectRatio) {
        float offset = frame.snapOffset(imageRect);

        switch (this) {
            case LEFT:
                if (frame.equals(Frame.TOP)) {
                    float top = imageRect.top;
                    float bottom = Frame.BOTTOM.getCoordinate() - offset;
                    float right = Frame.RIGHT.getCoordinate();
                    float left = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                } else if (frame.equals(Frame.BOTTOM)) {
                    float bottom = imageRect.bottom;
                    float top = Frame.TOP.getCoordinate() - offset;
                    float right = Frame.RIGHT.getCoordinate();
                    float left = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                }
                break;
            case TOP:
                if (frame.equals(Frame.LEFT)) {
                    float left = imageRect.left;
                    float right = Frame.RIGHT.getCoordinate() - offset;
                    float bottom = Frame.BOTTOM.getCoordinate();
                    float top = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                } else if (frame.equals(Frame.RIGHT)) {
                    float right = imageRect.right;
                    float left = Frame.LEFT.getCoordinate() - offset;
                    float bottom = Frame.BOTTOM.getCoordinate();
                    float top = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                }
                break;
            case RIGHT:
                if (frame.equals(Frame.TOP)) {
                    float top = imageRect.top;
                    float bottom = Frame.BOTTOM.getCoordinate() - offset;
                    float left = Frame.LEFT.getCoordinate();
                    float right = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                } else if (frame.equals(Frame.BOTTOM)) {
                    float bottom = imageRect.bottom;
                    float top = Frame.TOP.getCoordinate() - offset;
                    float left = Frame.LEFT.getCoordinate();
                    float right = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                }
                break;
            case BOTTOM:
                if (frame.equals(Frame.LEFT)) {
                    float left = imageRect.left;
                    float right = Frame.RIGHT.getCoordinate() - offset;
                    float top = Frame.TOP.getCoordinate();
                    float bottom = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                } else if (frame.equals(Frame.RIGHT)) {
                    float right = imageRect.right;
                    float left = Frame.LEFT.getCoordinate() - offset;
                    float top = Frame.TOP.getCoordinate();
                    float bottom = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio);

                    return isOutOfBounds(top, left, bottom, right, imageRect);
                }
                break;
        }
        return false;
    }

    /**
     * 返回新矩形是否超出边界
     *
     * @param imageRect 要与之比较的图像
     *
     * @return 是否会越界
     */
    private boolean isOutOfBounds(float top, float left, float bottom, float right, RectFloat imageRect) {
        return (!(top < imageRect.top) && !(left < imageRect.left) && !(bottom > imageRect.bottom) && !(right > imageRect.right));
    }

    /**
     * 确定此框架是否在给定边框的内边距之外。边距通过SNAPRADIUS数量进入实际帧；因此，确定点是否在内部“边距”帧之外。
     */
    public boolean isOutsideMargin(RectFloat rect, float margin) {
        boolean result;

        switch (this) {
            case LEFT:
                result = mCoordinate - rect.left < margin;
                break;
            case TOP:
                result = mCoordinate - rect.top < margin;
                break;
            case RIGHT:
                result = rect.right - mCoordinate < margin;
                break;
            default: // BOTTOM
                result = rect.bottom - mCoordinate < margin;
                break;
        }
        return result;
    }

    /**
     * 在给定控制柄位置、图像边界框和捕捉半径的情况下，获取裁剪窗口左边缘的结果x位置。
     *
     * @param x               左边缘拖动到的x位置
     * @param imageRect       正在裁剪的图像的边界框
     * @param imageSnapRadius 到图像边缘的捕捉距离
     *
     * @return 左边缘的实际x位置
     */
    private static float adjustLeft(float x, RectFloat imageRect, float imageSnapRadius, float aspectRatio) {
        float resultX;

        if (x - imageRect.left < imageSnapRadius) {
            resultX = imageRect.left;
        } else {

            // 选择要使用的三个可能值中的最小值
            float resultXHoriz = Float.POSITIVE_INFINITY;
            float resultXVert = Float.POSITIVE_INFINITY;

            // 检查车窗是否水平过小
            if (x >= Frame.RIGHT.getCoordinate() - MIN_CROP_LENGTH_PX) {
                resultXHoriz = Frame.RIGHT.getCoordinate() - MIN_CROP_LENGTH_PX;
            }

            // 检查窗口是否垂直过小
            if (((Frame.RIGHT.getCoordinate() - x) / aspectRatio) <= MIN_CROP_LENGTH_PX) {
                resultXVert = Frame.RIGHT.getCoordinate() - (MIN_CROP_LENGTH_PX * aspectRatio);
            }
            resultX = Math.min(x, Math.min(resultXHoriz, resultXVert));
        }
        return resultX;
    }

    /**
     * 在给定控制柄位置、图像边界框和捕捉半径的情况下，获取裁剪窗口右边缘的结果x位置。
     *
     * @param x               右边缘拖动到的x位置
     * @param imageRect       正在裁剪的图像的边界框
     * @param imageSnapRadius 到图像边缘的捕捉距离
     *
     * @return 右边缘的实际x位置
     */
    private static float adjustRight(float x, RectFloat imageRect, float imageSnapRadius, float aspectRatio) {
        float resultX;

        // 如果靠近边缘
        if (imageRect.right - x < imageSnapRadius) {
            resultX = imageRect.right;
        } else {

            // 选择要使用的三个可能值中的最大值
            float resultXHoriz = Float.NEGATIVE_INFINITY;
            float resultXVert = Float.NEGATIVE_INFINITY;

            // 检查车窗是否水平过小
            if (x <= Frame.LEFT.getCoordinate() + MIN_CROP_LENGTH_PX) {
                resultXHoriz = Frame.LEFT.getCoordinate() + MIN_CROP_LENGTH_PX;
            }
            // 检查窗口是否垂直过小
            if (((x - Frame.LEFT.getCoordinate()) / aspectRatio) <= MIN_CROP_LENGTH_PX) {
                resultXVert = Frame.LEFT.getCoordinate() + (MIN_CROP_LENGTH_PX * aspectRatio);
            }
            resultX = Math.max(x, Math.max(resultXHoriz, resultXVert));
        }
        return resultX;
    }

    /**
     * 在给定控制柄位置、图像边界框和捕捉半径的情况下，获取裁剪窗口上边缘的结果y位置。
     *
     * @param y               将上边缘拖动到的Y位置
     * @param imageRect       正在裁剪的图像的边界框
     * @param imageSnapRadius 到图像边缘的捕捉距离
     *
     * @return 上边缘的实际y位置
     */
    private static float adjustTop(float y,RectFloat imageRect, float imageSnapRadius, float aspectRatio) {
        float resultY;

        if (y - imageRect.top < imageSnapRadius) {
            resultY = imageRect.top;
        } else {

            // 选择要使用的三个可能值中的最小值
            float resultYVert = Float.POSITIVE_INFINITY;
            float resultYHoriz = Float.POSITIVE_INFINITY;

            // 检查窗口是否垂直过小
            if (y >= Frame.BOTTOM.getCoordinate() - MIN_CROP_LENGTH_PX) {
                resultYHoriz = Frame.BOTTOM.getCoordinate() - MIN_CROP_LENGTH_PX;
            }
            // 检查窗口是否水平过小
            if (((Frame.BOTTOM.getCoordinate() - y) * aspectRatio) <= MIN_CROP_LENGTH_PX) {
                resultYVert = Frame.BOTTOM.getCoordinate() - (MIN_CROP_LENGTH_PX / aspectRatio);
            }
            resultY = Math.min(y, Math.min(resultYHoriz, resultYVert));
        }
        return resultY;
    }

    /**
     * 在给定控制柄位置、图像边界框和捕捉半径的情况下，获取裁剪窗口底部边缘的结果y位置。
     *
     * @param y               底边拖动到的Y位置
     * @param imageRect       正在裁剪的图像的边界框
     * @param imageSnapRadius 到图像边缘的捕捉距离
     *
     * @return 底边的实际y位置
     */
    private static float adjustBottom(float y, RectFloat imageRect, float imageSnapRadius, float aspectRatio) {
        float resultY;

        if (imageRect.bottom - y < imageSnapRadius) {
            resultY = imageRect.bottom;
        } else {

            // 选择要使用的三个可能值中的最小值
            float resultYVert = Float.NEGATIVE_INFINITY;
            float resultYHoriz = Float.NEGATIVE_INFINITY;

            // 检查窗口是否垂直过小
            if (y <= Frame.TOP.getCoordinate() + MIN_CROP_LENGTH_PX) {
                resultYVert = Frame.TOP.getCoordinate() + MIN_CROP_LENGTH_PX;
            }
            // 检查窗口是否水平过小
            if (((y - Frame.TOP.getCoordinate()) * aspectRatio) <= MIN_CROP_LENGTH_PX) {
                resultYHoriz = Frame.TOP.getCoordinate() + (MIN_CROP_LENGTH_PX / aspectRatio);
            }
            resultY = Math.max(y, Math.max(resultYHoriz, resultYVert));
        }
        return resultY;
    }
}