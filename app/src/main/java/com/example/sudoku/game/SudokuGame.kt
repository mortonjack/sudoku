package com.example.sudoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame {

    // lifecycle-aware data holder class. Obtain selected cell info.
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()

    private var selectedRow = -1
    private var selectedCol = -1

    init {
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
    }

    // Updated selected row/col info
    fun updateSelectedCell(row: Int, col: Int) {
        if (selectedRow == row && selectedCol == col) {
            // Deselect if tapping on current cell
            selectedRow = -1
            selectedCol = -1
        } else {
            selectedRow = row
            selectedCol = col
        }
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
    }

}