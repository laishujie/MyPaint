package com.lai.mypaint

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.lai.mypaint.pen.EraserPen
import com.lai.mypaint.pen.IPen
import com.lai.mypaint.pen.ImagePen
import com.lai.mypaint.pen.LinePen
import com.lai.mypaint.pen.bean.ImageBean
import java.util.*

/**

 * @author: Lai
 * @createDate: 2020-04-21 15:56
 * @description:
 */
class PagerView : View {
    private var mBufferBitmap: Bitmap? = null
    private var mBufferCanvas: Canvas? = null
    private var mImageList: List<ImageBean>? = null

    private var mDraPenArrayDeque = ArrayDeque<IPen>()
    private var mmBackArrayDeque = ArrayDeque<IPen>()

    //撤销
    fun undo(pagerView: PagerView) {
        if (mDraPenArrayDeque.isNotEmpty()) {
            mBufferCanvas?.drawPaint(mClearPaint)
            mmBackArrayDeque.push(mDraPenArrayDeque.pop())
            mDraPenArrayDeque.descendingIterator().forEach {
                it.drawPen(pagerView)
            }

            /* mDraPenArrayDeque.forEachIndexed { index, iPen ->
                 Log.e("11111","pen "+iPen.javaClass.name+" index "+index)
                 iPen.drawPen(pagerView)
             }*/
            /*   mDraPenArrayDeque.forEach {
                   Log.e("11111","pen "+it.javaClass.name)
                   it.drawPen(pagerView)
               }*/
            pagerView.invalidate()
        }
    }

    //返回
    fun redo(pagerView: PagerView) {
        if (mmBackArrayDeque.isNotEmpty()) {
            mBufferCanvas?.drawPaint(mClearPaint)
            mDraPenArrayDeque.push(mmBackArrayDeque.pop())
            mDraPenArrayDeque.descendingIterator().forEach {
                it.drawPen(pagerView)
            }
            pagerView.invalidate()
        }
    }

    fun setImageList(imageList: List<ImageBean>) {
        this.mImageList = imageList
    }

    enum class PenType {
        COLOR_LINE, IMAGE, ERASER
    }

    private var mCurrType = PenType.COLOR_LINE

    fun setPenType(type: PenType) {
        this.mCurrType = type
    }


    private val mBitmapPaint by lazy {
        val paint1 = Paint()
        paint1.isAntiAlias = true
        paint1.isFilterBitmap = true
        paint1.isDither = true
        paint1
    }

    private val mClearPaint by lazy {
        val paint1 = Paint()
        paint1.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        paint1
    }
    private var mCurrPen: IPen? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas?) {
        canvas?.also {
            mBufferBitmap?.apply {
                canvas.drawBitmap(this, 0f, 0f, mBitmapPaint)
            }
        }
    }

    fun cleanPager() {
        mBufferCanvas?.drawPaint(mClearPaint)
        mmBackArrayDeque.forEach {
            it.rest()
        }
        mDraPenArrayDeque.forEach {
            it.rest()
        }
        mmBackArrayDeque.clear()
        mDraPenArrayDeque.clear()
        invalidate()
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mBufferCanvas?.also {
                    mCurrPen = createPen(it)
                    mCurrPen?.downEvent(this, event)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                mCurrPen?.moveEvent(this, event)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mCurrPen?.upEvent(event)
                mDraPenArrayDeque.push(mCurrPen)
            }
        }
        return true
    }

    private fun createPen(canvas: Canvas): IPen {
        return when (mCurrType) {
            PenType.IMAGE -> {
                val imagePen = ImagePen(canvas)
                imagePen.setImageList(mImageList)
                return imagePen
            }
            PenType.ERASER -> EraserPen(canvas)
            else -> LinePen(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mBufferBitmap == null || mBufferCanvas == null) {
            mBufferBitmap =
                Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            mBufferCanvas = Canvas(mBufferBitmap!!)
        }
    }


}