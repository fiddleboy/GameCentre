package fall2018.csc2017.GameCentre.pictureMatching;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import fall2018.csc2017.GameCentre.data.User;
import fall2018.csc2017.GameCentre.R;
import fall2018.csc2017.GameCentre.util.CustomAdapter;
import fall2018.csc2017.GameCentre.util.GestureDetectGridView;
import fall2018.csc2017.GameCentre.util.popScore;

public class PictureMatchingGameActivity extends AppCompatActivity implements Observer {
    /**
     * TextView for displaying time.
     */
    private TextView timeDisplay;
    /**
     * Grid View and calculated column height and width based on device size
     */
    private GestureDetectGridView gridView;
    /**
     * column width and height of each row and column of gridView
     */
    private static int columnWidth, columnHeight;
    /**
     * The name of the current game.
     */
    private static final String GAME_NAME = "PictureMatch";
    /**
     * Current User.
     */

    private LocalTime startingTime;
    /**
     * the total time
     */
    private Long totalTimeTaken;
    /**
     * Controller object for this activity
     */
    private PictureMatchingGameController controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startingTime = LocalTime.now();
        setupController();
        controller.loadFromFile();
        controller.createTileButtons();
        setContentView(R.layout.activity_picturematching_game);
        setupTime();
        addGridViewToActivity();
    }


    /**
     * Create and setup controller
     */
    private void setupController() {
        controller = new PictureMatchingGameController(this, (User)getIntent().getSerializableExtra("user"));
        controller.setupFile();
    }


    /**
     * Time counting, setup initial time based on the record in boardmanager
     */
    private void setupTime() {
        if (!controller.boardSolved())
            controller.setGameRunning(true);
        Timer timer = new Timer();
        final Long preStartTime = controller.getBoardManager().getTimeTaken();
        timeDisplay = findViewById(R.id.time_display_view_in_picturematching);
        totalTimeTaken = preStartTime;
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                long time = Duration.between(startingTime, LocalTime.now()).toMillis();
                if(controller.isGameRunning()){
                    totalTimeTaken = time + preStartTime;
                    timeDisplay.setText(controller.convertTime(totalTimeTaken));
                    controller.getBoardManager().setTimeTaken(totalTimeTaken);
                }
            }
        };
        timer.schedule(task2, 0, 1000);
    }


    /**
     * Setup the gridview where the tiles are located
     */
    private void addGridViewToActivity() {
        gridView = findViewById(R.id.PictureMatchingGrid);
        gridView.setNumColumns(controller.getBoardManager().getDifficulty());
        gridView.setBoardManager(controller.getBoardManager());
        controller.getBoard().addObserver(this);
        // Observer sets up desired dimensions as well as calls our display function
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int displayWidth = gridView.getMeasuredWidth();
                        int displayHeight = gridView.getMeasuredHeight();
                        columnWidth = (displayWidth / controller.getBoardManager().getDifficulty());
                        columnHeight = (displayHeight / controller.getBoardManager().getDifficulty());
                        display();
                    }
                });
    }

    /**
     * Set up the background image for each button based on the master list
     * of positions, and then call the adapter to set the view.
     */
    public void display() {
        controller.updateTileButtons();
        gridView.setAdapter(new CustomAdapter(controller.getTileButtons(), columnWidth, columnHeight));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String text = "Time: " + controller.convertTime(controller.getBoardManager().getTimeTaken());
        timeDisplay.setText(text);
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        controller.saveToFile(controller.getTempGameStateFile());
        controller.saveToFile(controller.getGameStateFile());
    }


    @Override
    public void update(Observable o, Object arg) {
        setupTimeDelay();
        display();
        if (controller.boardSolved()) {
            Toast.makeText(PictureMatchingGameActivity.this, "YOU WIN!", Toast.LENGTH_SHORT).show();
            Integer score = controller.calculateScore(totalTimeTaken);
            boolean newRecord = controller.updateScore(score);
            controller.saveToFile(controller.getUserFile());
            controller.setGameRunning(false);
            popScoreWindow(score, newRecord);
        }
    }

    /**
     * A time delay of 0.5 second for the pictures to turn over
     */
    private void setupTimeDelay() {
        if (controller.getBoardManager().check2tiles()) {
            final android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (controller.getBoardManager().check2tiles())
                            controller.getBoardManager().getBoard().solveTile();
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "slow down!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 500);
        }
    }


    /**
     * Pop up window that shows user the score he/she gets
     * @param score Score that is to be displayed on popup window
     * @param newRecord Indicator that determines which text is to be displayed (New Record: or
     *                  Your Highest Score Was
     */
    private void popScoreWindow(Integer score, boolean newRecord) {
        Intent goToPopWindow = new Intent(getApplication(), popScore.class);
        goToPopWindow.putExtra("score", score);
        goToPopWindow.putExtra("user", controller.getUser());
        goToPopWindow.putExtra("gameType", GAME_NAME);
        goToPopWindow.putExtra("newRecord", newRecord);
        startActivity(goToPopWindow);
    }

}
