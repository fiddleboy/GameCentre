package fall2018.csc2017.GameCentre.slidingTiles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fall2018.csc2017.GameCentre.data.StateStack;
import fall2018.csc2017.GameCentre.util.BoardManagerForBoardGames;

/**
 * Manage a board, including swapping tiles, checking for a win, and managing taps.
 */
public class SlidingTilesBoardManager extends BoardManagerForBoardGames implements Serializable {

    /**
     * The board being managed.
     */
    private SlidingTilesBoard board;

    /**
     * The time has taken so far.
     */
    private long timeTaken = 0L;

    /**
     * The undoStack storing steps has taken.(limited capacity)
     */
    private StateStack<Integer> undoStack;

    /**
     * The default number of performUndo time.
     */
    private static final int DEFAULT_UNDO_LIMIT = 3;

    /**
     * The number of steps the user have taken so far.
     */
    private int stepsTaken = 0;

    /**
     * The background of the image.
     */
    private byte[] imageBackground;

    /**
     * Manage a new shuffled board.
     */
    SlidingTilesBoardManager(int difficulty) {
        List<Integer> tiles = new ArrayList<>();
        final int numTiles = difficulty * difficulty;
        for (Integer tileNum = 1; tileNum != numTiles + 1; tileNum++) {
            tiles.add(tileNum);
        }
        Collections.shuffle(tiles);
        board = new SlidingTilesBoard(tiles, difficulty);
        do {Collections.shuffle(tiles);
            board = new SlidingTilesBoard(tiles, difficulty);
        } while (!isSolvable());
        undoStack = new StateStack<>(DEFAULT_UNDO_LIMIT);
    }

    /**
     * Manage a prepared board.
     */
    public SlidingTilesBoardManager(SlidingTilesBoard board) {
        this.board = board;
        this.undoStack = new StateStack<>(DEFAULT_UNDO_LIMIT);
    }

    /**
     * The getter function for the board.
     */
    public SlidingTilesBoard getBoard() {
        return this.board;
    }

    /**
     * Getter function for the number of performUndo allowed to perform.
     */
    int getCapacity() {return this.undoStack.getCapacity();}

    /**
     * Set performUndo limit.
     */
    void setCapacity(int input) {
        this.undoStack.setCapacity(input);
    }

    /**
     * Get level of difficulty of the board.
     */
    public int getDifficulty() {
        return board.getDifficulty();
    }

    /**
     * The getter function for the steps taken.
     */
    int getStepsTaken() {
        return stepsTaken;
    }

    /**
     * The setter function for the steps taken.
     */
    void setStepsTaken(int stepsTakenSoFar) {
        this.stepsTaken = stepsTakenSoFar;
    }

    /**
     * The getter function for the time taken.
     */
    public long getTimeTaken() {
        return timeTaken;
    }

    /**
     * The setter function for the time taken.
     */
    public void setTimeTaken(long timeTakenSoFar) {
        this.timeTaken = timeTakenSoFar;
    }

    /**
     * Return the current image background in byte array.
     */
    public byte[] getImageBackground() {
        return imageBackground;
    }

    /**
     * Set the image background of the board.
     */
    public void setImageBackground(byte[] imageBackground) {
        this.imageBackground = imageBackground;
    }

    /**
     * Returns if performUndo is available.
     */
    boolean undoAvailable() {
        return !undoStack.isEmpty();
    }

    /**
     * Add a move to the performUndo stack.
     */
    public void addUndo(Integer move) {
        undoStack.put(move);
    }

    /**
     * Get the performUndo step.
     */
    Integer popUndo() {
        return undoStack.pop();
    }

    /**
     * Determines whether the tile board is isSolvable.
     */
    public boolean isSolvable() {
        Iterator<Integer> tiles = board.iterator();
        ArrayList<Integer> listOfTiles = new ArrayList<>(board.numTiles());
        while (tiles.hasNext()) {
            listOfTiles.add(tiles.next());
        }
        if (board.numTiles() % 2 != 0) {
            return getTotalInversion(listOfTiles) % 2 == 0;
        } else {
            return blankPosition() % 2 == 0 && getTotalInversion(listOfTiles) % 2 != 0 ||
                    blankPosition() % 2 != 0 && getTotalInversion(listOfTiles) % 2 == 0;
            }
        }

    /**
     * Return the number of inversions in a list of Integer.
     * Note: This is a helper function for isSolvable.
     */
    public int getTotalInversion(ArrayList<Integer> listOfTiles) {
        int totalInversion = 0;
        for (int i = 0; i < board.numTiles() - 1; i++) {
            for (int j = i + 1; j < board.numTiles(); j++) {
                if (listOfTiles.get(i) != board.numTiles() &&
                        listOfTiles.get(i) > listOfTiles.get(j)) {
                    totalInversion++;
                }
            }
        }
        return totalInversion;
    }

    /**
     * Return the index of row which the blank tile is in.
     * Note: This is a helper function for isSolvable.
     */
    public int blankPosition() {
        for (int i = 0; i < board.getDifficulty(); i++) {
            for (int j = 0; j < board.getDifficulty(); j++) {
                if (board.getTile(i, j) == board.numTiles()) {
                    return board.getDifficulty() - i;
                }
            }
        }
        return -1;
    }

    /**
     * Return whether the tiles are in row-major order.
     */
    public boolean boardSolved() {
        Iterator<Integer> iterator = board.iterator();
        for (Integer i = 1; i < board.numTiles() + 1; i++) {
            if (!iterator.next().equals(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return whether any of the four surrounding tiles is the blank tile.
     *
     * @param position the tile to check
     * @return whether the tile at position is surrounded by a blank tile
     */
    public boolean isValidTap(int position) {
        int row = position / board.getDifficulty();
        int col = position % board.getDifficulty();
        int blankId = board.numTiles();
        Integer above = row == 0 ? null : board.getTile(row - 1, col);
        Integer below = row == board.getDifficulty() - 1 ? null : board.getTile(row + 1, col);
        Integer left = col == 0 ? null : board.getTile(row, col - 1);
        Integer right = col == board.getDifficulty() - 1 ? null : board.getTile(row, col + 1);
        return (below != null && below == blankId)
                || (above != null && above == blankId)
                || (left != null && left == blankId)
                || (right != null && right == blankId);
    }

    /**
     * Performs an in-undoable move and returns the position
     * of the blank tile before the move.
     */
    public int move(int position) {
        int row = position / board.getDifficulty();
        int col = position % board.getDifficulty();
        int blankId = board.numTiles();
        Integer above = row == 0 ? null : board.getTile(row - 1, col);
        Integer below = row == board.getDifficulty() - 1 ? null : board.getTile(row + 1, col);
        Integer left = col == 0 ? null : board.getTile(row, col - 1);
        if (above != null && above == blankId) {
            board.swapTiles(row - 1, col, row, col);
            return (row - 1) * board.getDifficulty() + col;
        } else if (below != null && below == blankId) {
            board.swapTiles(row + 1, col, row, col);
            return (row + 1) * board.getDifficulty() + col;
        } else if (left != null && left == blankId) {
            board.swapTiles(row, col - 1, row, col);
            return row * board.getDifficulty() + (col - 1);
        } else {
            board.swapTiles(row, col + 1, row, col);
            return row * board.getDifficulty() + (col + 1);
        }
    }

    /**
     * Performs the an undoable move.
     */
    public void makeMove(int position) {
        addUndo(move(position));
    }
}