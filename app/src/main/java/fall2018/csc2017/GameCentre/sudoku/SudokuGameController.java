package fall2018.csc2017.GameCentre.sudoku;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.List;

import fall2018.csc2017.GameCentre.data.SQLDatabase;
import fall2018.csc2017.GameCentre.data.User;

import static android.content.Context.MODE_PRIVATE;

public class SudokuGameController {
    /**
     * The context.
     */
    private final Context context;
    /**
     * The database.
     */
    private SQLDatabase db;
    /**
     * The user.
     */
    private User user;
    /**
     * The gameStateFile.
     */
    private String gameStateFile;
    /**
     * The gameRunning.
     */
    private boolean gameRunning;
    /**
     * The tempGameStateFile.
     */
    private String tempGameStateFile;
    /**
     * The SudokuBoardManager.
     */
    private SudokuBoardManager boardManager;
    /**
     * The game name.
     */
    private static final String GAME_NAME = "Sudoku";
    /**
     * The list of cell buttons.
     */
    private List<Button> cellButtons;


    /**
     * Constructor for SudokuGameController.
     *
     * @param context
     * @param user
     */
    SudokuGameController(Context context, User user){
        this.context = context;
        this.db = new SQLDatabase(context);
        this.user = user;
    }

    /**
     * Return list of buttons.
     *
     * @return list of buttons
     */
    public List<Button> getCellButtons() {
        return cellButtons;
    }

    /**
     * Return the user's file
     *
     * @return user's file
     */
    public String getUserFile(){
        return db.getUserFile(user.getUsername());
    }

    /**
     * Return whether game is still running.
     *
     * @return whether game is still running
     */
    public boolean isGameRunning() {
        return gameRunning;
    }

    /**
     * Set gameRunning.
     *
     * @param gameRunning
     */
    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    /**
     * Set boardManager.
     *
     * @param boardManager
     */
    public void setBoardManager(SudokuBoardManager boardManager) {
        this.boardManager = boardManager;
    }

    /**
     * Return gameStateFile.
     *
     * @return gameStateFile
     */
    public String getGameStateFile() {
        return gameStateFile;
    }

    /**
     * Return tempGameStateFile.
     *
     * @return tempGameStateFile
     */
    public String getTempGameStateFile() {
        return tempGameStateFile;
    }

    /**
     * Return boardManager.
     *
     * @return boardManager
     */
    public SudokuBoardManager getBoardManager() {
        return boardManager;
    }

    /**
     * Return SudokuBoard.
     *
     * @return SudokuBoard
     */
    public SudokuBoard getBoard(){
        return boardManager.getBoard();
    }

    /**
     * Return user.
     *
     * @return user
     */
    public User getUser() {
        return user;
    }

    /**
     * Set up file.
     */
    void setupFile(){
        if (!db.dataExists(user.getUsername(), GAME_NAME))
            db.addData(user.getUsername(), GAME_NAME);
        gameStateFile = db.getDataFile(user.getUsername(), GAME_NAME);
        tempGameStateFile = "temp_" + gameStateFile;
    }

    /**
     * Create cell buttons.
     */
    void createCellButton(){
        SudokuBoard board = boardManager.getBoard();
        cellButtons = new ArrayList<>();
        for (int row = 0; row != 9; row++) {
            for (int col = 0; col != 9; col++) {
                Button tmp = new Button(context);
                tmp.setBackgroundResource(board.getCell(row, col).getBackground());
                this.cellButtons.add(tmp);
            }
        }
    }

    /**
     * Update cell buttons.
     */
    void updateCellButtons(){
        SudokuBoard board = boardManager.getBoard();
        int nextPos = 0;
        for (Button b : cellButtons) {
            Cell cell = board.getCell(nextPos / 9, nextPos % 9);
            if (cell.getFaceValue() == 0) {
                b.setText("");
            } else {
                b.setText(String.format("%s", cell.getFaceValue().toString()));
            }
            b.setBackgroundResource(cell.getBackground());
            nextPos++;
        }
    }

    /**
     * Return time in String format.
     *
     * @param time
     * @return string format of time
     */
    String convertTime(long time) {
        Integer hour = (int) (time / 3600000);
        Integer min = (int) ((time % 3600000) / 60000);
        Integer sec = (int) ((time % 3600000 % 60000) / 1000);
        String hourStr = hour.toString();
        String minStr = min.toString();
        String secStr = sec.toString();
        if (hour < 10) {
            hourStr = "0" + hourStr;
        }
        if (min < 10) {
            minStr = "0" + minStr;
        }
        if (sec < 10) {
            secStr = "0" + secStr;
        }
        return hourStr + ":" + minStr + ":" + secStr;
    }

    /**
     * Return current score.
     *
     * @param totalTimeTaken
     * @return score
     */
    Integer calculateScore(Long totalTimeTaken) {
        int timeInSec = totalTimeTaken.intValue() / 1000;
        return 10000 / (timeInSec);
    }

    /**
     * Return whether update score succeed.
     *
     * @param score
     * @return whether succeed
     */
    boolean updateScore(int score){
        boolean newRecord = user.updateScore(GAME_NAME, score);
        db.updateScore(user, GAME_NAME);
        return newRecord;
    }

    /**
     * Load boardManager from file.
     */
    public void loadFromFile() {
        try {
            InputStream inputStream = context.openFileInput(tempGameStateFile);
            if (inputStream != null) {
                ObjectInputStream input = new ObjectInputStream(inputStream);
                boardManager = (SudokuBoardManager) input.readObject();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        } catch (ClassNotFoundException e) {
            Log.e("login activity", "File contained unexpected data type: " + e.toString());
        }
    }

    /**
     * Save the board manager to fileName.
     *
     * @param fileName the name of the file
     */
    public void saveToFile(String fileName) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput(fileName, MODE_PRIVATE));
            if (fileName.equals(db.getUserFile(user.getUsername()))) {
                outputStream.writeObject(user);
            } else if (fileName.equals(gameStateFile) || fileName.equals(tempGameStateFile)) {
                outputStream.writeObject(boardManager);
            }
            outputStream.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Return whether board is solved.
     *
     * @return whether board is solved
     */
    public boolean boardSolved() {
        return boardManager.boardSolved();
    }
}
