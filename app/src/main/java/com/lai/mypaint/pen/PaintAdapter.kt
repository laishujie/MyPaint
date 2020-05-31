package com.lai.mypaint.pen

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lai.mypaint.PagerView
import com.lai.mypaint.R
import com.lai.mypaint.pen.bean.ImageBean
import com.lai.mypaint.pen.bean.PaintBean

/**
 *
 * @author  Lai
 *
 * @time 2020/5/2 15:25
 * @describe describe
 *
 */
class PaintAdapter(context: Context) : BaseQuickAdapter<PaintBean, BaseViewHolder>(
    R.layout.item_paint, createList(context)
) {
    override fun convert(holder: BaseViewHolder, item: PaintBean) {
        holder.setImageResource(R.id.iv_image, item.cover)
    }

    companion object {
        fun createList(context: Context): MutableList<PaintBean> {
            return mutableListOf(
                PaintBean(R.mipmap.ic_eraser, PagerView.PenType.ERASER),
                PaintBean(
                    R.mipmap.ic_paint,
                    PagerView.PenType.COLOR_LINE
                ),
                PaintBean(
                    R.mipmap.ic_clothes_1,
                    PagerView.PenType.IMAGE,
                    getImageBeans(context)
                )
            )
        }

        private fun getImageBeans(context: Context): ArrayList<ImageBean> {
            val list = ArrayList<ImageBean>()
            val bitmap1 =
                BitmapFactory.decodeResource(context.resources, R.mipmap.ic_clothes_1)
            val bitmap2 =
                BitmapFactory.decodeResource(context.resources, R.mipmap.ic_clothes_2)
            val bitmap3 =
                BitmapFactory.decodeResource(context.resources, R.mipmap.ic_clothes_3)
            val bitmap4 =
                BitmapFactory.decodeResource(context.resources, R.mipmap.ic_clothes_4)
            val bitmap5 =
                BitmapFactory.decodeResource(context.resources, R.mipmap.ic_clothes_5)

            list.add(
                ImageBean(
                    bitmap1,
                    RectF(0f, 0f, bitmap1.width.toFloat(), bitmap1.height.toFloat())
                )
            )
            list.add(
                ImageBean(
                    bitmap2,
                    RectF(0f, 0f, bitmap1.width.toFloat(), bitmap1.height.toFloat())
                )
            )
            list.add(
                ImageBean(
                    bitmap3,
                    RectF(0f, 0f, bitmap1.width.toFloat(), bitmap1.height.toFloat())
                )
            )
            list.add(
                ImageBean(
                    bitmap4,
                    RectF(0f, 0f, bitmap1.width.toFloat(), bitmap1.height.toFloat())
                )
            )
            list.add(
                ImageBean(
                    bitmap5,
                    RectF(0f, 0f, bitmap1.width.toFloat(), bitmap1.height.toFloat())
                )
            )
            return list
        }
    }
}

