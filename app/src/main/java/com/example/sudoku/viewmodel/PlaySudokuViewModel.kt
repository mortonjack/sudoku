package com.example.sudoku.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sudoku.game.SudokuGame

class PlaySudokuViewModel : ViewModel() {
    // TODO: Create interaction between view & backend
    val sudokuGame = SudokuGame(9)
}