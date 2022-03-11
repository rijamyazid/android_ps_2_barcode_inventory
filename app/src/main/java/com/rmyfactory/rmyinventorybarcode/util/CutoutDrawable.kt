package com.rmyfactory.rmyinventorybarcode.util

import android.graphics.*
import android.view.View
import androidx.annotation.Nullable
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel


internal class CutoutDrawable @JvmOverloads constructor(@Nullable shapeAppearanceModel: ShapeAppearanceModel? = null) :
    MaterialShapeDrawable(shapeAppearanceModel ?: ShapeAppearanceModel()) {
    private val cutoutPaint: Paint
    private val cutoutBounds: RectF
    private var savedLayer = 0
    private fun setPaintStyles() {
        cutoutPaint.style = Paint.Style.FILL_AND_STROKE
        cutoutPaint.color = Color.WHITE
        cutoutPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }

    fun hasCutout(): Boolean {
        return !cutoutBounds.isEmpty
    }

    fun setCutout(left: Float, top: Float, right: Float, bottom: Float) {
        // Avoid expensive redraws by only calling invalidateSelf if one of the cutout's dimensions has
        // changed.
        if (left != cutoutBounds.left || top != cutoutBounds.top || right != cutoutBounds.right || bottom != cutoutBounds.bottom) {
            cutoutBounds[left, top, right] = bottom
            invalidateSelf()
        }
    }

    fun setCutout(bounds: RectF) {
        setCutout(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    fun removeCutout() {
        // Call setCutout with empty bounds to remove the cutout.
        setCutout(0f, 0f, 0f, 0f)
    }

    override fun draw(canvas: Canvas) {
        preDraw(canvas)
        super.draw(canvas)

        // Draw mask for the cutout.
        canvas.drawRect(cutoutBounds, cutoutPaint)
        postDraw(canvas)
    }

    private fun preDraw(canvas: Canvas) {
        val callback = callback
        if (useHardwareLayer(callback)) {
            val viewCallback: View? = callback as View?
            // Make sure we're using a hardware layer.
            if (viewCallback?.layerType != View.LAYER_TYPE_HARDWARE) {
                viewCallback?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            }
        } else {
            // If we're not using a hardware layer, save the canvas layer.
            saveCanvasLayer(canvas)
        }
    }

    private fun saveCanvasLayer(canvas: Canvas) {
        savedLayer =
            canvas.saveLayer(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat(), null)
    }

    private fun postDraw(canvas: Canvas) {
        if (!useHardwareLayer(callback)) {
            canvas.restoreToCount(savedLayer)
        }
    }

    private fun useHardwareLayer(callback: Callback?): Boolean {
        return callback is View
    }

    init {
        cutoutPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        setPaintStyles()
        cutoutBounds = RectF()
    }
}