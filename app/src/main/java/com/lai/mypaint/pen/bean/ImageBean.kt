package com.lai.mypaint.pen.bean

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import com.lai.mypaint.PagerView

/**
 *
 * @author  Lai
 *
 * @time 2020/5/2 0:08
 * @describe describe
 *
 */
data class ImageBean(
    val bitmap: Bitmap,
    val rectF: RectF,
    var mHistoryMatrix: Matrix? = null,
    var mHistoryRectF: RectF? = null,
    var rotate: Float = 1f
)

data class PaintBean(
    val cover: Int,
    val type: PagerView.PenType,
    val images: List<ImageBean>? = null
)

