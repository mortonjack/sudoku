package com.example.sudoku.game

import kotlin.math.min
import kotlin.math.sqrt

class BoardSolver(private val sqrtSize: Int) {
    private val size: Int = sqrtSize*sqrtSize
    var grid = Array<Array<Int>>(size) { Array<Int>(size) { _ -> 0 } }
    val board = Board(size)
    
    fun makeBoard() {
        // Step 1: Fill in diagonal boxes with random nums 1-9
        // 1 0 0
        // 0 1 0 <-- 1s represent boxes to fill in
        // 0 0 1
        for (box in 0 until 3) {
            val fill = (1..size).shuffled()
            var i = 0
            for (r in box*sqrtSize until (box*sqrtSize)+sqrtSize) {
                for (c in box*sqrtSize until (box*sqrtSize)+sqrtSize) {
                    grid[r][c] = fill[i++]
                }
            }
        }
        // Solve remainder of board
        board.initialiseBoard(grid)
        board.fillAllNotes()
        solveBoard(false)
        // Display board
        for (r in 0 until size) {
            for (c in 0 until size) {
                print(board.cellValue(r, c))
            }
            println("")
        }
    }

    // 'limit' denotes whether to return false if there are multiple valid solutions
    // by default, it will
    fun solveBoard(limit: Boolean = true): Boolean {
        // Step 1: Find lowest note count
        var low = size+1
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (board.cellValue(r, c) <= 0) {
                    low = min(low, board.noteCount(r, c))
                }
            }
        }
        // Return true if all cells are filled (low == size+1)
        if (low == size+1) return true
        // Return false if one cell has no notes
        if (low == 0) return false

        // Step 2: Look at all cells with this note count
        var found = false
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (board.cellValue(r, c) <= 0 && board.noteCount(r, c) == low) {
                    // Step 3: Fill in with random value & recurse
                    var nums = ArrayList<Int>()
                    for (i in 1..size) {
                        if (board.isNote(r, c, i)) nums.add(i)
                    }
                    nums.shuffle()
                    nums.forEach {
                        // Update board value
                        board.updateCell(r, c, it, false)
                        // Check result
                        val result = solveBoard(limit)
                        if (result) {
                            if (found) return false
                            if (!limit) return true
                            found = true
                        }

                        // Reset board value
                        board.updateCell(r, c, 0, false)
                        board.fillAllNotes()
                    }
                }
            }
        }
        return found
    }
}