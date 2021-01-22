package com.xm6leefun.scan_lib.codescaner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.xm6leefun.scan_lib.R;
import com.xm6leefun.scan_lib.codescaner.camera.CameraManager;

final class ViewfinderView extends View {

    private static final int POINT_SIZE = 6;

    private CameraManager cameraManager;
    private final Paint paint;

    private int animationDelay = 0;
    private Bitmap laserLineBitmap;

    private int laserLineTop;            //扫描线最顶端位置
    private int laserLineHeight = 2;     //扫描线默认高度
    private int frameCornerLength = 13;  //扫描框4角长度，单位dp 默认15
    private int frameCornerWidth = 3;    //扫描框4角宽度，单位dp 默认2
    private int tipTextSize = 12;        //提示文字大小 单位：sp
    private int tipTextMargin = 26;      //提示文字与扫描框距离  单位:20dp
    private boolean tipTextPositionTop = false;    //提示框的位置  默认是在顶部
    private int tipTextColor = 0xffaaaaaa;       //提示文字颜色rgb值，默认白色
    private int frameOutsideColor = 0x73000000;   //扫描框以外区域半透明黑色   45%黑色透明度
    private int laserLineMoveSpeed = 6;           //扫描线移动间距，默认每毫秒移动6px，单位px
    private int frameStrokeColor =  0xff47abff;   //扫描边框颜色rgb值
    private float frameStrokeWidth = 2;           //扫描边框的宽度，单位px
    private int padding = 12; //四个角的间距
    //是否暂停
    private boolean isPause;
    private String textStr = "";//提示语

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        laserLineHeight = dp2px(laserLineHeight);
        frameCornerWidth = dp2px(frameCornerWidth);
        frameCornerLength = dp2px(frameCornerLength);

