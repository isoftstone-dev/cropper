package com.crop.cropperlib.handle;

import com.crop.cropperlib.border.Frame;
import com.crop.cropperlib.utils.AspectRatioUtil;
import ohos.agp.utils.RectFloat;

/**
 * 垂直处理器
 */
class VerticalHandler extends Handler {

    private Frame mFrame;

    VerticalHandler(Frame frame) {
        super(null, frame);
        mFrame = frame;
    }

    @Override
    void refreshCropWindow(float x, float y, float targetAspectRatio, RectFloat imageRect, float snapRadius) {

        // 相应地调整此框架.
        mFrame.adjustCoordinate(x, y, imageRect, snapRadius, targetAspectRatio);

        float top = Frame.TOP.getCoordinate();
        float bottom = Frame.BOTTOM.getCoordinate();

        // 移动此帧后，裁剪窗口将失去比例
        float targetHeight = AspectRatioUtil.calculateHeight(Frame.getWidth(), targetAspectRatio);

        // 调整裁剪窗口，使其通过对称地移入或移出相邻边来保持给定的纵横比
        float difference = targetHeight - Frame.getHeight();
        float halfDifference = difference / 2;
        top -= halfDifference;
        bottom += halfDifference;

        Frame.TOP.setCoordinate(top);
        Frame.BOTTOM.setCoordinate(bottom);

        // 检查我们是否在顶部或底部越界，并修复
        if (Frame.TOP.isOutsideMargin(imageRect, snapRadius)
                && mFrame.isNewRectangleOutOfBounds(Frame.TOP, imageRect, targetAspectRatio)) {

            float offset = Frame.TOP.snapToRect(imageRect);
            Frame.BOTTOM.offset(-offset);
            mFrame.adjustCoordinate(targetAspectRatio);
        }

        if (Frame.BOTTOM.isOutsideMargin(imageRect, snapRadius)
                && mFrame.isNewRectangleOutOfBounds(Frame.BOTTOM, imageRect, targetAspectRatio)) {

            float offset = Frame.BOTTOM.snapToRect(imageRect);
            Frame.TOP.offset(-offset);
            mFrame.adjustCoordinate(targetAspectRatio);
        }
    }
}