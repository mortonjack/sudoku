package com.example.sudoku.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.R
import com.example.sudoku.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.menu_activity.*

class MenuActivity : AppCompatActivity() {

    private lateinit var viewModel: MenuViewModel
    private var difficulty: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("CREATING MENU")
        setContentView(R.layout.menu_activity)

        // Get view model for this activity
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)

        continueGameButton.setOnClickListener {
            val continueIntent = Intent(this, PlaySudokuActivity::class.java)
            continueIntent.putExtra("difficulty", -1)
            startActivity(continueIntent)
        }

        newGameButton.setOnClickListener {
            val newGameIntent = Intent(this, PlaySudokuActivity::class.java)
            newGameIntent.putExtra("difficulty", difficulty)
            startActivity(newGameIntent)
        }

        easyButton.setOnClickListener {
            difficulty = 0
        }
        mediumButton.setOnClickListener {
            difficulty = 1
        }
        hardButton.setOnClickListener {
            difficulty = 2
        }
        extremeButton.setOnClickListener {
            difficulty = 3
        }

    }
}