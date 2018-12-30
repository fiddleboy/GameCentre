package fall2018.csc2017.GameCentre.sudoku;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SudokuBoardManagerTest {


    private SudokuBoardManager manager;
    private int moveTaken;

    /**
     * Set up necessary steps for following test cases.
     */
    @Before
    public void setUp() {
        manager = new SudokuBoardManager();
        moveTaken = findEditablePosition(manager.getBoard());
        manager.makeMove(moveTaken);
        int move = findEditablePosition(manager.getBoard());
        manager.updateValue(move, false);
        manager.undo();
        manager.setHint(5);
    }

    /**
     * This is a helper function for setUp. It helps to find a position in the sudoku board
     * that is editable.
     * @param board the board
     * @return a editable position
     */
    private int findEditablePosition(SudokuBoard board){
        int position = -1;
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(board.getCell(i, j).isEditable()){
                    position = i * 9 + j;
                    break;
                }
            }
        }
        return position;
    }

    /**
     * This test the functionality getHint() and setHint().
     */
    @Test
    public void getAndSetHint() {
        assertEquals(5, manager.getHint());
    }

    /**
     * This test the functionality reduceHint().
     */
    @Test
    public void reduceHint() {
        manager.reduceHint();
        assertEquals(4, manager.getHint());
    }

    /**
     * This test the functionality getBoard().
     */
    @Test
    public void getBoard() {
        assertNotNull(manager.getBoard());
    }

    /**
     * This test the functionality getTimeTaken() and setTimeTaken().
     */
    @Test
    public void getAndSetTimeTaken() {
        manager.setTimeTaken(6);
        assertEquals(6, manager.getTimeTaken());
    }

    /**
     * This test the functionality getCurrentCell() and setCurrentCell().
     */
    @Test
    public void setAndGetCurrentCell() {
        manager.setCurrentCell(new Cell(1, 2, 3));
        assertNotNull(manager.getCurrentCell());
    }

    /**
     * This test the functionality undoAvailable().
     */
    @Test
    public void undoAvailable() {
        assertFalse(manager.undoAvailable());
    }

    /**
     * This test the functionality gameFinished().
     */
    @Test
    public void boardSolved() {
        assertFalse(manager.boardSolved());
    }

    /**
     * This test the functionality isValidTap().
     */
    @Test
    public void isValidTap() {
        assertTrue(manager.isValidTap(moveTaken));
    }

}