        tipTextSize = ScannerUtils.sp2px(getContext(), tipTextSize);
        tipTextMargin = dp2px(tipTextMargin);
    }

    void setTextStr(String textStr){
        this.textStr = textStr;
    }

    void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return;
        }
        Rect frame = cameraManager.getFramingRect();//取扫描框
        //取屏幕预览
        Rect previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }
        drawMask(canvas, frame); //绘制扫描框以外4个区域
        drawFrame(canvas, frame);//绘制扫描框
        drawFrameCorner(canvas, frame);//绘制扫描框4角
        drawText(canvas, frame);// 画扫描框下面的字
        if(!isPause){
            drawLaserLine(canvas, frame);//绘制扫描框内扫描线
            moveLaserSpeed(frame);//计算扫描框内移动位置
        } else {
            laserLineTop = 1000;
        }
    }

    private void moveLaserSpeed(Rect frame) {
        //初始化扫描线起始点为扫描框顶部位置
        if (laserLineTop == 0) {
            laserLineTop = frame.top;
        }
        int laserMoveSpeed = laserLineMoveSpeed;
        // 每次刷新界面，扫描线往下移动 LASER_VELOCITY
        laserLineTop += laserMoveSpeed;
        if (laserLineTop >= frame.bottom) {
            laserLineTop = frame.top;
        }
        if (animationDelay == 0) {
            animationDelay = (int) ((1.0f * 1000 * laserMoveSpeed) / (frame.bottom - frame.top));
        }

        // 只刷新扫描框的内容，其他地方不刷新
        postInvalidateDelayed(animationDelay, frame.left - POINT_SIZE, frame.top - POINT_SIZE, frame.right + POINT_SIZE, frame.bottom + POINT_SIZE);
    }


    /**
     * 画扫描框外区域
     *
     * @param canvas
     * @param frame
     */
    private void drawMask(Canvas canvas, Rect frame) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        paint.setColor(frameOutsideColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
    }

    /**
     * 绘制提示文字
     *
     * @param canvas
     * @param frame
     */
    private void drawText(Canvas canvas, Rect frame) {
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(tipTextColor);
        textPaint.setTextSize(tipTextSize);

        float x = frame.left;//文字开始位置
        //文字在扫描框上方还是上文，默认上方
        float y = tipTextPositionTop ? frame.top - tipTextMargin:frame.bottom + tipTextMargin ;

        StaticLayout staticLayout = new StaticLayout(textStr, textPaint, frame.width(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0, false);
        canvas.save();
        canvas.translate(x, y);
        staticLayout.draw(canvas);
        canvas.restore();

    }

    /**
     * 绘制扫描不到的提示语
     *
     * @param canvas
     * @param frame
     */
    private void drawScanTipText(Canvas canvas, Rect frame) {
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(tipTextColor);
        textPaint.setTextSize(tipTextSize);

        float x = frame.left;//文字开始位置
        //文字在扫描框上方还是上文，默认上方
        float y = tipTextPositionTop ? frame.top - tipTextMargin:frame.bottom + tipTextMargin ;

        StaticLayout staticLayout = new StaticLayout("将二维码/条形码放入框内，即可自动扫描", textPaint, frame.width(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0, false);
        canvas.save();
        canvas.translate(x, y);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制扫描框4角
     *
     * @param canvas
     * @param frame
     */
    private void drawFrameCorner(Canvas canvas, Rect frame) {
        paint.setColor(getResources().getColor(R.color._47abff));
        paint.setStyle(Paint.Style.FILL);
        // 左上角
        canvas.drawRect(frame.left - frameCornerWidth - padding, frame.top-padding, frame.left-padding, frame.top -padding + frameCornerLength, paint);
        canvas.drawRect(frame.left - frameCornerWidth - padding, frame.top - frameCornerWidth-padding, frame.left-padding + frameCornerLength, frame.top-padding, paint);
        // 右上角
        canvas.drawRect(frame.right + padding, frame.top - padding, frame.right + padding + frameCornerWidth, frame.top - padding + frameCornerLength, paint);
        canvas.drawRect(frame.right - frameCornerLength + padding, frame.top - frameCornerWidth - padding, frame.right + frameCornerWidth + padding, frame.top - padding, paint);
        // 左下角
        canvas.drawRect(frame.left - frameCornerWidth - padding, frame.bottom - frameCornerLength + padding, frame.left - padding, frame.bottom + padding, paint);
        canvas.drawRect(frame.left - frameCornerWidth - padding, frame.bottom + padding, frame.left + frameCornerLength - padding, frame.bottom + frameCornerWidth + padding, paint);
        // 右下角
        canvas.drawRect(frame.right + padding, frame.bottom - frameCornerLength + padding, frame.right + frameCornerWidth + padding, frame.bottom + padding, paint);
        canvas.drawRect(frame.right - frameCornerLength + padding, frame.bottom + padding, frame.right + frameCornerWidth + padding, frame.bottom + frameCornerWidth + padding, paint);
    }

    /**
     * 画扫描框
     *
     * @param canvas
     * @param frame
     */
    private void drawFrame(Canvas canvas, Rect frame) {
        paint.setColor(frameStrokeColor);//扫描边框色
        paint.setStrokeWidth(frameStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(frame, paint);
    }

    /**
     * 画扫描线
     *
     * @param canvas
     * @param frame
     */
    private void drawLaserLine(Canvas canvas, Rect frame) {
        if (laserLineBitmap == null)//图片资源文件转为 Bitmap
            laserLineBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.scan_line);

        Rect laserRect = new Rect(frame.left, laserLineTop, frame.right, laserLineTop + laserLineHeight);
        canvas.drawBitmap(laserLineBitmap, null, laserRect, paint);
    }

    public void onResume(){
        isPause = false;
        invalidate();
    }

    public void onPause(){
        isPause = true;
    }


    void laserLineBitmapRecycle() {
        if (laserLineBitmap != null) {
            laserLineBitmap.recycle();
            laserLineBitmap = null;
        }
    }

    private int dp2px(int dp) {
        return ScannerUtils.dp2px(getContext(), dp);
    }
}
