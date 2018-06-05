package com.anwesh.uiprojects.linkedclockview

/**
 * Created by anweshmishra on 05/06/18.
 */

import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.*

val CLOCK_NODES : Int = 5

class LinkedClockView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class ClockNode(var i : Int) {

        private val state : State = State()

        private var next : ClockNode? = null

        private var prev : ClockNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < CLOCK_NODES - 1) {
                next = ClockNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {

        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }
    }
}