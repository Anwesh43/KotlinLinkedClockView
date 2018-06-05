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

    data class State(var j : Int = 0, var dir : Float = 0f, var prevScale : Float = 0f) {

        val scales : Array<Float> = arrayOf(0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                dir = 0f
                prevScale = scales[j]
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
            paint.color = Color.parseColor("#16a085")
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val gap : Float = (h / CLOCK_NODES)
            val deg : Float = 360f / CLOCK_NODES
            paint.strokeCap = Paint.Cap.ROUND
            paint.strokeWidth = Math.min(w, h) / 60
            canvas.save()
            canvas.translate(w/2, gap * i + gap/2)
            canvas.drawCircle(0f, 0f, gap/3, paint)
            canvas.save()
            canvas.rotate(deg * i + deg * state.scales[1])
            canvas.drawLine(0f, 0f, 0f, -gap/4, paint)
            canvas.restore()
            canvas.restore()
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : ClockNode {
            var curr : ClockNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }
}