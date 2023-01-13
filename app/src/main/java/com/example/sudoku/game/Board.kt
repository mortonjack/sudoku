package com.example.sudoku.game

class Board(private val size: Int) {
    private val grid = List<List<Cell>>(size) { _ -> List<Cell>(size) { _ -> Cell(-1, size)} }

    // Initialise
    init {
        initialiseBoard(0)
    }

    private fun initialiseBoard(difficulty: Int) {
        // Initialise board with starting state
        // Accepts values 0 (easy), 1 (medium), 2 (hard), 3 (extreme)
    }

    // Update cell value/notes
    fun updateCell (row: Int, col: Int, num: Int, note: Boolean) {
        if (note) grid[row][col].flipNote(num)
        else {
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

}