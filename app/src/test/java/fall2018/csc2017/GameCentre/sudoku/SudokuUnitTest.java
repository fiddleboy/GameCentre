package fall2018.csc2017.GameCentre.sudoku;


import android.content.Context;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import fall2018.csc2017.GameCentre.sudoku.SudokuBoard;
import fall2018.csc2017.GameCentre.sudoku.SudokuBoardManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SudokuUnitTest {

    /**
     * The SudokuBoardManager for test.
     */
    private SudokuBoardManager boardManager;

    /**
     * Make a solved SudokuBoard.
     */
    private void setUpCorrect() {
        this.boardManager = new SudokuBoardManager();
    }


    /**
     * Checks whether each row has numbers 1 to 9.
     */
    @Test
    public void horizontallySetUp() {
        setUpCorrect();
        SudokuBoard board = boardManager.getBoard();
        ArrayList<ArrayList<Integer>> horizontal =
                new ArrayList<ArrayList<Integer>>();
        for (int row = 0; row < 9; row++) {
            ArrayList<Integer> rows = new ArrayList<Integer>();
            for (int column = 0; column < 9; column++) {
                rows.add(board.getCell(row, column).getSolutionValue());
            }
            horizontal.add(rows);
        }
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                assertTrue(horizontal.get(row).contains(column + 1));
            }
        }
    }

    /**
     * Checks whether each column has numbers 1 to 9.
     */
    @Test
    public void verticallySetUp() {
        setUpCorrect();
        SudokuBoard board = boardManager.getBoard();
        ArrayList<ArrayList<Integer>> horizontal =
                new ArrayList<ArrayList<Integer>>();
        for (int column = 0; column < 9; column++) {
            ArrayList<Integer> rows = new ArrayList<Integer>();
            for (int row = 0; row < 9; row++) {
                rows.add(board.getCell(row, column).getSolutionValue());
            }
            horizontal.add(rows);
        }
        boolean result = true;
        for (int column = 0; column < 9; column++) {
            for (int row = 0; row < 9; row++) {
                if (!horizontal.get(column).contains(row + 1)) {
                    result = false;
                    break;
                };
            }
        }
        assertTrue(result);
    }

    /**
     * Checks whether each column has numbers 1 to 9.
     */
    @Test
    public void boxSetUp() {
        setUpCorrect();
        SudokuBoard board = boardManager.getBoard();
        ArrayList<ArrayList<Integer>> boxes =
                new ArrayList<ArrayList<Integer>>();
        int columnStarting = 0;
        int rowStarting = 0;
        while (columnStarting != 9) {
            ArrayList<Integer> box = new ArrayList<Integer>();
            for (int column = columnStarting;
                 column < columnStarting + 3;
                 column++) {
                if (column == columnStarting) {
                    box = new ArrayList<Integer>();
                }
                for (int row = rowStarting; row < rowStarting + 3; row++) {
                    box.add(board.getCell(row, column).getSolutionValue());
                }
            }
            boxes.add(box);
            if (rowStarting == 6) {
                rowStarting = 0;
                columnStarting += 3;
            } else {
                rowStarting += 3;
            }
        }
        boolean result = true;
        for (int boxNumber = 0; boxNumber < 9; boxNumber++) {
            for (int boxIndex = 0; boxIndex < 9; boxIndex++) {
                if (!boxes.get(boxNumber).contains(boxIndex + 1)) {
                    result = false;
                    break;
                };
            }
        }
        assertTrue(result);
    }

    /**
     * Test whether the level of difficulty works properly.
     */
    @Test
    public void levelOfDifficultyEasy() {
        SudokuBoardManager.setLevelOfDifficulty(1);
        setUpCorrect();
        int count = 0;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (boardManager.getBoard().getCell(row, column)
                        .getFaceValue().equals(0)) {
                    count += 1;
                }
            }
        }
        assertEquals(count, 18);
    }

    /**
     * Test whether the level of difficulty works properly.
     */
    @Test
    public void levelOfDifficultyMedium() {
        SudokuBoardManager.setLevelOfDifficulty(2);
        setUpCorrect();
        int count = 0;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (boardManager.getBoard().getCell(row, column)
                        .getFaceValue().equals(0)) {
                    count += 1;
                }
            }
        }
        assertEquals(count, 36);
    }

    /**
     * Test whether the level of difficulty works properly.
     */
    @Test
    public void levelOfDifficultyHard() {
        SudokuBoardManager.setLevelOfDifficulty(3);
        setUpCorrect();
        int count = 0;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (boardManager.getBoard().getCell(row, column)
                        .getFaceValue().equals(0)) {
                    count += 1;
                }
            }
        }
        assertEquals(count, 54);

    }


}
