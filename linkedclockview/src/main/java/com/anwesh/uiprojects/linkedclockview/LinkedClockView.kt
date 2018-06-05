package com.anwesh.uiprojects.linkedclockview

/**
 * Created by anweshmishra on 05/06/18.
 */

import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.*

class LinkedClockView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        return true
    }

}