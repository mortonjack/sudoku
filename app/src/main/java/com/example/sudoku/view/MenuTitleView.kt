package com.example.sudoku.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class MenuTitleView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val colourMode = 0;
    private val title = "SUDOKU"

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#0000FF")
        }
        textSize = 0F
    }

    override fun onDraw(canvas: Canvas) {
        textPaint.textSize = height*0.8F

        val textBounds = Rect()
        val textWidth = textPaint.measureText(title)
        val textHeight = textBounds.height()
        canvas.drawText(title, width.toFloat()/2, height.toFloat()/2, textPaint)
    }
}