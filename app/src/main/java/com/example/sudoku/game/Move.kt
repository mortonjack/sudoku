package com.example.sudoku.game

import java.util.*

class Move(val row: Int, val col: Int, val num: Int, val note: Boolean) {
    val notes: Stack<Move> = Stack()
}