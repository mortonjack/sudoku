package com.example.sudoku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlaySudokuViewModelFactory(private val difficultyLevel: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PlaySudokuViewModel::class.java)) {
            PlaySudokuViewModel(difficultyLevel) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}