package com.crop.cropperlib.handle;

import com.crop.cropperlib.border.Frame;
import com.crop.cropperlib.border.FrameGroup;
import com.crop.cropperlib.utils.AspectRatioUtil;
import ohos.agp.utils.RectFloat;

/**
 * 来处理裁剪窗口的操作的抽象类
 */
abstract class Handler {
    private static final float UNFIXED_ASPECT_RATIO_CONSTANT = 1;
    private Frame mHorizontalFrame;
    private Frame mVerticalFrame;

    // 将Pair对象另存为成员变量，以避免每次调用getActiveEdge（）时都必须实例化新对象。
    private FrameGroup mActiveFrames;

    /**
     * @param horizontalFrame 水平边；可以为null
     * @param verticalFrame   垂直边；可以为null
     */
    Handler(Frame horizontalFrame, Frame verticalFrame) {
        mHorizontalFrame = horizontalFrame;
        mVerticalFrame = verticalFrame;
        mActiveFrames = new FrameGroup(mHorizontalFrame, mVerticalFrame);
    }

    /**
     * 通过直接设置帧坐标来更新裁剪窗口
     *
     * @param x          x坐标
     * @param y          y坐标
     * @param imageRect  图像的边框
     * @param snapRadius 裁剪窗口应捕捉到图像的最大距离
     */
    void refreshCropWindow(float x, float y, RectFloat imageRect, float snapRadius) {
        FrameGroup activeFrames = getActiveFrames();
        Frame primaryFrame = activeFrames.primary;
        Frame secondaryFrame = activeFrames.seconder;

        if (primaryFrame != null) {
            primaryFrame.adjustCoordinate(x, y, imageRect, snapRadius, UNFIXED_ASPECT_RATIO_CONSTANT);
        }
        if (secondaryFrame != null) {
            secondaryFrame.adjustCoordinate(x, y, imageRect, snapRadius, UNFIXED_ASPECT_RATIO_CONSTANT);
        }
    }

    /**
     * 通过直接设置帧坐标来更新裁剪窗口；此方法保持给定的纵横比
     *
     * @param x                 x坐标
     * @param y                 y坐标
     * @param targetAspectRatio 要保持的横纵比
     * @param imageRect         图像的边框
     * @param snapRadius        裁剪窗口应捕捉到图像的最大距离
     */
    abstract void refreshCropWindow(float x, float y, float targetAspectRatio, RectFloat imageRect, float snapRadius);

    /**
     * 获取关联的边（即拖动此句柄时应移动的边）。在不保持纵横比的情况下使用。
     *
     * @return 一组活动边
     */
    private FrameGroup getActiveFrames() {
        return mActiveFrames;
    }

    /**
     * 获取关联的边作为有序对。一对中的主边是决定边。当我们需要保持纵横比时使用这个方法。
     *
     * @param x 触点x坐标
     * @param y 触点y坐标
     * @param targetAspectRatio 需要保持的横纵比
     *
     * @return 一组活动边
     */
    FrameGroup getActiveFrames(float x, float y, float targetAspectRatio) {

        // 如果将此控制柄拖动到给定的x-y坐标，则计算纵横比
        final float potentialAspectRatio = getAspectRatio(x, y);

        // 如果接触点比宽高比宽，那么x是确定的边。否则，y是决定性的一方
        if (potentialAspectRatio > targetAspectRatio) {
            mActiveFrames.primary = mVerticalFrame;
            mActiveFrames.seconder = mHorizontalFrame;
        } else {
            mActiveFrames.primary = mHorizontalFrame;
            mActiveFrames.seconder = mVerticalFrame;
        }
        return mActiveFrames;
    }

    /**
     * 获取裁剪窗口的纵横比
     *
     * @param x x坐标
     * @param y y坐标
     *
     * @return 纵横比
     */
    private float getAspectRatio(float x, float y) {

        // 用给定的触摸坐标替换活动边坐标
        float left = (mVerticalFrame == Frame.LEFT) ? x : Frame.LEFT.getCoordinate();
        float top = (mHorizontalFrame == Frame.TOP) ? y : Frame.TOP.getCoordinate();
        float right = (mVerticalFrame == Frame.RIGHT) ? x : Frame.RIGHT.getCoordinate();
        float bottom = (mHorizontalFrame == Frame.BOTTOM) ? y : Frame.BOTTOM.getCoordinate();

        return AspectRatioUtil.calculateAspectRatio(left, top, right, bottom);
    }
}