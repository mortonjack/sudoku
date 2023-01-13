package com.example.sudoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame(private val size: Int) {

    // lifecycle-aware data holder class. Obtain selected cell info.
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var boardLiveData = MutableLiveData<Board>()

    private var selectedRow = -1
    private var selectedCol = -1
    var note = false
    private val board = Board(size)

    init {
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        boardLiveData.postValue(board)
    }

    // Update cell number
    fun updateCellNum(num: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        board.updateCell(selectedRow, selectedCol, num, note)
        boardLiveData.postValue(board)
    }

    // Update cell selection
    fun updateSelectedCell(row: Int, col: Int) {
        if (selectedRow == row && selectedCol == col || !(row in 0 until size) || !(col in 0 until size)) {
            // Deselect if tapping on current cell, or if cell doesn't exist
            selectedRow = -1
            selectedCol = -1
        } else {
            selectedRow = row
            selectedCol = col
        }
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
    }

}