package com.example.sudoku.game

import java.util.*

class Board(private val sqrtSize: Int, private val size: Int = sqrtSize*sqrtSize) {
    private val grid = List(size) { List(size) { Cell(size)} }
    private val moves: Deque<Move> = LinkedList()
    private val maxMovesSize = 500

    fun initialiseBoard(start: Array<Array<Int>>, removed: Array<BooleanArray>) {
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (start[r][c] != 0 && !removed[r][c]) {
                    grid[r][c].changeVal(start[r][c])
                    grid[r][c].starting = true
                } else {
                    grid[r][c].starting = false
                    grid[r][c].changeVal(0)
                }
            }
        }
        clearMoveStack()
    }

    // Update cell
    fun updateCell (row: Int, col: Int, num: Int, note: Boolean, connected: Boolean = false) {
        // Add move to move stack
        if (!connected) moves.addFirst(Move(row, col, if (!note) cellValue(row, col) else num, note))
        else if (!moves.isEmpty()) {
            if (!note || num != 0) moves.first.notes.add(Move(row, col, num, note))
            else {
                for (i in 1..size) {
                    if (isNote(row, col, i)) moves.first.notes.add(Move(row, col, i, true))
                }
            }
        }
        if (moves.size > maxMovesSize) moves.removeLast()

        // Update cell values
        if (note) grid[row][col].flipNote(num)
        else {
            // Clear all notes from cell
            updateCell(row, col, 0, true, connected = true)
            grid[row][col].changeVal(num)
            // Remove note from row/col
            for (i in 0 until size) {
                if (grid[i][col].isNote(num)) updateCell(i, col, num, true, connected = true)
                if (grid[row][i].isNote(num)) updateCell(row, i, num, true, connected = true)
            }
            // Remove note from block
            for (i in 3*(row/3) until 3*((row/3)+1)) {
                for (j in 3*(col/3) until 3*((col/3)+1)) {
                    if (grid[i][j].isNote(num)) updateCell(i, j, num, true, connected = true)
                }
            }
        }
    }

    // Return true if starting cell
    fun isStarting(row: Int, col: Int): Boolean {
        if (row < 0 || col < 0 || row >= size || col >= size) return false
        return grid[row][col].starting
    }

    // Return true if cell contains note for num
    fun isNote(row: Int, col: Int, num: Int): Boolean {
        if (row < 0 || col < 0 || row >= size || col >= size) return false
        if (cellValue(row, col) > 0) return false
        return grid[row][col].isNote(num)
    }

    // Return value of cell
    fun cellValue(row: Int, col: Int): Int {
        if (row < 0 || col < 0 || row >= size || col >= size) return -1
        return grid[row][col].value
    }

    // Return number of notes (caution: no input validation!)
    fun noteCount(row: Int, col: Int): Int {
        return grid[row][col].noteCount
    }

    private fun fillNotes(row: Int, col: Int) {
        // Store possible values this cell could take
        val possible = Array(size) { true }

        // Check row & col to remove possible values
        for (i in 0 until size) {
            val rNum = cellValue(row, i)
            val cNum = cellValue(i, col)
            if (rNum > 0) possible[rNum-1] = false
            if (cNum > 0) possible[cNum-1] = false
        }

        // Check block to remove possible values
        for (i in 3*(row/3) until 3 *((row/3)+1)) {
            for (j in 3*(col/3) until 3*((col/3)+1)) {
                val num = cellValue(i, j)
                if (num > 0) possible[num-1] = false
            }
        }

        // Fill notes for possible values
        for (i in 0 until size) {
            if (possible[i] && !isNote(row, col, i+1)) {
                updateCell(row, col, i+1, true, connected = true)
            }
        }
    }

    fun fillAllNotes(separateMove: Boolean) {
        if (separateMove) moves.add(Move(-1, -1, 0, false))
        for (row in 0 until size) {
            for (col in 0 until size) {
                // Fill notes for all cells without a value
                if (cellValue(row, col) < 1) fillNotes(row, col)
            }
        }
    }

    fun clearNotes() {
        for (row in 0 until size) {
            for (col in 0 until size) {
                updateCell(row, col, 0, true)
            }
        }
    }

    fun setExpectedValue(row: Int, col: Int) {
        if (row < 0 || col < 0 || row >= size || col >= size) return
        grid[row][col].expectedValue = grid[row][col].value
    }

    fun expectedValue(row: Int, col: Int): Int {
        if (row < 0 || col < 0 || row >= size || col >= size) return -1
        return grid[row][col].expectedValue
    }

    // Undo previous move
    fun undo() {
        //println("Undoing most recent move...")
        // Return early if no move to undo
        if (moves.isEmpty()) return

        // Pop previous move from stack
        val prev: Move = moves.first()
        moves.removeFirst()

        // Update previous move
        if (prev.row != -1) updateCell(prev.row, prev.col, prev.num, prev.note)
        else moves.add(Move(-1, -1, 0, false))
        //println("${prev.row}, ${prev.col}, ${prev.num}, ${if (prev.note) "note" else "num"} ")

        // Update notes too
        while (!prev.notes.isEmpty()) {
            val move = prev.notes.peek()
            prev.notes.pop()
            updateCell(move.row, move.col, move.num, move.note, connected = true)
        }

        // Remove this removal from the move stack
        moves.removeFirst()
    }

    fun clearMoveStack() {
        while (!moves.isEmpty()) moves.pop()
    }
}