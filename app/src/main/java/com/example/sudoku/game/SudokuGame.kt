package com.example.sudoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame(private val size: Int) {

    // lifecycle-aware data holder class. Obtain selected cell info.
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var boardLiveData = MutableLiveData<Board>()

    private var selectedRow = -1
    private var selectedCol = -1
    private val board = Board(size)

    init {
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        boardLiveData.postValue(board)
    }

    // Update cell number
    fun updateCellNum(num: Int, note: Boolean = false) {
        if (selectedRow == -1 || selectedCol == -1) return
        board.updateCell(selectedRow, selectedCol, num, note)
        boardLiveData.postValue(board)
    }

    // Update cell selection
    fun updateSelectedCell(row: Int, col: Int) {
        if (selectedRow == row && selectedCol == col || row > size || col > size || row < 1 || col < 1) {
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