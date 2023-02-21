package com.example.sudoku.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.R
import com.example.sudoku.game.Board
import com.example.sudoku.viewmodel.PlaySudokuViewModel
import com.example.sudoku.viewmodel.PlaySudokuViewModelFactory
import kotlinx.android.synthetic.main.sudoku_activity.*

class PlaySudokuActivity : AppCompatActivity(), SudokuBoardView.onTouchListener {

    /* 'lateinit' keyword: declare a variable that is guaranteed
     * to be initialised in the future.
     * [var].isInitialized to check if its been initialised
     */
    private lateinit var viewModel: PlaySudokuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sudoku_activity)

        sudokuBoardView.registerListener(this)

        // Get view model for this activity
        val difficulty = intent.getIntExtra("difficulty", 0)
        viewModel = ViewModelProvider(this, PlaySudokuViewModelFactory(difficulty)).get(PlaySudokuViewModel::class.java)
        // Observe selectedCellLiveData for updates. Call updateSelectedCellUI when it updates.
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer{ updateSelectedCellUI(it)})
        viewModel.sudokuGame.boardLiveData.observe(this, Observer{ updateBoard(it) })

        // Get the toolbar view from the layout resource file
        val toolbar = findViewById<Toolbar>(R.id.sudokuToolbar)

        // Set the toolbar as the action bar for the activity
        setSupportActionBar(toolbar)

        // Customize the toolbar
        supportActionBar?.apply {
            title = "Sudoku - ${when (difficulty) {
                0 -> "Easy"
                1 -> "Medium"
                2 -> "Hard"
                3 -> "Extreme"
                else -> "(Secret) Expert"
            }}"
            setDisplayHomeAsUpEnabled(true)
        }

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

        buttonUndo.setOnClickListener {
            viewModel.sudokuGame.undoMove()
        }

        /* buttonHint.setOnClickListener {
            viewModel.sudokuGame.fillHint()
        } */

        buttons.forEachIndexed {i, button ->
            button.setOnClickListener {
                viewModel.sudokuGame.updateCellNum(i+1)
                if (viewModel.sudokuGame.won()) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("You win!")
                    builder.setPositiveButton("Analyse Game") { _, _ ->
                        // Do something when win
                    }
                    builder.setNegativeButton("Exit") { _, _ ->
                        // Go back
                        super.onBackPressed()
                    }

                    builder.show()
                }
            }
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to quit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ -> super.onBackPressed() }
            .setNegativeButton("No, take me back!") { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
            buttonNote.setTextColor(ContextCompat.getColor(this, R.color.button))
        }
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }
}