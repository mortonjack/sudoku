package com.example.sudoku.game

import kotlin.Int.Companion.MAX_VALUE
import kotlin.math.min

class BoardCreator(private val sqrtSize: Int, private val board: Board) {
    private val size: Int = sqrtSize*sqrtSize
    private var grid = Array<Array<Int>>(size) { Array<Int>(size) { _ -> 0 } }

    private var removable = Array<BooleanArray>(size) { BooleanArray(size) { _ -> true } }
    private var removed = Array<BooleanArray>(size) { BooleanArray(size) { _ -> false } }
    
    fun makeBoard(difficulty: Int) {
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

        // Step 2: Create a random, valid solution to the board
        board.initialiseBoard(grid, removed)
        board.fillAllNotes()
        solveBoard(false)
        // Copy full board to grid. Set board expected value
        for (r in 0 until size) {
            for (c in 0 until size) {
                grid[r][c] = board.cellValue(r, c)
                board.setExpectedValue(r, c)
            }
        }

        // Step 3: Remove squares from grid based off difficulty
        val n = when (difficulty) {
            0 -> 42 // Easy
            1 -> 48 // Medium
            2 -> 52 // Hard
            3 -> 56 // Extreme
            else -> 56 // Default to extreme
        }
        if (!removeSquares(n)) {
            println("ERROR: FAILED TO REMOVE SQUARES")
            makeBoard(difficulty)
            return
        }

        // Step 4: Ready board for play
        board.initialiseBoard(grid, removed)
        board.clearNotes()
    }

    // Returns 1 if there is only 1 valid solution, or more if there are multiple.
    // Set limit to false to allow an unlimited number of valid solutions.
    private fun solveBoard(limit: Boolean = true): Int {
        // Step 1: Find cell with lowest note count
        board.fillAllNotes()
        var row = -1
        var col = -1
        var low = MAX_VALUE
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (board.cellValue(r, c) <= 0 && board.noteCount(r, c) < low) {
                    low = board.noteCount(r, c)
                    row = r
                    col = c
                }
            }
        }
        // Return true if all cells are filled
        if (row == -1 || col == -1) return 1
        // Return false if one cell has no notes
        if (low == 0) return 0


        // Step 2: Find all values that this cell can hold
        var nums = ArrayList<Int>()
        for (i in 1..size) {
            if (board.isNote(row, col, i)) nums.add(i)
        }

        // Step 3: Check each possible value in a random order
        var sols = 0
        nums.shuffle()
        nums.forEach {
            // Update board value
            board.updateCell(row, col, it, false)
            // Check result
            sols += solveBoard(limit)

            // Return true if found solution
            if (sols == 1 && !limit) return 1

            // Reset board value
            board.updateCell(row, col, 0, false)

            if (sols > 1) return sols
        }
        // Return true if a solution exists
        return sols
    }

    private fun removeSquares(n: Int): Boolean {
        // Remove n squares, one at a time

        // Step 1: Ensure this board only has 1 solution
        board.initialiseBoard(grid, removed)
        if (solveBoard() != 1) return false
        if (n == 0) return true

        // Step 2: Create list of removable squares
        var squares = ArrayList<Pair<Int, Int>>()
        var count = 0
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (removable[r][c]) {
                    squares.add(Pair(r, c))
                    count++
                }
            }
        }
        if (count < n) return false

        // Step 3: Randomly pick squares to remove. If none can be removed, backtrack.
        squares.shuffle()
        squares.forEach {
            val r = it.first
            val c = it.second
            removable[r][c] = false
            removed[r][c] = true
            if (removeSquares(n-1)) return true
            removed[r][c] = false
        }

        // Step 4: No squares are removable. Backtrack
        squares.forEach {
            val r = it.first
            val c = it.second
            removable[r][c] = true
        }
        return false
    }
}