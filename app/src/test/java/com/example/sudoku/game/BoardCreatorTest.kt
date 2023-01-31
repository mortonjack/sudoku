package com.example.sudoku.game

import org.junit.Test

internal class BoardCreatorTest {
    @Test
    fun singleCandidateOrPositionTest() {
        val board = Board(3)
        val grid = arrayOf(
            arrayOf(5, 3, 4, 6, 7, 8, 9, 1, 2),
            arrayOf(6, 7, 2, 1, 9, 5, 3, 4, 8),
            arrayOf(1, 9, 8, 3, 4, 2, 5, 6, 7),
            arrayOf(8, 5, 9, 7, 6, 1, 4, 2, 3),
            arrayOf(4, 2, 6, 8, 5, 3, 7, 9, 1),
            arrayOf(7, 1, 3, 9, 2, 4, 8, 5, 6),
            arrayOf(9, 6, 1, 5, 3, 7, 2, 8, 4),
            arrayOf(2, 8, 7, 4, 1, 9, 6, 3, 5),
            arrayOf(3, 4, 5, 2, 8, 6, 1, 7, 9)
        )
        val removed = Array (9) { BooleanArray(9) {false} }
        removed[8][8] = true
        assert(grid[8][8] == 9)
        board.initialiseBoard(grid, removed)
        assert(board.cellValue(8, 8) != 9)
        board.fillAllNotes()

        val boardCreator = BoardCreator(3, board)
        assert(boardCreator.humanMove(true) == 1)
        assert(board.cellValue(8, 8) == 9)
    }
}