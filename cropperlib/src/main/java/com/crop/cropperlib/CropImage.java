package com.crop.cropperlib;

import com.crop.cropperlib.border.Frame;
import com.crop.cropperlib.handle.Handle;
import com.crop.cropperlib.utils.HandleUtil;
import com.crop.cropperlib.utils.PaintUtil;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Point;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Rect;
import ohos.media.image.common.Size;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;

public class CropImage extends Image implements Component.TouchEventListener, Component.DrawTask, Component.LayoutRefreshedListener {
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00202, "CROP");

    // 手柄周围可触摸区域的半径（以像素为单位）。
    // 我们是基于建议的48dp触摸目标大小的值
    private static final float M_HANDLE_RADIUS = 24f;

    // 当裁剪窗口边缘与边界框边缘的距离小于或等于此距离（以像素为单位）时，裁剪窗口的边缘将捕捉到指定边界框的相应边缘
    private static final float M_SNAP_RADIUS = 3f;

    // 用来在裁剪区域周围绘制白色矩形
    private Paint mBorderPaint;

    // 按下时用于在裁剪区域内绘制指导线
    private Paint mGuidelinePaint;

    // 用于使特定区域以外变暗
    private Paint mSurroundingAreaPaint;

    // 正在裁剪的位图周围的边界框
    private RectFloat mPixelMapRect = new RectFloat();

    // 保持精确触摸位置和激活的精确手柄位置之间的x和y偏移。
    // 可能有一个偏移量，因为我们在激活句柄时允许一些余地（由“mHandleRadius”指定）。
    // 但是，我们希望在拖动控制柄时保持这些偏移值，以便控制柄不会跳转。
    private Point mTouchOffset = new Point();

    // 当前按下的句柄；如果没有按下句柄，则为空。
    private Handle mPressedHandle;

    // 组件左上角X坐标
    private int topLeftX;

    // 组件左上角Y坐标
    private int topLeftY;

    public CropImage(Context context) {
        super(context);
        init();
    }

    public CropImage(Context context, AttrSet attrSet) {
        super(context, attrSet);
        init();
    }

    public CropImage(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        init();
    }

    private void init() {
        mBorderPaint = PaintUtil.getBorderPaint();
        mGuidelinePaint = PaintUtil.getGuidelinePaint();
        mSurroundingAreaPaint = PaintUtil.getSurroundingAreaPaint();
        setLayoutRefreshedListener(this);
        addDrawTask(this);
        setTouchEventListener(this);
    }

    private void initCropWindow() {
        ohos.agp.utils.Rect position = getComponentPosition();
        topLeftX = position.getCenterX() - getWidth() / 2;
        topLeftY = position.getCenterY() - getHeight() / 2;
        mPixelMapRect = getPixelMapRect();
        //  初始化裁剪窗口，使其具有相对于可绘制边界的10%填充
        float horizontalPadding = 0.1f * mPixelMapRect.getWidth();
        float verticalPadding = 0.1f * mPixelMapRect.getHeight();
        Frame.LEFT.setCoordinate(mPixelMapRect.left + horizontalPadding);
        Frame.TOP.setCoordinate(mPixelMapRect.top + verticalPadding);
        Frame.RIGHT.setCoordinate(mPixelMapRect.right - horizontalPadding);
        Frame.BOTTOM.setCoordinate(mPixelMapRect.bottom - verticalPadding);
    }

    @Override
    public void onRefreshed(Component component) {
        initCropWindow();
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        drawDarkenedSurroundingArea(canvas);
        drawGuidelines(canvas);
        drawBorder(canvas);
    }

    @Override
    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case TouchEvent.PRIMARY_POINT_DOWN:
                MmiPoint point1 = touchEvent.getPointerPosition(touchEvent.getIndex());
                onActionDown(point1.getX() - topLeftX, point1.getY() - topLeftY);
                invalidate();
                return true;
            case TouchEvent.PRIMARY_POINT_UP:
            case TouchEvent.CANCEL:
                onActionUp();
                invalidate();
                return true;
            case TouchEvent.POINT_MOVE:
                MmiPoint point2 = touchEvent.getPointerPosition(touchEvent.getIndex());
                onActionMove(point2.getX() - topLeftX, point2.getY() - topLeftY);
                invalidate();
                return true;
            default:
                return false;
        }
    }

    public PixelMap getCroppedImage() {
        float scaleX = getScaleX();
        float scaleY = getScaleY();
        float transX = getTranslationX();
        float transY = getTranslationY();

        float pixelMapLeft = (transX < 0) ? Math.abs(transX) : 0;
        float pixelMapTop = (transY < 0) ? Math.abs(transY) : 0;

        float cropX = (pixelMapLeft + Frame.LEFT.getCoordinate()) / scaleX;
        float cropY = (pixelMapTop + Frame.TOP.getCoordinate()) / scaleY;

        PixelMap originalPixelMap = this.getPixelMap();
        Size size = originalPixelMap.getImageInfo().size;

        float hideX = 0f;
        if (size.width > getWidth()) {
            hideX = (size.width - getWidth()) / 2f;
        }
        float hideY = 0f;
        if (size.height > getHeight()) {
            hideY = (size.height - getHeight()) / 2f;
        }

        float cropWidth = Math.min(Frame.getWidth() / scaleX, size.width - cropX);
        float cropHeight = Math.min(Frame.getHeight() / scaleY, size.height - cropY);

        Rect cropRect = new Rect((int)(cropX + hideX), (int)(cropY + hideY),(int)cropWidth, (int)cropHeight);
        PixelMap.InitializationOptions options = new PixelMap.InitializationOptions();
        options.size = new Size(cropRect.width, cropRect.height);
        return PixelMap.create(originalPixelMap, cropRect, options);
    }

    private RectFloat getPixelMapRect() {
        float scaleX = getScaleX();
        float scaleY = getScaleY();
        float transX = getTranslationX();
        float transY = getTranslationY();

        PixelMap originalPixelMap = this.getPixelMap();
        Size size = originalPixelMap.getImageInfo().size;

        int drawableDisplayWidth = Math.round(size.width * scaleX);
        int drawableDisplayHeight = Math.round(size.height * scaleY);

        float left = Math.max(transX, 0);
        float top = Math.max(transY, 0);
        float right = Math.min(left + drawableDisplayWidth, getWidth());
        float bottom = Math.min(top + drawableDisplayHeight, getHeight());

        return new RectFloat(left, top, right, bottom);
    }

    private void drawDarkenedSurroundingArea(Canvas canvas) {
        RectFloat bitmapRect = mPixelMapRect;
        float left = Frame.LEFT.getCoordinate();
        float top = Frame.TOP.getCoordinate();
        float right = Frame.RIGHT.getCoordinate();
        float bottom = Frame.BOTTOM.getCoordinate();

        RectFloat rect = new RectFloat(bitmapRect.left, bitmapRect.top, bitmapRect.right, top);
        canvas.drawRect(rect, mSurroundingAreaPaint);

        rect = new RectFloat(bitmapRect.left, bottom, bitmapRect.right, bitmapRect.bottom);
        canvas.drawRect(rect, mSurroundingAreaPaint);

        rect = new RectFloat(bitmapRect.left, top, left, bottom);
        canvas.drawRect(rect, mSurroundingAreaPaint);

        rect = new RectFloat(right, top, bitmapRect.right, bottom);
        canvas.drawRect(rect, mSurroundingAreaPaint);
    }

    private void drawGuidelines(Canvas canvas) {
        float left = Frame.LEFT.getCoordinate();
        float top = Frame.TOP.getCoordinate();
        float right = Frame.RIGHT.getCoordinate();
        float bottom = Frame.BOTTOM.getCoordinate();

        // 绘制垂直线
        float oneThirdCropWidth = Frame.getWidth() / 3;

        float x1 = left + oneThirdCropWidth;
        canvas.drawLine(new Point(x1, top), new Point(x1, bottom), mGuidelinePaint);
        float x2 = right - oneThirdCropWidth;
        canvas.drawLine(new Point(x2, top), new Point(x2, bottom), mGuidelinePaint);

        // 画水平线
        float oneThirdCropHeight = Frame.getHeight() / 3;

        float y1 = top + oneThirdCropHeight;
        canvas.drawLine(new Point(left, y1), new Point(right, y1), mGuidelinePaint);

        float y2 = bottom - oneThirdCropHeight;
        canvas.drawLine(new Point(left, y2), new Point(right, y2), mGuidelinePaint);
    }

    private void drawBorder(Canvas canvas) {
        RectFloat rect = new RectFloat(
                Frame.LEFT.getCoordinate(),
                Frame.TOP.getCoordinate(),
                Frame.RIGHT.getCoordinate(),
                Frame.BOTTOM.getCoordinate());
        canvas.drawRect(rect, mBorderPaint);
    }

    private void onActionDown(float x, float y) {
        float left = Frame.LEFT.getCoordinate();
        float top = Frame.TOP.getCoordinate();
        float right = Frame.RIGHT.getCoordinate();
        float bottom = Frame.BOTTOM.getCoordinate();
        mPressedHandle = HandleUtil.getPressedHandle(x, y, left, top, right, bottom, M_HANDLE_RADIUS);
        HandleUtil.getOffset(mPressedHandle, x, y, left, top, right, bottom, mTouchOffset);

    }

    private void onActionUp() {
        if (mPressedHandle != null) {
            mPressedHandle = null;
        }
    }

    private void onActionMove(float x, float y) {
        if (mPressedHandle == null) {
            return;
        }
        x += mTouchOffset.getPointX();
        y += mTouchOffset.getPointY();
        mPressedHandle.refreshCropWindow(x, y, mPixelMapRect, M_SNAP_RADIUS);
    }
}