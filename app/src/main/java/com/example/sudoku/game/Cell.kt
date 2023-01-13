package com.example.sudoku.game

class Cell(public val expectedValue: Int, private val size: Int, private val starting: Boolean = false) {
    // Value of cell
    var value = -1

    // Notes array
    private val notes = BooleanArray(size)

    // Add/remove from notes
    fun flipNote(num: Int) {
        // Return if num doesnt exist
        if (num <= 0 || num > size) return

        // Flip note value
        notes[num] = !notes[num]
        return
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