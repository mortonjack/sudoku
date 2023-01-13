package com.example.sudoku.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.sudoku.game.Board
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

    // Board
    private var board: Board? = null

    private var listener: SudokuBoardView.onTouchListener? = null

    /* PAINT STYLES */

    // Style for thick lines
    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = when (colourMode) {
            1 -> Color.RED
            else -> Color.BLACK
        }
        strokeWidth = 9F
    }

    // Style for thin lines
    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = when (colourMode) {
            1 -> Color.RED
            else -> Color.BLACK
        }
        strokeWidth = 3F
    }

    // Selected cell paint
    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#f9fcae")
        }
    }

    // Same row/col/block cell paint. Unsure on a good name here!
    private val adjacentCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#aedffc")
        }
    }

    // Highlighted cell paint
    private val highlightCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#aefcb3")
        }
    }

    // Conflicting cell paint
    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#fcb3ae")
        }
    }

    // Starting number paint
    private val startPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#000000")
        }
        textSize = 80F
    }

    // Filled-in number paint
    private val numPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#0000FF")
        }
        textSize = 80F
    }

    // Note paint
    private val notePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = when (colourMode) {
            else -> Color.parseColor("#aaaaaa")
        }
        textSize = 30F
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

        // Update selected num
        if (board != null && selectedRow != -1 && selectedCol != -1) {
            selectedNum = board!!.cellValue(selectedRow, selectedCol)
        } else selectedNum = -1

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

        // Paint individual cells - Loop over board!
        for (r in 0 until size) {
            for (c in 0 until size) {
                // Check if cell has same value as selected value
                if ((r != selectedRow || c != selectedCol) && board!!.cellValue(r, c) == selectedNum) {
                    // Check for conflict
                    if (r == selectedRow || c == selectedCol || (r/3 == selectedRow/3 && c/3 == selectedCol/3) ) {
                        // Highlight conflicting cells
                        fillCell(canvas, r, c, conflictingCellPaint)
                    }
                    // Highlight non-conflicting cells with same num
                    else fillCell(canvas, r, c, highlightCellPaint)
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
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLinePaint)

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
                var num = board!!.cellValue(r, c)
                if (num <= 0 || num > size) {
                    // Draw notes instead
                } else {
                    // Draw num
                    val numString = num.toString()
                    val textBounds = Rect()
                    numPaint.getTextBounds(numString, 0, numString.length, textBounds)
                    val textWidth = numPaint.measureText(numString)
                    val textHeight = textBounds.height()

                    // Paint num in middle of cell
                    canvas.drawText(numString, (c*cellSizePixels) + cellSizePixels/2 - textWidth/2,
                    (r*cellSizePixels) + cellSizePixels/2 + textHeight/2, numPaint)
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

    fun registerListener(listener: SudokuBoardView.onTouchListener) {
        this.listener = listener
    }

    /* INTERFACES */
    interface onTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }
}