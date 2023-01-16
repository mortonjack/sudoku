package com.example.sudoku.game

class Board(private val sqrtSize: Int) {
    private val size: Int = sqrtSize*sqrtSize
    private val grid = List<List<Cell>>(size) { _ -> List<Cell>(size) { _ -> Cell(size)} }

    // Count of notes in rows/cols/blocks
    var rowNoteCount = Array<Array<Int>>(size) {Array<Int>(size){ _ -> 0 }}
    var colNoteCount = Array<Array<Int>>(size) {Array<Int>(size){ _ -> 0 }}
    var blockNoteCount = Array<Array<Int>>(size) {Array<Int>(size){ _ -> 0 }}
    var blockRowNoteCount = Array<Array<Array<Int>>>(size) {Array<Array<Int>>(sqrtSize) {Array<Int>(size){ _ -> 0 }}}
    var blockColNoteCount = Array<Array<Array<Int>>>(size) {Array<Array<Int>>(sqrtSize) {Array<Int>(size){ _ -> 0 }}}

    private fun updateNoteCount(row: Int, col: Int, num: Int, add: Int) {
        if (num == 0) {
            for (i in 1..size) {
                if (isNote(row, col, i)) updateNoteCount(row, col, i, -1)
                else updateNoteCount(row, col, i, 1)
            }
        }
        rowNoteCount[row][num] += add
        colNoteCount[col][num] += add
        val block: Int = sqrtSize*(row/sqrtSize) + col/sqrtSize
        blockNoteCount[block][num] += add
        blockRowNoteCount[block][row % sqrtSize][num] += add
        blockColNoteCount[block][col % sqrtSize][num] += add
    }

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
    }

    // Update cell value/notes
    fun updateCell (row: Int, col: Int, num: Int, note: Boolean) {
        if (note) {
            if (grid[row][col].isNote(num)) updateNoteCount(row, col, num, -1)
            else updateNoteCount(row, col, num, 1)
            grid[row][col].flipNote(num)
        }
        else {
            // Clear all notes from cell
            updateCell(row, col, 0, true)
            grid[row][col].changeVal(num)
            // Remove note from row/col
            for (i in 0 until size) {
                if (grid[i][col].isNote(num)) grid[i][col].flipNote(num)
                if (grid[row][i].isNote(num)) grid[row][i].flipNote(num)
            }
            // Remove note from block
            for (i in 3*(row/3) until 3*((row/3)+1)) {
                for (j in 3*(col/3) until 3*((col/3)+1)) {
                    if (grid[i][j].isNote(num)) grid[i][j].flipNote(num)
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
        if (row < 0 || col < 0 || col >= size || col >= size) return false
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
        var possible = Array<Boolean>(size) { _ -> true }

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
                grid[row][col].flipNote(i+1)
            }
        }
    }

    fun fillAllNotes() {
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
}