package fall2018.csc2017.GameCentre.pictureMatching;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import fall2018.csc2017.GameCentre.R;
import fall2018.csc2017.GameCentre.data.SQLDatabase;
import fall2018.csc2017.GameCentre.data.User;

import static android.content.Context.MODE_PRIVATE;

public class PictureMatchingGameController {
    /**
     * Context of MatchingBoardGameActivity
     */
    private final Context context;
    /**
     * Database that stores user file address and game state
     */
    private SQLDatabase db;
    /**
     * User object of current user
     */
    private User user;
    /**
     * File that saves serialized BoardManager Object
     */
    private String gameStateFile;
    /**
     * Game status, true if game is solved, false otherwise
     */
    private boolean gameRunning;
    /**
     * File that saves serialized BoardManager Object
     */
    private String tempGameStateFile;
    /**
     * current BoardManager
     */
    private MatchingBoardManager boardManager;
    /**
     * Name of current game
     */
    private static final String GAME_NAME = "PictureMatch";
    /**
     * A collection of buttons that is to be manipulated and displayed
     */
    private List<Button> tileButtons;
    /**
     * Resource of context (gameActivity)
     */
    private Resources resources;
    /**
     * The packageName
     */
    private String packageName;

    /**
     * Constructor of the controller class
     * @param context MatchingPictureGameActivity
     * @param user user object of current user
     */
    PictureMatchingGameController(Context context, User user){
        this.context = context;
        this.db = new SQLDatabase(context);
        this.user = user;
        this.packageName = context.getPackageName();
        this.resources = context.getResources();
    }

    /**
     *
     * @return list of tileButtons that's going to be displayed
     */
    public List<Button> getTileButtons() {
        return tileButtons;
    }

    /**
     * Used to determine whether the timer should keep counting
     * @return true of game is not ended (board is not solved), false otherwise
     */
    public boolean isGameRunning() {
        return gameRunning;
    }

    /**
     * set whether the game is running.
     * @param gameRunning state of game, whether game is still running
     */
    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    /**
     * set boardManager to a new boardManager.
     * @param boardManager the new boardManager that we want to assign to.
     */
    public void setBoardManager(MatchingBoardManager boardManager) {
        this.boardManager = boardManager;
    }

    /**
     * get the game state file
     * @return the string of game state file.
     */
    String getGameStateFile() {
        return gameStateFile;
    }

    /**
     * get the temporary game state file.
     * @return the the temporary game state file.
     */
    String getTempGameStateFile() {
        return tempGameStateFile;
    }

    /**
     * get boardManager
     * @return boardManager
     */
    public MatchingBoardManager getBoardManager() {
        return boardManager;
    }

    /**
     * get the board of the boardManager.
     * @return the board of the boardManager.
     */
    public MatchingBoard getBoard(){
        return boardManager.getBoard();
    }

    /**
     * get the User object that store the information of the current user.
     * @return the User object that represent the current user.
     */
    public User getUser() {
        return user;
    }

    /**
     * get the user file
     * @return the user file.
     */
    String getUserFile(){
        return db.getUserFile(user.getUsername());
    }

    /**
     * set up the game state file.
     */
    void setupFile(){
        if (!db.dataExists(user.getUsername(), GAME_NAME))
            db.addData(user.getUsername(), GAME_NAME);
        gameStateFile = db.getDataFile(user.getUsername(), GAME_NAME);
        tempGameStateFile = "temp_" + gameStateFile;
    }

    /**
     * create the tile buttons for displaying.
     */
    void createTileButtons(){
        tileButtons = new ArrayList<>();
        for (int row = 0; row != boardManager.getBoard().getDifficulty(); row++) {
            for (int col = 0; col != boardManager.getBoard().getDifficulty(); col++) {
                Button tmp = new Button(context);
                tmp.setBackgroundResource(R.drawable.picturematching_tile_back);
                tileButtons.add(tmp);
            }
        }
    }

    /**
     * update the tileButtons after make a move.
     */
    void updateTileButtons(){
        MatchingBoard board = boardManager.getBoard();
        int nextPos = 0;
        for (Button b : tileButtons) {
            int row = nextPos / boardManager.getDifficulty();
            int col = nextPos % boardManager.getDifficulty();
            PictureTile currentTile = board.getTile(row,col);
            switch (currentTile.getState()){
                case PictureTile.FLIP:
                    String name = "pm_" + boardManager.getTheme() + "_" + Integer.toString(currentTile.getId());
                    int id = resources.getIdentifier(name, "drawable", packageName);
                    b.setBackgroundResource(id);
                    break;
                case PictureTile.COVERED:
                    b.setBackgroundResource(R.drawable.picturematching_tile_back);
                    break;
                case PictureTile.SOLVED:
                    b.setBackgroundResource(R.drawable.picturematching_tile_done);
                    break;
            }
            nextPos++;
        }
    }

    /**
     * covert the time to proper format.
     * @param time the time that we want to convert.
     * @return the converted time.
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
     * calculate the score according to the totalTimeTaken.
     * @param totalTimeTaken the time for calculating score.
     * @return the calculated score.
     */
    Integer calculateScore(Long totalTimeTaken) {
        int timeInSec = totalTimeTaken.intValue() / 1000;
        return 10000 / (timeInSec);
    }

    /**
     * update the score in user object and database.
     * @param score the score that we wanted to store.
     * @return whether the score is updated.
     */
    boolean updateScore(int score){
        boolean newRecord = user.updateScore(GAME_NAME, score);
        db.updateScore(user, GAME_NAME);
        return newRecord;
    }

    /**
     * load the boardManager from the file.
     */
    public void loadFromFile() {
        try {
            InputStream inputStream = context.openFileInput(tempGameStateFile);
            if (inputStream != null) {
                ObjectInputStream input = new ObjectInputStream(inputStream);
                boardManager = (MatchingBoardManager) input.readObject();
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
     * whether the board is solved.
     * @return whether the board is solved,
     */
    public boolean boardSolved() {
        return boardManager.boardSolved();
    }
}
