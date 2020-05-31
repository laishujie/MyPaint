package com.lai.mypaint.pen

import android.graphics.*
import android.view.MotionEvent
import android.view.View
import com.lai.mypaint.pen.bean.ImageBean
import kotlin.math.abs
import kotlin.math.atan


/**
 * @author: Lai
 * @createDate: 2020-04-24 16:13
 * @description:
 */
class ImagePen(private val mBufferCanvas: Canvas) : IPen {
    override fun rest() {
        mHistoryImageList.clear()
        mImageList = null
    }

    private var mDownPoint = PointF()
    private var mCurrBitmapPosint = 0

    private val mHistoryImageList by lazy {
        mutableListOf<ImageBean>()
    }

    private val mImagePaint by lazy {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.isDither = true
        paint
    }

    private val mLinePaint by lazy {
        initLinePaint()
    }

    private fun initLinePaint(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true//抗锯齿
        paint.isDither = true//防抖动
        paint.strokeJoin = Paint.Join.ROUND//设置圆角,让绘制的线条更圆滑
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE
        paint.color = Color.GRAY
        paint.strokeWidth = 5f
        return paint
    }

    private var mImageList: List<ImageBean>? = null

    fun setImageList(imageList: List<ImageBean>?) {
        this.mImageList = imageList
    }

    private val mMatrix = Matrix()

    //上一个Image的位置
    private var mLastImageRect = RectF()
    private var mCurrImageRect = RectF()
    private var mTempImageRect = RectF()

    override fun downEvent(pagerView: View, event: MotionEvent) {
        mDownPoint.set(event.x, event.y)


        // mBufferCanvas.drawRect(200f,200f,)
    }


    override fun moveEvent(pagerView: View, event: MotionEvent) {
        getDrawBitmap()?.apply {
            val imageBean = this
            mMatrix.reset()
            mBufferCanvas.save()
            mMatrix.postTranslate(event.x, event.y)
            mCurrImageRect.set(imageBean.rectF)
            //映射坐标
            mMatrix.mapRect(mCurrImageRect)
            var calulateXYAnagle = calulateXYAnagle(mDownPoint.x, mDownPoint.y, event.x, event.y)
            if (!calulateXYAnagle.isNaN()) {
                mBufferCanvas.rotate(
                    calulateXYAnagle,
                    mCurrImageRect.centerX(),
                    mCurrImageRect.centerY()
                )
            } else {
                calulateXYAnagle = 1f;
            }
            if (isInCircle(
                    mLastImageRect.centerX(),
                    mLastImageRect.centerY(),
                    mCurrImageRect.centerX(),
                    mCurrImageRect.centerY(),
                    mCurrImageRect.width()
                )
            ) {
                mBufferCanvas.drawBitmap(bitmap, mMatrix, mImagePaint)
                mBufferCanvas.drawRect(mCurrImageRect, mLinePaint)
                val bean =
                    ImageBean(bitmap, rectF, Matrix(mMatrix), RectF(mCurrImageRect), calulateXYAnagle)
                mHistoryImageList.add(bean)
                mTempImageRect.set(mCurrImageRect)
                mLastImageRect = mTempImageRect
                pagerView.invalidate()
                mCurrBitmapPosint++
            }
            mBufferCanvas.restore()

        }
    }

    //判断点是否存在圆内
    private fun isInCircle(
        centerCircleX: Float,
        centerCircleY: Float,
        targetX: Float,
        targetY: Float,
        radius: Float
    ): Boolean {
        val targetDistance =
            Math.sqrt(
                Math.abs((targetX - centerCircleX) * (targetX - centerCircleX) + (targetY - centerCircleY) * (targetY - centerCircleY))
                    .toDouble()
            )
        return targetDistance > radius
    }

    /**
     * 矩形缩放
     * @param rectF
     * @param scale
     */
    fun scale(rectF: RectF, scale: Float) {
        val width = rectF.width()
        val height = rectF.height()

        val newWidth = scale * width
        val newHeight = scale * height

        val dx = (newWidth - width) / 2
        val dy = (newHeight - height) / 2

        rectF.left -= dx
        rectF.top -= dy
        rectF.right += dx
        rectF.bottom += dy
    }

    /**
     * 旋转
     * @param rect
     * @param centerX
     * @param centerY
     * @param rotateAngle
     */
    fun rotate(rect: RectF, centerX: Float, centerY: Float, rotateAngle: Float) {
        val x = rect.centerX()
        val y = rect.centerY()
        val sinA = Math.sin(Math.toRadians(rotateAngle.toDouble())).toFloat()
        val cosA = Math.cos(Math.toRadians(rotateAngle.toDouble())).toFloat()
        // 使用三角形公式计算新的位置
        val newX = centerX + (x - centerX) * cosA - (y - centerY) * sinA
        val newY = centerY + (y - centerY) * cosA + (x - centerX) * sinA

        val dx = newX - x
        val dy = newY - y

        rect.offset(dx, dy)
    }

    /**
     * @param rc1 第一个矩阵的位置
     * @param rc2 第二个矩阵的位置
     * @return 两个矩阵是否重叠（边沿重叠，也认为是重叠）
     */
    fun isOverlap(rc1: RectF, rc2: RectF): Boolean {
        return rc1.left + rc1.width() > rc2.left &&
                rc2.left + rc2.width() > rc1.left &&
                rc1.top + rc1.height() > rc2.top &&
                rc2.top + rc2.height() > rc1.top
    }

    /**
     * 根据两点计算方向角度
     * @param startx
     * @param starty
     * @param endx
     * @param endy
     * @return
     */
    private fun calulateXYAnagle(
        startx: Float, starty: Float, endx: Float,
        endy: Float
    ): Float {
        val tan = (atan(abs((endy - starty) / (endx - startx))) * 180 / Math.PI).toFloat()
        return if (endx > startx && endy > starty)
        // 第一象限
        {
            -tan
        } else if (endx > startx && endy < starty)
        // 第二象限
        {
            tan
        } else if (endx < startx && endy > starty)
        // 第三象限
        {
            tan - 180
        } else {
            180 - tan
        }
    }


    private fun getDrawBitmap(): ImageBean? {
        mImageList?.also {
            if (mCurrBitmapPosint > it.size - 1)
                mCurrBitmapPosint = 0
            return mImageList?.get(mCurrBitmapPosint)
        }
        return null
    }


    override fun upEvent(event: MotionEvent) {

    }

    override fun drawPen(pagerView: View) {
        mHistoryImageList.forEach {
            if (it.mHistoryMatrix == null || it.mHistoryRectF == null) return
            mBufferCanvas.save()
            mBufferCanvas.rotate(
                it.rotate,
                it.mHistoryRectF!!.centerX(),
                it.mHistoryRectF!!.centerY()
            )
            mBufferCanvas.drawBitmap(it.bitmap, it.mHistoryMatrix!!, mImagePaint)
            mBufferCanvas.drawRect(it.mHistoryRectF!!, mLinePaint)
            mBufferCanvas.restore()
        }
    }

}