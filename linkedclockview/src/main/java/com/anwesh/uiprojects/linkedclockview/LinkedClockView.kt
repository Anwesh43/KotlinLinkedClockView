package com.anwesh.uiprojects.linkedclockview

/**
 * Created by anweshmishra on 05/06/18.
 */

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.*

val CLOCK_NODES : Int = 5

class LinkedClockView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
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
            prev?.draw(canvas, paint)
            canvas.save()
            canvas.translate(w/2, gap * i - gap/2 + gap * state.scales[0])
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

    data class LinkedClock(var i : Int) {

        private var curr : ClockNode = ClockNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : LinkedClockView) {

        private val linkedClock : LinkedClock = LinkedClock(0)

        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            linkedClock.draw(canvas, paint)
            animator.animate {
                linkedClock.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            linkedClock.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) {
            val view : LinkedClockView = LinkedClockView(activity)
            activity.setContentView(view)
        }
    }
}