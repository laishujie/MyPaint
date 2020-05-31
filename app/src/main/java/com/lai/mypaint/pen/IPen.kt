package com.lai.mypaint.pen

import android.view.MotionEvent
import android.view.View

/**
 * @author: Lai
 * @createDate: 2020-04-24 15:43
 * @description:
 */
interface IPen {
    fun downEvent(pagerView: View,event: MotionEvent)
    fun moveEvent(pagerView: View, event: MotionEvent)
    fun upEvent(event: MotionEvent)
    fun drawPen(pagerView: View)
    fun rest()
}