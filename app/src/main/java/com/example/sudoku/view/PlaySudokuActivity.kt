package com.example.sudoku.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.R
import com.example.sudoku.viewmodel.PlaySudokuViewModel
import kotlinx.android.synthetic.main.activity_main.*

class PlaySudokuActivity : AppCompatActivity(), SudokuBoardView.onTouchListener {

    /* 'lateinit' keyword: declare a variable that is guaranteed
     * to be initialised in the future.
     * [var].isInitialized to check if its been initialised
     */
    private lateinit var viewModel: PlaySudokuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sudokuBoardView.registerListener(this)

        // Get view model for this activity
        viewModel = ViewModelProvider(this).get(PlaySudokuViewModel::class.java)
        // Observe selectedCellLiveData for updates. Call updateSelectedCellUI when it updates.
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer{ updateSelectedCellUI(it)})
    }

    // Reminder: ? means it can take the value null. cell?.let means it only runs if cell isn't null
    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        // Update sudokuBoardView
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }
}