package fall2018.csc2017.GameCentre.util;

import java.util.Observable;

import fall2018.csc2017.GameCentre.data.StateStack;


public abstract class BoardManagerForBoardGames extends Observable {

    /**
     * The board begin managed.
     */
    private BoardForBoardGames board;

    /**
     * The time has taken so far.
     */
    private long timeTaken;

    /**
     * The level of difficulty.
     */
    private static Integer levelOfDifficulty = 2;

    /**
     * The steps the user has taken so far.
     */
    private Integer stepsTaken;

    /**
     * The undoStack storing steps has taken.(limited capacity)
     */
    private StateStack undoStack;

    /**
     * The default Constructor for the SlidingTilesBoard Manager.
     */
    public BoardManagerForBoardGames() {
    }

    /**
     * Manage a board that has been pre-populated.
     */
    public BoardManagerForBoardGames(BoardForBoardGames board) {
        this.board = board;
    }

    ;

    /**
     * Return the current board.
     */
    public abstract BoardForBoardGames getBoard();

    /**
     * Getter function for the time the user used.
     */
    public abstract long getTimeTaken();

    /**
     * Setter function for the time the user used.
     */
    public abstract void setTimeTaken(long timeTakenSoFar);

    /**
     * Returns whether the sudoku puzzle has been solved.
     */
    public abstract boolean boardSolved();

    /**
     * Return whether the tap is valid.
     */
    public abstract boolean isValidTap(int position);

    /**
     * Performs changes to the board.
     */
    public abstract void makeMove(int position);
}
