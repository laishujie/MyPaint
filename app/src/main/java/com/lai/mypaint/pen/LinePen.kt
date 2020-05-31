package com.lai.mypaint.pen

import android.graphics.*
import android.view.MotionEvent
import android.view.View

/**
 * @author: Lai
 * @createDate: 2020-04-24 15:46
 * @description:
 */
class LinePen(private val mBufferCanvas: Canvas) : IPen {

    private val mLinePaint by lazy {
        initLinePaint()
    }
    val mClearPaint by lazy {
        val paint1 = Paint()
        paint1.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        paint1
    }

    private var mPath = Path()
    private var mDrawPath = Path()
    private val mPrePoint = PointF()


    private fun initLinePaint(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true//抗锯齿
        paint.isDither = true//防抖动
        paint.strokeJoin = Paint.Join.ROUND//设置圆角,让绘制的线条更圆滑
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE
        paint.color = Color.GRAY
        paint.strokeWidth = 20f
        /*if (mIsTape && mShader != null) {
            mLinePaint.setColor(Color.WHITE)
            mLinePaint.setShader(mShader)
        }
        if (mDashPathEffect != null) {
            mLinePaint.setPathEffect(mDashPathEffect)
        }
        if (mShadowRadius > 0) {
            mLinePaint.setShadowLayer(
                DensityUtil.dp2px(mContext, mShadowRadius),
                0, 0, mShadowColor
            )
        }
        mLinePaint.setAlpha(mAlpha)*/
        return paint
    }

    override fun downEvent(pagerView: View, event: MotionEvent) {
        mPrePoint.set(event.x, event.y)
        mPath.moveTo(mPrePoint.x, mPrePoint.y)
        mDrawPath.moveTo(mPrePoint.x, mPrePoint.y)
    }
    override fun moveEvent(pagerView: View, event: MotionEvent) {
        val endX = (event.x + mPrePoint.x) / 2
        val endY = (event.y + mPrePoint.y) / 2
        mPath.quadTo(
            mPrePoint.x,
            mPrePoint.y,
            endX,
            endY
        )
        mDrawPath.addPath(mPath)
        mBufferCanvas.drawPath(mPath, mLinePaint)
        mPrePoint.set(event.x, event.y)
        mPath.reset()
        mPath.moveTo(endX, endY)
        pagerView.invalidate()
    }

    override fun upEvent(event: MotionEvent) {
        //mPath.reset()
    }

    override fun drawPen(pagerView: View) {
        mBufferCanvas.drawPath(mDrawPath,mLinePaint)
    }

    override fun rest() {
    }

}