package com.crop.cropperlib.handle;

import com.crop.cropperlib.border.Frame;
import ohos.agp.utils.RectFloat;

/**
 * 表示裁剪窗口上可按、可拖动的枚举
 */
public enum Handle {

    TOP_LEFT(new CornerHandler(Frame.TOP, Frame.LEFT)),
    TOP_RIGHT(new CornerHandler(Frame.TOP, Frame.RIGHT)),
    BOTTOM_LEFT(new CornerHandler(Frame.BOTTOM, Frame.LEFT)),
    BOTTOM_RIGHT(new CornerHandler(Frame.BOTTOM, Frame.RIGHT)),
    LEFT(new VerticalHandler(Frame.LEFT)),
    TOP(new HorizontalHandler(Frame.TOP)),
    RIGHT(new VerticalHandler(Frame.RIGHT)),
    BOTTOM(new HorizontalHandler(Frame.BOTTOM)),
    CENTER(new CenterHandler());

    private Handler mHelper;

    Handle(Handler helper) {
        mHelper = helper;
    }

    public void refreshCropWindow(float x, float y, RectFloat imageRect, float snapRadius) {
        mHelper.refreshCropWindow(x, y, imageRect, snapRadius);
    }

    public void refreshCropWindow(float x, float y, float targetAspectRatio, RectFloat imageRect, float snapRadius) {
        mHelper.refreshCropWindow(x, y, targetAspectRatio, imageRect, snapRadius);
    }
}