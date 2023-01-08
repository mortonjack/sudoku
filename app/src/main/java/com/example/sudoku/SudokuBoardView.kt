package com.example.sudoku

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt

class SudokuBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    // Select color mode (Default: 0, Dark mode: 1, more can be added!)
    private var colourMode = 0

    // Board size
    private var size = 9;
    private var sqrtSize = sqrt(size.toDouble()).toInt()

    // Width & height of board - for drawing
    private var cellSizePixels = 0F;

    // Player selection
    private var selectedRow = -1
    private var selectedCol = -1
    // Player can highlight cells containing a num
    private var selectedNum = -1

    /* PAINT STYLES */

    // Style for thick lines
    private val thickLinePaint = Paint().apply() {
        style = Paint.Style.STROKE
        color = when (colourMode) {
            1 -> Color.RED
            else -> Color.BLACK
        }
        strokeWidth = 8F
    }

    // Style for thin lines
    private val thinLinePaint = Paint().apply() {
        style = Paint.Style.STROKE
        color = when (colourMode) {
            1 -> Color.RED
            else -> Color.BLACK
        }
        strokeWidth = 2F
    }

    // Selected cell paint
    private val selectedCellPaint = Paint().apply() {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#f9fcae")
        }
    }

    // Same row/col/block cell paint. Unsure on a good name here!
    private val adjacentCellPaint = Paint().apply() {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#aedffc")
        }
    }

    // Highlighted cell paint
    private val highlightCellPaint = Paint().apply() {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#aefcb3")
        }
    }

    // Conflicting cell paint
    private val conflictingCellPaint = Paint().apply() {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#fcb3ae")
        }
    }

    /* FUNCTION OVERRIDES */

    // Override to ensure board is square
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Constrain width & height by min value
        val pixelSize = widthMeasureSpec.coerceAtMost(heightMeasureSpec)
        setMeasuredDimension(pixelSize, pixelSize)
    }

    // Add functionality to draw function - draw the board
    override fun onDraw(canvas: Canvas) {
        // Divide width of view by amount of cells
        cellSizePixels = (width.coerceAtMost(height)/size).toFloat()
        // Draw cell highlights
        fillAllCells(canvas)
        // Draw grid OVER highlights
        drawLines(canvas)
    }

    // Select cell when player touches it
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null || event.action != MotionEvent.ACTION_DOWN) return false;

        // Update selected row/col
        val newCol = (event.x/cellSizePixels).toInt()
        val newRow = (event.y/cellSizePixels).toInt()
        if (newRow == selectedRow && newCol == selectedCol || newCol >= size || newRow >= size) {
            // Unselect if tapping on same cell
            selectedRow = -1;
            selectedCol = -1;
        } else {
            // Update to new cell
            selectedRow = newRow;
            selectedCol = newCol;
        }

        // Invalidate board drawing (re-draw board)
        invalidate()
        return true;
    }

    /* FUNCTIONS */

    // Draw cell highlights
    private fun fillAllCells(canvas: Canvas) {
        // Ensure
        if ((selectedRow == -1 || selectedCol == -1) && selectedNum == -1) return

        // Draw rectangle around col
        canvas.drawRect(
            selectedCol * cellSizePixels,
            0F,
            (selectedCol + 1) * cellSizePixels,
            size * cellSizePixels,
            adjacentCellPaint
        )
        // Draw rectangle around row
        canvas.drawRect(
            0F,
            selectedRow * cellSizePixels,
            size * cellSizePixels,
            (selectedRow + 1) * cellSizePixels,
            adjacentCellPaint
        )
        // Draw rectangle around block
        canvas.drawRect(
            selectedCol / sqrtSize * cellSizePixels * sqrtSize,
            selectedRow / sqrtSize * cellSizePixels * sqrtSize,
            (selectedCol + sqrtSize) / sqrtSize * cellSizePixels * sqrtSize,
            (selectedRow + sqrtSize) / sqrtSize * cellSizePixels * sqrtSize,
            adjacentCellPaint
        )

        // Paint individual cells
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (r == selectedRow && c == selectedCol) {
                    // Highlight selected cell
                    fillCell(canvas, r, c, selectedCellPaint)
                } /*else if (TODO: HIGHLIGHT CELLS WITH SELECTED NUM) {
                    // Highlight cells with selected number (real or in comments)
                    fillCell(canvas, r, c, highlightCellPaint)
                } else if (TODO: HIGHLIGHT CONFLICTING CELLS) {
                    // Highlight conflicting cells
                    fillCell(canvas, r, c, conflictingCellPaint)
                }*/
            }
        }
    }

    // Highlight cell
    private fun fillCell(canvas: Canvas, r: Int, c: Int, paint: Paint) {
        canvas.drawRect(
            c * cellSizePixels,
            r * cellSizePixels,
            (c + 1) * cellSizePixels,
            (r + 1) * cellSizePixels,
            paint
        )
    }

    // Draw board grid
    private fun drawLines(canvas: Canvas) {
        // Draw outline
        val len = width.coerceAtMost(height).toFloat()
        canvas.drawRect(0F, 0F, len, len, thickLinePaint)

        for (i in 1 until size) {
            // In Kotlin, 'if' and 'when' (aka 'switch') statements are expressions
            // which can return values
            val paintToUse = if (i % sqrtSize == 0) thickLinePaint else thinLinePaint

            // Draw vertical lines
            canvas.drawLine(
                i * cellSizePixels,
                0F,
                i * cellSizePixels,
                len,
                paintToUse
            )
            // Draw horizontal lines
            canvas.drawLine(
                0F,
                i * cellSizePixels,
                len,
                i * cellSizePixels,
                paintToUse
            )
        }
    }
}