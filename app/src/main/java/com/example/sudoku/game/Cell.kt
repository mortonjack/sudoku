package com.example.sudoku.game

class Cell(val expectedValue: Int, private val size: Int, val starting: Boolean = false) {
    // Value of cell
    var value = -1

    // Notes array
    private val notes = BooleanArray(size)

    // Add/remove from notes
    fun flipNote(num: Int) {
        if (num <= 0 || num > size) {
            // Clear value if num doesn't exist
            for (i in 0 until size) notes[i] = false
        } else {
            // Otherwise, flip note value
            notes[num - 1] = !notes[num - 1]
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
            // TODO: Error-stuff if val != expected val
        }
    }
}