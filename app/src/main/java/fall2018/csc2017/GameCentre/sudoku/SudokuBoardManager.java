package fall2018.csc2017.GameCentre.sudoku;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fall2018.csc2017.GameCentre.data.StateStack;
import fall2018.csc2017.GameCentre.util.BoardManagerForBoardGames;

public class SudokuBoardManager extends BoardManagerForBoardGames implements Serializable {

    /**
     * The board begin managed.
     */
    private SudokuBoard board;


    private int hint;

    /**
     * The cell currently selected
     */
    private Cell currentCell;


    private int currentPos;

    /**
     * The time has taken so far.
     */
    private long timeTaken;

    /**
     * The undoStack storing steps has taken.(limited capacity)
     */
    private StateStack<Integer[]> undoStack;

    /**
     * The default number of performUndo time.
     */
    private static final int DEFAULT_UNDO_LIMIT = 20;

    /**
     * The level of difficulty.
     */
    private static Integer levelOfDifficulty = 2;


    /**
     * Manage a new shuffled board.
     */
    public SudokuBoardManager() {
        super();
        List<Cell> cells = new ArrayList<>();
        Integer[][] newBoard = new BoardGenerator().getBoard();
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                cells.add(new Cell(row, column, newBoard[row][column]));
            }
        }
        Integer editable = 0;
        if (levelOfDifficulty == 1) {
            editable = 18;
            hint = 20;
        } else if (levelOfDifficulty == 2) {
            editable = 36;
            hint = 5;
        } else if (levelOfDifficulty == 3) {
            editable = 54;
            hint = 3;
        }
        Integer changed = 0;
        while (!changed.equals(editable)) {
            Random r = new Random();
            int index = r.nextInt(SudokuBoard.NUM_COL * SudokuBoard.NUM_ROW);
            if (!cells.get(index).isEditable()) {
                cells.get(index).makeEditable();
                cells.get(index).setFaceValue(0);
                changed++;
            }
        }
        this.board = new SudokuBoard(cells);
        this.timeTaken = 0L;
        this.undoStack = new StateStack<>(DEFAULT_UNDO_LIMIT);
    }

    /**
     * Get hint number.
     *
     * @return hint number
     */
    public int getHint() {
        return hint;
    }

    /**
     * Set hints number.
     *
     * @param hint
     */
    public void setHint(int hint) {
        this.hint = hint;
    }

    /**
     * Reduce hint.
     */
    public void reduceHint() {
        this.hint--;
    }

    /**
     * Return the current board.
     */
    public SudokuBoard getBoard() {
        return board;
    }

    /**
     * Get the time which the user has already used.
     */
    public long getTimeTaken() {
        return timeTaken;
    }

    /**
     * Setter function for time taken.
     */
    public void setTimeTaken(long timeTakenSoFar) {
        this.timeTaken = timeTakenSoFar;
    }

    /**
     * Add a move to the performUndo stack.
     */
    private void addUndo(Integer[] move) {
        undoStack.put(move);
    }

    /**
     * Set current cell.
     *
     * @param currentCell
     */
    public void setCurrentCell(Cell currentCell) {
        this.currentCell = currentCell;
    }

    /**
     * Return current cells.
     *
     * @return current cells
     */
    Cell getCurrentCell() {
        return currentCell;
    }

    /**
     * Returns if performUndo is available.
     */
    boolean undoAvailable() {
        return !undoStack.isEmpty();
    }

    /**
     * Get the performUndo step.
     */
    Integer[] popUndo() {
        return undoStack.pop();
    }


    /**
     * Setter for level of difficulty.
     */
    public static void setLevelOfDifficulty(int levelOfDifficulty) {
        SudokuBoardManager.levelOfDifficulty = levelOfDifficulty;
    }

    /**
     * Returns whether the sudoku puzzle has been solved.
     */
    public boolean boardSolved() {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (!board.checkCell(row, column)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Return whether the tap is valid.
     */
    public boolean isValidTap(int position) {
        return board.checkEditable(position / 9, position % 9);
    }

    /**
     * When a cell is taped, set it to be currentCell and highlight it.
     *
     * @param position The position of the cell taped.
     */
    public void makeMove(int position) {
        currentPos = position;
        if (currentCell != null) {
            currentCell.setHighlighted(false);
            currentCell.setFaceValue(currentCell.getFaceValue());
        }
        currentCell = this.board.getCell(position / SudokuBoard.NUM_COL,
                position % SudokuBoard.NUM_ROW);
        currentCell.setHighlighted(true);
        currentCell.setFaceValue(currentCell.getFaceValue());
        setChanged();
        notifyObservers();
    }

    /**
     * Update the face value of the board.
     */
    void updateValue(int value, boolean undo) {
        if (currentCell != null) {
            if (!undo)
                addUndo(new Integer[]{currentPos, currentCell.getFaceValue()});
            currentCell.setFaceValue(value);
            setChanged();
            notifyObservers();
        }

    }

    /**
     * Do all steps of an performUndo
     */
    void undo() {
        if (currentCell != null && undoStack.size() > 1) {
            // De-highlight cell
            currentCell.setHighlighted(false);
            currentCell.setFaceValue(0);
        }
        // Undo
        Integer[] move = popUndo();
        int position = move[0];
        int value = move[1];
        currentCell = board.getCell(position / SudokuBoard.NUM_COL,
                position % SudokuBoard.NUM_ROW);
        if (!undoStack.isEmpty())
            currentCell.setHighlighted(false);
        updateValue(value, true);
        currentCell.setFaceValue(currentCell.getFaceValue());
        // Highlight the next cell in performUndo stack
        if (!undoStack.isEmpty()) {
            move = this.undoStack.get();
            position = move[0];
            currentCell = board.getCell(position / SudokuBoard.NUM_COL,
                    position % SudokuBoard.NUM_ROW);
            currentCell.setHighlighted(true);
            currentPos = position;
            currentCell.setFaceValue(currentCell.getFaceValue());
        }
        setChanged();
        notifyObservers();
    }
}






















