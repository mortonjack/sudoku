package com.example.sudoku.game

import kotlin.Int.Companion.MAX_VALUE

class BoardCreator(private val sqrtSize: Int, private val board: Board) {
    private val size: Int = sqrtSize*sqrtSize
    private var grid = Array(size) { Array(size) { 0 } }

    private var removable = Array(size) { BooleanArray(size) { true } }
    private var removed = Array(size) { BooleanArray(size) { false } }


    // Count of notes in rows/cols/blocks
    private val rowNoteCount = Array(size) {Array(size+1){ 0 }}
    private val colNoteCount = Array(size) {Array(size+1){ 0 }}
    private val blockNoteCount = Array(size) {Array(size+1){ 0 }}
    private val blockRowNoteCount = Array(size) {Array(sqrtSize) {Array(size+1){ 0 }}}
    private val blockColNoteCount = Array(size) {Array(sqrtSize) {Array(size+1){ 0 }}}

    private fun getBlock(row: Int, col: Int): Int {
        return sqrtSize*(row/sqrtSize) + (col/sqrtSize)
    }

    private fun blockRow(row: Int, block: Int): Int {
        return row + 3*(block / 3)
    }

    private fun blockCol(col: Int, block: Int): Int {
        return col + 3*(block % 3)
    }
    
    fun makeBoard(difficulty: Int) {
        // Step 1: Fill in diagonal boxes with random nums 1-9
        // 1 0 0
        // 0 1 0 <-- 1s represent boxes to fill in
        // 0 0 1
        println("Filling in board...")
        for (box in 0 until sqrtSize) {
            val fill = (1..size).shuffled()
            var i = 0
            for (r in box*sqrtSize until (box*sqrtSize)+sqrtSize) {
                for (c in box*sqrtSize until (box*sqrtSize)+sqrtSize) {
                    grid[r][c] = fill[i++]
                }
            }
        }

        // Step 2: Create a random, valid solution to the board
        println("Creating solution...")
        board.initialiseBoard(grid, removed)
        board.fillAllNotes()
        backtrackSolve(false)
        // Copy full board to grid. Set board expected value
        for (r in 0 until size) {
            for (c in 0 until size) {
                grid[r][c] = board.cellValue(r, c)
                board.setExpectedValue(r, c)
            }
        }

        println("Removing squares...")
        // Step 3: Remove squares from grid based off difficulty
        val n = when (difficulty) {
            0 -> 42 // Easy
            1 -> 48 // Medium
            2 -> 52 // Hard
            3 -> 56 // Extreme
            else -> 60 // Expert
        }

        if (!removeSquares(n)) {
            println("ERROR: FAILED TO REMOVE SQUARES")
            makeBoard(difficulty)
            return
        }

        // Step 4: Ready board for play
        println("Preparing for play...")
        board.initialiseBoard(grid, removed)
        board.clearNotes()
    }

    /* Board creation functions */

