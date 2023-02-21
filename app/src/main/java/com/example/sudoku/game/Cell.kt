package com.example.sudoku.game

class Cell(private val size: Int, var starting: Boolean = false) {
    // Value of cell
    var value = -1
    var expectedValue = 0

    // Notes array
    private val notes = BooleanArray(size)
    var noteCount = 0

    // Add/remove from notes
    fun flipNote(num: Int) {
        if (num <= 0 || num > size) {
            // Clear value if num doesn't exist
            for (i in 0 until size) notes[i] = false
            noteCount = 0
        } else {
            // Otherwise, flip note value
            notes[num - 1] = !notes[num - 1]
            if (!notes[num-1]) noteCount--
            else noteCount++
        }
    }

    fun isNote(num: Int): Boolean {
        if (num <= 0 || num > size) return false
        return notes[num-1]
    }

    // Change value of cell
    fun changeVal(num: Int) {
        // Change number if cell isn't starting cell
        if (!starting) {
            value = num
        }
    }
}