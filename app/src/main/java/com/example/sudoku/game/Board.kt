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

    fun updateCell (row: Int, col: Int, num: Int, note: Boolean) {
        if (note) grid[row][col].flipNote(num)
        else grid[row][col].changeVal(num)
    }

    fun cellValue(row: Int, col: Int): Int {
        if (row <= 0 || col <= 0 || row > size || col > size) return -1
        return grid[row][col].value
    }

}