    // Uses backtracking to solve the puzzle.
    // Returns 1 if there is only 1 valid solution, or more if there are multiple.
    // Set limit to false to allow an unlimited number of valid solutions.
    private fun backtrackSolve(limit: Boolean = true): Int {
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
        val nums = ArrayList<Int>()
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
            sols += backtrackSolve(limit)

            // Return true if found solution
            if (sols == 1 && !limit) return 1

            // Reset board value
            board.updateCell(row, col, 0, false)

            if (sols > 1) return sols
        }
        // Return true if a solution exists
        return sols
    }

    // Removes n squares from the board
    private fun removeSquares(n: Int): Boolean {
        // Remove n squares, one at a time

        // Step 1: Ensure this board only has 1 solution
        board.initialiseBoard(grid, removed)
        if (backtrackSolve() != 1) return false
        if (n == 0) return true

        // Step 2: Create list of removable squares
        val squares = ArrayList<Pair<Int, Int>>()
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

    // Looks for the easiest move a human could spot. Returns code representing move found.
    // Difficulty https://www.sudokuoftheday.com/difficulty
    fun humanMove(fill: Boolean = false): Int {
        // Clear note counts
        for (i in 0 until size) {
            for (j in 1 .. size) {
                rowNoteCount[i][j] = 0
                colNoteCount[i][j] = 0
                blockNoteCount[i][j] = 0
                for (b in 0 until sqrtSize) {
                    blockColNoteCount[i][b][j] = 0
                    blockRowNoteCount[i][b][j] = 0
                }
            }
        }
        // Fill in note counts
        for (r in 0 until size) {
            for (c in 0 until size) {
                for (i in 1 .. size) {
                    if (board.isNote(r, c, i)) {
                        rowNoteCount[r][i]++
                        colNoteCount[c][i]++
                        val block = getBlock(r, c)
                        blockNoteCount[block][i]++
                        blockRowNoteCount[block][r % 3][i]++
                        blockColNoteCount[block][c % 3][i]++
                    }
                }
            }
        }

        // Single candidate or Single position
        if (singleCandidateOrPosition(fill)) return 1

        // Candidate lines (ie one box has a number only in 1 row/col/etc)
        if (candidateLines(fill)) return 2

        // Double pairs

        // Multiple lines

        // Naked pair

        // Hidden pair

        // Naked triple

        // X-Wing

        // Forcing chains

        // Naked Quad

        // Hidden Quad

        // Swordfish

        // No move found, return -1
        return -1
    }

    /* Human Move Functions */

    // Fill single candidate (only 1 note in the cell)
    private fun fillSingleCandidate(row: Int, col: Int) {
        for (i in 1..size) {
            if (board.isNote(row, col, i)) {
                board.updateCell(row, col, i, false)
                return
            }
        }
    }

    // Fill single position (only 1 position in row/col/block with this note)
    private fun fillSinglePosition(type: Char, index: Int, num: Int) {
        println("Filling type $type")
        when (type) {
            'r' -> { // Row
                for (c in 0 until size) {
                    if (board.isNote(index, c, num)) {
                        board.updateCell(index, c, num, false)
                        return
                    }
                }
            }
            'c' -> { // Col
                for (r in 0 until size) {
                    if (board.isNote(r, index, num)) {
                        board.updateCell(r, index, num, false)
                        return
                    }
                }
            }
            'b' -> { // Block
                for (r in 0 until 3) {
                    for (c in 0 until 3) {
                        val row = blockRow(r, index)
                        val col = blockCol(c, index)
                        if (board.isNote(row, col, num)) {
                            board.updateCell(row, col, num, false)
                            return
                        }
                    }
                }
            }
        }
    }

    // Check for single candidate or position
    private fun singleCandidateOrPosition(fill: Boolean = false): Boolean {
        for (i in 0 until size) {
            for (j in 1.. size) {
                // Single candidate check
                if (board.noteCount(i, j-1) == 1) {
                    // Fill note with candidate
                    if (fill) fillSingleCandidate(i, j-1)
                    return true
                }
                // Single position check
                if (rowNoteCount[i][j] == 1) {
                    fillSinglePosition('r', i, j)
                    return true
                }
                if (colNoteCount[i][j] == 1) {
                    fillSinglePosition('c', i, j)
                    return true
                }
                if (blockNoteCount[i][j] == 1) {
                    fillSinglePosition('b', i, j)
                    return true
                }
            }
        }
        // None found, return false
        return false
    }

    // Remove notes from candidate lines (only 2 notes in a block in the same row/col)
    private fun fillCandidateLines(isRow: Boolean, index: Int, num: Int, block: Int) {
        if (isRow) {
            for (c in 0 until size) {
                if (board.isNote(index, c, num)) {
                    // Remove note if not in block
                    if (getBlock(index, c) != block) {
                        board.updateCell(index, c, num, true)
                    }
                }
            }
        } else {
            for (r in 0 until size) {
                if (board.isNote(r, index, num)) {
                    // Remove note if not in block
                    if (getBlock(r, index) != block) {
                        board.updateCell(r, index, num, true)
                    }
                }
            }
        }
    }

    // Check for candidate lines
    private fun candidateLines(fill: Boolean = false): Boolean {
        // Ugly function, prime for refactoring

        // Iterate over every block
        for (block in 0 until size) {
            // Iterate over every number per block
            for (i in 1 ..size) {
                // If there are only 2 of this number in this block
                if (blockNoteCount[block][i] == 2) {
                    var seenRow = -1
                    var seenCol = -1
                    // Iterate over every cell in the block
                    for (j in 0 until 3) {
                        for (k in 0 until 3) {
                            val r = blockRow(j, block)
                            val c = blockCol(k, block)
                            // If number is found
                            if (board.cellValue(r, c) == i) {
                                if (seenRow == -1) {
                                    seenRow = r
                                    seenCol = c
                                } else {
                                    // Return true if same row/col with deletion candidates
                                    if (seenRow == r && rowNoteCount[r][i] > 2) {
                                        if (fill) fillCandidateLines(true, r, i, block)
                                        return true
                                    }
                                    if (seenCol == c && colNoteCount[c][i] > 2) {
                                        if (fill) fillCandidateLines(true, c, i, block)
                                        return true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }
}