package com.example.sudoku.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.example.sudoku.R
import com.example.sudoku.game.Board
import kotlinx.android.synthetic.main.sudoku_activity.view.*
import kotlin.math.sqrt

class SudokuBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    // Board size
    private var size = 9
    private var sqrtSize = sqrt(size.toDouble()).toInt()

    // These values are set in onDraw. They note the size in pixels of cells & text
    private var cellSizePixels = 0F
    private var textSizePixels = 0F

    // Player selection
    private var selectedRow = -1
    private var selectedCol = -1
    // Player can highlight cells containing a num
    private var selectedNum = -1

    // Board
    private var board: Board? = null

    private var listener: onTouchListener? = null

    /* PAINT STYLES */

    // Style for outline
    private val outlinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.colorThickLine)
        strokeWidth = 18F
    }

    // Style for thick lines
    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.colorThickLine)
        strokeWidth = 9F
    }

    // Style for thin lines
    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.colorThickLine)
        strokeWidth = 3F
    }

    // Selected cell paint
    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.colorSelectedCell)
    }

    // Same row/col/block cell paint. Unsure on a good name here!
    private val adjacentCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.colorAdjacentCell)
    }

    // Highlighted cell paint
    private val highlightCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.colorHighlightCell)
    }

    // Conflicting cell paint
    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.colorConflictCell)
    }

    // Starting number paint
    private val startPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.colorStartNum)
        textSize = 0F
    }

    // Filled-in number paint
    private val numPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.colorPlayerNum)
        textSize = 0F
    }

    // Note paint
    private val notePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.colorNote)
        textSize = 0F
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
        // Update cell size & text size measurements
        updatePixelMeasurements(width.coerceAtMost(height))

        // Update selected num
        if (board != null && selectedRow != -1 && selectedCol != -1) {
            selectedNum = board!!.cellValue(selectedRow, selectedCol)
        }

        // Draw cell highlights
        fillAllCells(canvas)
        // Draw grid & text OVER highlights
        drawNums(canvas)
        drawLines(canvas)
    }

    // Select cell when player touches it
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null || event.action != MotionEvent.ACTION_DOWN) return false;

        // Obtain row/col info
        var newCol = (event.x/cellSizePixels).toInt()
        var newRow = (event.y/cellSizePixels).toInt()

        // Set to -1 if outside game board
        if (newCol >= size || newRow >= size) {
            newRow = -1;
            newCol = -1;
        }

        // Update selected row/col
        listener?.onCellTouched(newRow, newCol)
        return true;
    }

    /* PRIVATE FUNCTIONS */

    private fun updatePixelMeasurements(width: Int) {
        // Update size of cell
        cellSizePixels = (width/size).toFloat()
        // Update text sizes
        textSizePixels = cellSizePixels * 0.8F
        startPaint.textSize = textSizePixels
        numPaint.textSize = textSizePixels
        notePaint.textSize = textSizePixels/sqrtSize
    }

    // Draw cell highlights
    private fun fillAllCells(canvas: Canvas) {
        // Ensure
        if ((selectedRow == -1 || selectedCol == -1) && selectedNum == -1) return

        if (selectedRow != -1 && selectedCol != -1) {
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

            // Fill selected cell
            fillCell(canvas, selectedRow, selectedCol, selectedCellPaint)
        }

        if (board == null) return

        // Highlight connected/conflicting cells!
        if (selectedNum in 1 .. size) {
            for (r in 0 until size) {
                for (c in 0 until size) {
                    // Check if cell has same value as selected value
                    val cellValue = board!!.cellValue(r, c)
                    if ((r != selectedRow || c != selectedCol)
                        && cellValue == selectedNum
                        || (cellValue <= 0 && board!!.isNote(r, c, selectedNum))) {
                        // Check for conflict
                        if (r == selectedRow || c == selectedCol ||
                            (r / 3 == selectedRow / 3 && c / 3 == selectedCol / 3
                                    && selectedRow != -1 && selectedCol != -1)) {
                            // Highlight conflicting cells
                            fillCell(canvas, r, c, conflictingCellPaint)
                        }
                        // Highlight non-conflicting cells with same num
                        else fillCell(canvas, r, c, highlightCellPaint)
                    }
                }
            }
        }

        // Paint individual cells which are WRONG
        for (r in 0 until size) {
            for (c in 0 until size) {
                val cellValue = board!!.cellValue(r, c)
                // Check if cell value conflicts with expected value
                if (cellValue > 0 && cellValue != board!!.expectedValue(r, c)) {
                    fillCell(canvas, r, c, conflictingCellPaint)
                }
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
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), outlinePaint)

        for (i in 1 until size) {
            // In Kotlin, 'if' and 'when' (aka 'switch') statements are expressions
            // which can return values
            val paintToUse = if (i % sqrtSize == 0) thickLinePaint else thinLinePaint

            // Draw vertical lines
            canvas.drawLine(
                i * cellSizePixels,
                0F,
                i * cellSizePixels,
                height.toFloat(),
                paintToUse
            )
            // Draw horizontal lines
            canvas.drawLine(
                0F,
                i * cellSizePixels,
                width.toFloat(),
                i * cellSizePixels,
                paintToUse
            )
        }
    }

    private fun drawNums(canvas: Canvas) {
        for (r in 0 until size) {
            for (c in 0 until size) {
                val num = board?.cellValue(r, c) ?: return
                if (num <= 0 || num > size) {
                    // Draw notes instead
                    drawNotes(canvas, r, c)
                } else {
                    // Draw num
                    val numString = num.toString()
                    val textBounds = Rect()
                    val usedPaint = if (board!!.isStarting(r, c)) startPaint else numPaint
                    usedPaint.getTextBounds(numString, 0, numString.length, textBounds)
                    val textWidth = usedPaint.measureText(numString)
                    val textHeight = textBounds.height()

                    // Paint num in middle of cell
                    canvas.drawText(numString, (c*cellSizePixels) + cellSizePixels/2 - textWidth/2,
                    (r*cellSizePixels) + cellSizePixels/2 + textHeight/2, usedPaint)
                }
            }
        }
    }

    private fun drawNotes(canvas: Canvas, row: Int, col: Int) {
        val usedPaint = notePaint
        val unit = cellSizePixels/sqrtSize
        val x = col*cellSizePixels + cellSizePixels/2 - unit
        val y = row*cellSizePixels + cellSizePixels/2 - unit
        // x, y point to top left note area

        for (i in 0 until sqrtSize) {
            for (j in 0 until sqrtSize) {
                // Get num
                val num = i*sqrtSize + j + 1
                if (board!!.isNote(row, col, num)) {
                    // Draw num  - gather info
                    val numString = num.toString()
                    val textBounds = Rect()
                    usedPaint.getTextBounds(numString, 0, numString.length, textBounds)
                    val textWidth = usedPaint.measureText(numString)
                    val textHeight = textBounds.height()

                    // Paint num in middle of 1/size of cell
                    canvas.drawText(
                        numString,
                        x + (j * unit) - textWidth / 2,
                        y + (i * unit) + textHeight / 2,
                        usedPaint
                    )
                }
            }
        }
    }

    /* PUBLIC FUNCTIONS */
    fun updateBoard(board: Board) {
        this.board = board
        invalidate()
    }

    fun updateSelectedCellUI(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        invalidate() // redraw board view
    }

    fun registerListener(listener: onTouchListener) {
        this.listener = listener
    }

    /* INTERFACES */
    interface onTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }
}