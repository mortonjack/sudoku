package com.example.sudoku.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.R
import com.example.sudoku.game.Board
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
        viewModel.sudokuGame.boardLiveData.observe(this, Observer{ updateBoard(it) })

        // Call "Update cell num" for respective number when a button is pushed
        // I wish I could find a less annoying way to do this!
        val buttons = listOf(buttonOne, buttonTwo, buttonThree, buttonFour, buttonFive, buttonSix,
            buttonSeven, buttonEight, buttonNine)

        buttonNote.setOnClickListener {
            updateNoteSetting()
        }

        buttonFillNotes.setOnClickListener {
            viewModel.sudokuGame.fillAllNotes()
        }

        buttonDelete.setOnClickListener {
            viewModel.sudokuGame.updateCellNum(0)
        }

        buttons.forEachIndexed {i, button ->
            button.setOnClickListener {
                viewModel.sudokuGame.updateCellNum(i+1)
            }
        }
    }

    // Reminder: ? means it can take the value null. cell?.let means it only runs if cell isn't null
    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        // Update sudokuBoardView
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    private fun updateBoard(board: Board?) = board?.let {
        sudokuBoardView.updateBoard(board)
    }

    private fun updateNoteSetting() {
        viewModel.sudokuGame.note = !viewModel.sudokuGame.note
        if (viewModel.sudokuGame.note) {
            buttonNote.setTextColor(Color.parseColor("#f2d65c"))
        } else {
            buttonNote.setTextColor(Color.parseColor("#ffffff"))
        }
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }
}