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
        else grid[row][col].changeVal(num)
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

    fun fillAllNotes() {
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (cellValue(r, c) <= 0) {
                    // Fill in notes for possible values this could take
                    var possible = Array<Boolean>(size) { _ -> true }
                    // Check row/col
                    for (i in 0 until size) {
                        val rNum = cellValue(r, i)
                        val cNum = cellValue(i, c)
                        if (rNum > 0) possible[rNum-1] = false
                        if (cNum > 0) possible[cNum-1] = false
                    }
                    // Check block
                    for (i in 3*(r/3) until 3 *((r/3)+1)) {
                        for (j in 3*(c/3) until 3*((c/3)+1)) {
                            val num = cellValue(i, j)
                            if (num > 0) possible[num-1] = false
                        }
                    }
                    // Fill notes for possible values
                    for (i in 0 until size) {
                        if (possible[i] && !isNote(r, c, i+1)) {
                            grid[r][c].flipNote(i+1)
                        }
                    }
                }
            }
        }
    }

}