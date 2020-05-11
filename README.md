# README #

## Changelog

04/22 - April
* Created project in IntelliJ
* Added scripts from Chess project into src 
* Started to set up the UI for the board

04/23 - April
* Attempting to convert gridLayout to gridView because I mixed them up.
* [X] Fix gridView, because items aren't showing.

04/23 - Krysti
* Added some logic around cell clicks

04/24 - April
* Completed nbasic gridView of the board that can get the move input
* [ ] Need to decide if PlayActivity class should control the game loop

04/25 - Krysti
* Refactored project structure
* Began logic for cell click interactions

04/26 - Krysti
* Connected board with original Chess logic
* Allowed board to check validness prior to pieces moving
* Fixed glitch with board not showing


04/28 - April
* Working on game states - saving games, replaying games, and undoing moves.
    - through GameStates, States, and GameSaver.
* Completed undo move function, needs thorough testing.
* Completed random move function, needs thorough testing.
* Implemented replay list activity to show the list of all saved games.
* Implemented replay activiy to step through a saved game.
* Testing for all these are needed!

04/28 - Krysti
* Reviewed and tested most of April's changes
* Optimized get random move function
* Refactored how we handle button clicks (moved logic to .xml file)

04/29 - Krysti
* Implemented Draw Button logic
* Implemented Resign Button logic

04/30 - April
* Maybe fixed the undo twice error by labeling states with their turn/state number.

05/03 - April
* [X] Found another error with the game states, where a move made cannot be undoed?
    - (start game) white makes a move -> black makes a move -> undo -> black's move is undoed, but cannot be done again
* [X] Found an error when deleting a gameState, where the listView isn't notified correctly of the change.
* Implemented the sort by date and sort by name in the Replay Activity List
* Added titles to the Replay Activity by adding titles to States

05/04 - April
* Fixed the above errors by 
    - making the game board create new cell instances during setBoard().
    - fixing how the listView is updated.
