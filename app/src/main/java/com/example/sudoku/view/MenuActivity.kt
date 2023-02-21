package com.example.sudoku.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sudoku.R
import kotlinx.android.synthetic.main.menu_activity.*

class MenuActivity : AppCompatActivity() {
    private var difficulty: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)

        /*continueGameButton.setOnClickListener {
            val continueIntent = Intent(this, PlaySudokuActivity::class.java)
            continueIntent.putExtra("difficulty", -1)
            startActivity(continueIntent)
        }*/

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