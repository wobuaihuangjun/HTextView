package com.hanks.htextview.animatetext;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;

import com.hanks.htextview.util.DisplayUtils;

/**
 * 线条边框流动
 * Created by hanks on 15-12-17.
 */
public class LineText extends HText {

    float progress = 0;
    float ANIMA_DURATION = 800;

    float mTextHeight = 0;

    Paint linePaint;
    float padding; // distance between text and line
    float gap;
    PointF p1 = new PointF();
    PointF p2 = new PointF();
    PointF p3 = new PointF();
    PointF p4 = new PointF();
    PointF p5 = new PointF();
    PointF p6 = new PointF();
    PointF p7 = new PointF();
    PointF p8 = new PointF();
    int xLineLength;

    private float distWidth; // line width when animation end
    private float distHeight;

    private float yLineLength;
    private float xLineWidth;

    private int yLineShort;
    private int xLineShort;

    @Override
    protected void initVariables() {

        xLineWidth = DisplayUtils.dp2px(mHTextView.getContext(), 1.5f);
        padding = DisplayUtils.dp2px(mHTextView.getContext(), 15);
        gap = DisplayUtils.dp2px(mHTextView.getContext(), 5);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(mHTextView.getCurrentTextColor());
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(xLineWidth);

    }

    @Override
    protected void animatePrepare(CharSequence text) {
        //2. 计算文字所在矩形，可以得到宽高
        Rect bounds = new Rect();
        mPaint.getTextBounds(mText.toString(), 0, mText.length(), bounds);
        mTextHeight = bounds.height();

        // 文字区域宽高
        distWidth = bounds.width() + padding * 2 + xLineWidth;
        distHeight = bounds.height() + padding * 2 + xLineWidth;

        // 控件布局宽高
        xLineLength = mHTextView.getWidth();
        yLineLength = mHTextView.getHeight();
    }

    @Override
    protected void animateStart(CharSequence text) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1)
                .setDuration((long) ANIMA_DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                mHTextView.invalidate();
            }
        });
        valueAnimator.start();
        progress = 0;
    }

    @Override
    protected void drawFrame(Canvas canvas) {
        float percent = progress;
        // 线条长度=文字所在矩形宽或高+边距*2+线条宽度

        Log.d("drawFrame", percent + "");
        // 计算横向 和 纵向 线条的动态宽度  mHTextView.getWidth()->distWidth
        xLineLength = (int) (mHTextView.getWidth()
                - (mHTextView.getWidth() - distWidth + gap) * percent);
        yLineLength = (int) (mHTextView.getHeight()
                - (mHTextView.getHeight() - distHeight + gap) * percent);

        // x轴从0到文字右顶点（包括线条宽度）
        p1.x = (mHTextView.getWidth() / 2 + distWidth / 2 + xLineWidth / 2 - gap) * percent;
        p1.y = (mHTextView.getHeight() - distHeight) / 2;
        float p1XStart = p1.x - xLineLength - xLineWidth;// 由于精度丢失，线条之间可能会有细微的缝隙，可在线条尾对绘制xLineWidth的长度
        canvas.drawLine(p1XStart, p1.y, p1.x, p1.y, linePaint);//上

        p2.x = (mHTextView.getWidth() + distWidth) / 2;
        p2.y = (mHTextView.getHeight() / 2 + distHeight / 2 + xLineWidth / 2 - gap) * percent;
        float p2YStart = p2.y - yLineLength - xLineWidth;
        canvas.drawLine(p2.x, p2YStart, p2.x, p2.y, linePaint);

        p3.x = mHTextView.getWidth()
                - (mHTextView.getWidth() / 2 + distWidth / 2 + xLineWidth / 2 - gap) * percent;
        p3.y = (mHTextView.getHeight() + distHeight) / 2;
        float p3XStart = p3.x + xLineLength + xLineWidth;
        canvas.drawLine(p3XStart, p3.y, p3.x, p3.y, linePaint);

        p4.x = (mHTextView.getWidth() / 2 - distWidth / 2);
        p4.y = mHTextView.getHeight()
                - (mHTextView.getHeight() / 2 + distHeight / 2 + xLineWidth / 2 - gap) * percent;
        float p4YStart = p4.y + yLineLength + xLineWidth;
        canvas.drawLine(p4.x, p4YStart, p4.x, p4.y, linePaint);


        // 离开的线条
        xLineShort = (int) ((distWidth + gap) * (1 - percent));
        yLineShort = (int) ((distHeight + gap) * (1 - percent));

        p5.x = (mHTextView.getWidth() / 2 + distWidth / 2);
        p5.y = (mHTextView.getHeight() - distHeight) / 2;
        canvas.drawLine(p5.x - xLineShort, p5.y, p5.x, p5.y, linePaint);

        p6.x = (mHTextView.getWidth() / 2 + distWidth / 2);
        p6.y = (mHTextView.getHeight() / 2 + distHeight / 2);
        canvas.drawLine(p6.x, p6.y - yLineShort, p6.x, p6.y, linePaint);

        p7.x = mHTextView.getWidth() - (mHTextView.getWidth() / 2 + distWidth / 2 - gap);
        p7.y = (mHTextView.getHeight() + distHeight) / 2;
        canvas.drawLine(p7.x + xLineShort, p7.y, p7.x, p7.y, linePaint);

        p8.x = (mHTextView.getWidth() / 2 - distWidth / 2);
        p8.y = mHTextView.getHeight() - (mHTextView.getHeight() / 2 + distHeight / 2 - gap);
        canvas.drawLine(p8.x, p8.y + yLineShort, p8.x, p8.y, linePaint);

        canvas.drawText(mText, 0, mText.length(), startX, startY, mPaint);

    }
}
