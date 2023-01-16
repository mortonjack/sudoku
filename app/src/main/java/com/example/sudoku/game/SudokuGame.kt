package com.example.sudoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame(sqrtSize: Int) {
    private val size: Int = sqrtSize*sqrtSize

    // lifecycle-aware data holder class. Obtain selected cell info.
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var boardLiveData = MutableLiveData<Board>()

    private var selectedRow = -1
    private var selectedCol = -1
    var note = false
    private val board = Board(sqrtSize)

    init {
        println("sudokugame init")
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        val solved = BoardCreator(3, board)
        solved.makeBoard(3)
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
        if (selectedRow == row && selectedCol == col || row !in 0 until size || col !in 0 until size) {
            // Deselect if tapping on current cell, or if cell doesn't exist
            selectedRow = -1
            selectedCol = -1
        } else {
            selectedRow = row
            selectedCol = col
        }
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
    }

    // Fill all possible notes
    fun fillAllNotes() {
        board.fillAllNotes()
        boardLiveData.postValue(board)
    }

}