package com.crop.cropperlib.handle;

import com.crop.cropperlib.border.Frame;
import com.crop.cropperlib.border.FrameGroup;
import ohos.agp.utils.RectFloat;

class CornerHandler extends Handler {

    CornerHandler(Frame horizontalFrame, Frame verticalFrame) {
        super(horizontalFrame, verticalFrame);
    }

    @Override
    void refreshCropWindow(float x, float y, float targetAspectRatio, RectFloat imageRect, float snapRadius) {
        FrameGroup activeEdges = getActiveFrames(x, y, targetAspectRatio);
        Frame primaryFrame = activeEdges.primary;
        Frame secondaryFrame = activeEdges.seconder;

        primaryFrame.adjustCoordinate(x, y, imageRect, snapRadius, targetAspectRatio);
        secondaryFrame.adjustCoordinate(targetAspectRatio);

        if (secondaryFrame.isOutsideMargin(imageRect, snapRadius)) {
            secondaryFrame.snapToRect(imageRect);
            primaryFrame.adjustCoordinate(targetAspectRatio);
        }
    }
}