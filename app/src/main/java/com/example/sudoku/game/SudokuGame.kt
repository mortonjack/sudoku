package com.example.sudoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame(sqrtSize: Int, difficulty: Int) {
    private val size: Int = sqrtSize*sqrtSize

    // lifecycle-aware data holder class. Obtain selected cell info.
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var boardLiveData = MutableLiveData<Board>()

    private var selectedRow = -1
    private var selectedCol = -1
    var note = false
    private val board = Board(sqrtSize)
    private val boardCreator = BoardCreator(sqrtSize, board)

    init {
        println("Initialising SudokuGame")
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        boardCreator.makeBoard(difficulty)
        println("Posting live data")
        boardLiveData.postValue(board)
        println("SudokuGame initialised")
    }

    // Check if won
    fun won(): Boolean {
        if (!board.won && board.incorrect == 0) {
            board.won = true
            return true
        }
        return false
    }

    // Update cell number
    fun updateCellNum(num: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        if (!note && board.cellValue(selectedRow, selectedCol) == num) return
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
        board.fillAllNotes(true)
        boardLiveData.postValue(board)
    }

    // Fill hint
    fun fillHint() {
        println("Filling in hint")
        println("Move type: ${boardCreator.humanMove(true)}")
        boardLiveData.postValue(board)
    }

    // Undo move
    fun undoMove() {
        board.undo()
        boardLiveData.postValue(board)
    }

}