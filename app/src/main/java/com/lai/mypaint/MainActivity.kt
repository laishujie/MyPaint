package com.lai.mypaint

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lai.mypaint.pen.PaintAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private val mPaintAdapter by lazy {
        PaintAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_clean.setOnClickListener {
            pagerView.cleanPager()
        }

        rv_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_list.adapter = mPaintAdapter
        rv_list.itemAnimator = null

        mPaintAdapter.setOnItemClickListener { _, _, position ->
            val bean = mPaintAdapter.getItem(position)
            bean.images?.apply {
                pagerView.setImageList(this)
            }
            pagerView.setPenType(bean.type)
            Toast.makeText(this, "onItemClick " + position, Toast.LENGTH_LONG).show()
        }

        btn_undo.setOnClickListener {
            pagerView.undo(pagerView)
        }
        btn_redo.setOnClickListener {
            pagerView.redo(pagerView)
        }
    }
}
