package com.example.sudoku.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sudoku.game.SudokuGame

class PlaySudokuViewModel(difficulty: Int) : ViewModel() {
    val sudokuGame = SudokuGame(3, difficulty)
}