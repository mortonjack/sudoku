# About

A fully-playable sudoku app. It automatically generates a board based off selected difficulty, ensuring only a single valid solution exists.
The user is tasked with filling out the board in such a way that every row, column, and block contains each number from 1 to 9.
Sudoku is a logic puzzle, and to assist with this, the player may note down candidates in each cell, 
or press a button to automatically fill in every cell's notes with all potential candidates. 

The current release generates a board by recursively deleting a set number of starting cells, backtracking when multiple solutions are created.
To read about this method, and potential future improvements, read my blog post [here](https://mortonjack.github.io/blog/generating-sudokus/).

# Implemented features
- User input
- Cell highlighting
- Notes
- Board auto-generation
- Difficulty levels based off number of starting cells
- Board validity check
- Auto-fill notes
- Auto-delete notes (always on)
- User-selected difficulty
- Menu screen
- Undo button
- Dark mode
- Win screen

# View code
Code can be found within app/src/main/java/com/example/sudoku/
