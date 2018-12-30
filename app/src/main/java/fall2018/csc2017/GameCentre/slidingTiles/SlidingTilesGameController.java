package fall2018.csc2017.GameCentre.slidingTiles;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import static android.graphics.Bitmap.createBitmap;

public class SlidingTilesGameController {
    /**
     * The Context of the activity.
     */
    private final Context context;

    /**
     * The database for game info.
     */
    private SQLDatabase db;

    /**
     * The user of the game.
     */
    private User user;

    /**
     * The file name of the game state file to be output.
     */
    private String gameStateFile, tempGameStateFile;

    /**
     * The number of steps which the user has taken.
     */
    private int steps;

    /**
     * The status of the game.
     */
    private boolean gameRunning;

    /**
     * The board's manager.
     */
    private SlidingTilesBoardManager boardManager;

    /**
     * The name of the game.
     */
    private static final String GAME_NAME = "SlidingTiles";

    /**
     * The list of buttons on the gridView.
     */
    private List<Button> tileButtons;

    /**
     * The background image of the game.
     */
    private Bitmap backgroundImage;

    /**
     * The formatted picture that will be used as the buttons' backgrounds.
     */
    private Bitmap[] tileImages;

    /**
     * The resources for the activity.
     */
    private Resources resources;

    /**
     * The name of the package.
     */
    private String packageName;

    /**
     * Constructor of SlidingTilesGameController class.
     */
    SlidingTilesGameController(Context context, User user) {
        this.context = context;
        this.db = new SQLDatabase(context);
        this.user = user;
        this.packageName = context.getApplicationContext().getPackageName();
        this.resources = context.getResources();
    }

    /**
     * Getter function for the file of the user in the database.
     */
    public String getUserFile() {
        return db.getUserFile(user.getUsername());
    }

    /**
     * Returns the user file of the current user of the game.
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns whether the user has successfully finished the task of the game.
     */
    boolean gameFinished() {
        return boardManager.boardSolved();
    }

    /**
     * The getter function of the Board's manager.
     */
    public SlidingTilesBoardManager getBoardManager() {
        return boardManager;
    }

    /**
     * Returns the board of the game.
     */
    public SlidingTilesBoard getBoard() {
        return boardManager.getBoard();
    }

    /**
     * Returns the steps that has been taken by the user.
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Setter function of the steps taken by the user.
     * Recorded data in the board's manager is also updated.
     */
    public void setSteps(int steps) {
        this.steps = steps;
        boardManager.setStepsTaken(steps);
    }

    /**
     * Setter function of the boardManager.
     */
    public void setBoardManager(SlidingTilesBoardManager boardManager) {
        this.boardManager = boardManager;
    }

    /**
     * Getter function of the name of the file of game state.
     */
    String getGameStateFile() {
        return gameStateFile;
    }

    /**
     * Getter function of the name of the temporary file of game state.
     */
    String getTempGameStateFile() {
        return tempGameStateFile;
    }

    /**
     * Getter function for tile buttons.
     */
    List<Button> getTileButtons() {
        return tileButtons;
    }

    /**
     * The setter function for gameRunning instance.
     */
    void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    /**
     * Return whether the game is running.
     */
    boolean isGameRunning() {
        return gameRunning;
    }

    /**
     * Set up the tile buttons of the game.
     */
    void createTileButtons() {
        tileButtons = new ArrayList<>();
        for (int row = 0; row != getBoard().getDifficulty(); row++) {
            for (int col = 0; col != getBoard().getDifficulty(); col++) {
                Button tmp = new Button(context);
                tileButtons.add(tmp);
            }
        }
    }

    /**
     * Update the tile buttons on the board.
     */
    void updateTileButtons() {
        SlidingTilesBoard board = getBoard();
        int nextPos = 0;
        for (Button b : tileButtons) {
            int row = nextPos / getBoard().getDifficulty();
            int col = nextPos % getBoard().getDifficulty();
            int tile_id = board.getTile(row, col);
            b.setBackground(new BitmapDrawable(context.getResources(), tileImages[tile_id - 1]));
            nextPos++;
        }
    }

    /**
     * Set up the file name for storing the game state.
     */
    void setupFile() {
        if (!db.dataExists(user.getUsername(), GAME_NAME))
            db.addData(user.getUsername(), GAME_NAME);
        gameStateFile = db.getDataFile(user.getUsername(), GAME_NAME);
        tempGameStateFile = "temp_" + gameStateFile;
    }

    /**
     * Set the steps taken recorded in the activity to the number
     * recorded by the board's manager.
     */
    void setupSteps() {
        this.steps = boardManager.getStepsTaken();
    }

    /**
     * Set up the tile images and background.
     */
    void setupTileImagesAndBackground() {
        tileImages = new Bitmap[boardManager.getDifficulty() * boardManager.getDifficulty()];
        try {
            byte[] tmpImage = boardManager.getImageBackground();
            backgroundImage = BitmapFactory.decodeByteArray(tmpImage, 0, tmpImage.length);
            imageConverter();
        } catch (Exception e) {
            integerConverter();
        }
    }

    /**
     * Convert the time into the format of HH:MM:SS
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
     * Convert image to multiple button-size images which could
     * be used and tile button backgrounds.
     */
    private void imageConverter() {
        int width = backgroundImage.getWidth();
        int height = backgroundImage.getHeight();
        int count = 0;
        for (int i = 0; i < boardManager.getDifficulty(); i++) {
            for (int j = 0; j < boardManager.getDifficulty(); j++) {
                tileImages[count++] = createBitmap(backgroundImage,
                        i * (width / boardManager.getDifficulty()),
                        j * (height / boardManager.getDifficulty()),
                        width / boardManager.getDifficulty(),
                        height / boardManager.getDifficulty(),
                        null,
                        false);
            }
        }
        tileImages[boardManager.getDifficulty() * boardManager.getDifficulty() - 1]
                = BitmapFactory.decodeResource(resources, R.drawable.tile_empty);
    }

    /**
     * Converts integer numbers to tile images.
     */
    private void integerConverter() {
        for (int i = 0; i < boardManager.getDifficulty() * boardManager.getDifficulty(); i++) {
            String name = "tile_" + Integer.toString(i + 1);
            int numImage = resources.getIdentifier(name, "drawable", packageName);
            tileImages[i] = BitmapFactory.decodeResource(resources, numImage);
        }
        tileImages[boardManager.getDifficulty() * boardManager.getDifficulty() - 1]
                = BitmapFactory.decodeResource(resources, R.drawable.tile_empty);
    }

    /**
     * Calculate and return the score of the user.
     */
    Integer calculateScore(Long totalTimeTaken) {
        int timeInSec = totalTimeTaken.intValue() / 1000;
        return 10000 / (steps + timeInSec);
    }

    /**
     * Update the user's score to the database.
     */
    boolean updateScore(int score) {
        boolean newRecord = user.updateScore(GAME_NAME, score);
        db.updateScore(user, GAME_NAME);
        return newRecord;
    }

    /**
     * Performs undo action.
     *
     * @return Whether undo action is successful.
     */
    boolean performUndo() {
        if (boardManager.undoAvailable()) {
            boardManager.move(boardManager.popUndo());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Load the saved Board Manager from fire base.
     */
    public void loadFromFile() {
        try {
            InputStream inputStream = context.openFileInput(tempGameStateFile);
            if (inputStream != null) {
                ObjectInputStream input = new ObjectInputStream(inputStream);
                boardManager = (SlidingTilesBoardManager) input.readObject();
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
}