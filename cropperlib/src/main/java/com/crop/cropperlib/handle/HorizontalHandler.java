package com.crop.cropperlib.handle;

import com.crop.cropperlib.border.Frame;
import com.crop.cropperlib.utils.AspectRatioUtil;
import ohos.agp.utils.RectFloat;

/**
 * 水平处理器
 */
class HorizontalHandler extends Handler {

    private Frame mFrame;

    HorizontalHandler(Frame frame) {
        super(frame, null);
        mFrame = frame;
    }

    @Override
    void refreshCropWindow(float x, float y, float targetAspectRatio, RectFloat imageRect, float snapRadius) {

        // 相应地调整此框架
        mFrame.adjustCoordinate(x, y, imageRect, snapRadius, targetAspectRatio);

        float left = Frame.LEFT.getCoordinate();
        float right = Frame.RIGHT.getCoordinate();

        // 移动此帧后，裁剪窗口将失去比例
        float targetWidth = AspectRatioUtil.calculateWidth(Frame.getHeight(), targetAspectRatio);

        // 调整裁剪窗口，使其通过对称地移入或移出相邻边来保持给定的纵横比
        float difference = targetWidth - Frame.getWidth();
        float halfDifference = difference / 2;
        left -= halfDifference;
        right += halfDifference;

        Frame.LEFT.setCoordinate(left);
        Frame.RIGHT.setCoordinate(right);

        // 检查两边是否越界，然后修理
        if (Frame.LEFT.isOutsideMargin(imageRect, snapRadius)
                && mFrame.isNewRectangleOutOfBounds(Frame.LEFT, imageRect, targetAspectRatio)) {

            float offset = Frame.LEFT.snapToRect(imageRect);
            Frame.RIGHT.offset(-offset);
            mFrame.adjustCoordinate(targetAspectRatio);
        }

        if (Frame.RIGHT.isOutsideMargin(imageRect, snapRadius)
                && mFrame.isNewRectangleOutOfBounds(Frame.RIGHT, imageRect, targetAspectRatio)) {

            float offset = Frame.RIGHT.snapToRect(imageRect);
            Frame.LEFT.offset(-offset);
            mFrame.adjustCoordinate(targetAspectRatio);
        }
    }
}