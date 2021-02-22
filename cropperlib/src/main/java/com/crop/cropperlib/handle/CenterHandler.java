package com.crop.cropperlib.handle;

import com.crop.cropperlib.border.Frame;
import ohos.agp.utils.RectFloat;

class CenterHandler extends Handler {

    CenterHandler() {
        super(null, null);
    }

    @Override
    void refreshCropWindow(float x, float y, RectFloat imageRect, float snapRadius) {
        float left = Frame.LEFT.getCoordinate();
        float top = Frame.TOP.getCoordinate();
        float right = Frame.RIGHT.getCoordinate();
        float bottom = Frame.BOTTOM.getCoordinate();

        float currentCenterX = (left + right) / 2;
        float currentCenterY = (top + bottom) / 2;
        float offsetX = x - currentCenterX;
        float offsetY = y - currentCenterY;

        // 调整裁剪窗口
        Frame.LEFT.offset(offsetX);
        Frame.TOP.offset(offsetY);
        Frame.RIGHT.offset(offsetX);
        Frame.BOTTOM.offset(offsetY);

        // 检查两边是否越界，然后修理
        if (Frame.LEFT.isOutsideMargin(imageRect, snapRadius)) {
            float offset = Frame.LEFT.snapToRect(imageRect);
            Frame.RIGHT.offset(offset);
        } else if (Frame.RIGHT.isOutsideMargin(imageRect, snapRadius)) {
            float offset = Frame.RIGHT.snapToRect(imageRect);
            Frame.LEFT.offset(offset);
        }

        // 检查我们是否在顶部或底部,越界需修复。
        if (Frame.TOP.isOutsideMargin(imageRect, snapRadius)) {
            float offset = Frame.TOP.snapToRect(imageRect);
            Frame.BOTTOM.offset(offset);
        } else if (Frame.BOTTOM.isOutsideMargin(imageRect, snapRadius)) {
            float offset = Frame.BOTTOM.snapToRect(imageRect);
            Frame.TOP.offset(offset);
        }
    }

    @Override
    void refreshCropWindow(float x, float y, float targetAspectRatio, RectFloat imageRect, float snapRadius) {
        refreshCropWindow(x, y, imageRect, snapRadius);
    }
}