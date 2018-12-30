package fall2018.csc2017.GameCentre.slidingTiles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
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

/**
 * The game activity.
 */
public class SlidingTilesGameActivity extends AppCompatActivity implements Observer {

    /**
     * Controller of the game.
     */
    private SlidingTilesGameController logicalController;

    /**
     * The time and steps user has taken to be displayed on the user interface.
     */
    private TextView timeDisplay, displayStep;

    /**
     * Grid View for the game.
     */
    private GestureDetectGridView gridView;

    /**
     * Calculated column height and width based on device size.
     */
    private static int columnWidth, columnHeight;

    /**
     * The name of the game.
     */
    private static final String GAME_NAME = "SlidingTiles";

    /**
     * The time user started to play the game.
     */
    private LocalTime startingTime;

    /**
     * The time user took to play the game in total.
     */
    private Long totalTimeTaken;

    /**
     * Warning message
     */
    private TextView warning;

    /**
     * Dispatch onCreate() to fragments.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startingTime = LocalTime.now();
        setupController();
        logicalController.loadFromFile();
        logicalController.createTileButtons();
        setContentView(R.layout.activity_main);
        setupTime();
        setUpStep();
        addGridViewToActivity();
        addUndoButtonListener();
        addWarningTextViewListener();
        addStepDisplayListener();
        logicalController.setupTileImagesAndBackground();
    }

    /**
     * Dispatch onResume() to fragments.
     */
    @Override
    protected void onResume() {
        super.onResume();
        displayStep.setText(String.format("%s", "Steps: " + Integer.toString(logicalController.getSteps())));
        timeDisplay.setText(logicalController.convertTime(logicalController.getBoardManager().getTimeTaken()));
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        logicalController.saveToFile(logicalController.getTempGameStateFile());
        logicalController.saveToFile(logicalController.getGameStateFile());
    }

    /**
     * Set up the background image for each button based on the master list
     * of positions, and then call the adapter to set the view.
     */
    public void display() {
        logicalController.updateTileButtons();
        gridView.setAdapter(new CustomAdapter(logicalController.getTileButtons(), columnWidth, columnHeight));
    }

    /**
     * Set up the logicalController for the game.
     */
    private void setupController() {
        logicalController = new SlidingTilesGameController(this,
                (User) getIntent().getSerializableExtra("user"));
        logicalController.setupFile();
    }

    /**
     * Setup the initial step base on the record in the Board's manager.
     */
    private void setUpStep() {
        displayStep = findViewById(R.id.stepDisplayTextView);
        logicalController.setupSteps();
        displayStep.setText(String.format("%s", "Steps: " + Integer.toString(logicalController.getSteps())));
    }

    /**
     * Setup the initial time based on the record in the board's manager.
     */
    private void setupTime() {
        if (!logicalController.gameFinished())
            logicalController.setGameRunning(true);
        Timer timer = new Timer();
        final long preStartTime = logicalController.getBoardManager().getTimeTaken();
        totalTimeTaken = preStartTime;
        timeDisplay = findViewById(R.id.time_display_view);
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                long time = Duration.between(startingTime, LocalTime.now()).toMillis();
                if (logicalController.isGameRunning()) {
                    totalTimeTaken = time + preStartTime;
                    timeDisplay.setText(logicalController.convertTime(totalTimeTaken));
                    logicalController.getBoardManager().setTimeTaken(totalTimeTaken);
                }
            }
        };
        timer.schedule(task2, 0, 1000);
    }

    /**
     * Set up the warning message displayed on the UI.
     */
    private void addWarningTextViewListener() {
        warning = findViewById(R.id.warningTextView);
        warning.setVisibility(View.INVISIBLE);
    }

    /**
     * Set up the step display textView
     */
    @SuppressLint("SetTextI18n")
    private void addStepDisplayListener() {
        displayStep = findViewById(R.id.stepDisplayTextView);
        displayStep.setText("Step: 0");
    }

    /**
     * Setup the GridView where the tiles are located
     */
    private void addGridViewToActivity() {
        gridView = findViewById(R.id.grid);
        gridView.setNumColumns(logicalController.getBoard().getDifficulty());
        gridView.setBoardManager(logicalController.getBoardManager());
        logicalController.getBoard().addObserver(this);
        // Observer sets up desired dimensions as well as calls our display function
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        columnWidth = (gridView.getMeasuredWidth() /
                                logicalController.getBoard().getDifficulty());
                        columnHeight = (gridView.getMeasuredHeight() /
                                logicalController.getBoard().getDifficulty());
                        display();
                    }
                });
    }

    /**
     * Set up the performUndo button.
     */
    private void addUndoButtonListener() {
        Button undoButton = findViewById(R.id.undo_button);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (!logicalController.performUndo()) {
                    warning.setText("Exceeds Undo-Limit!");
                    warning.setVisibility(View.VISIBLE);
                    warning.setError("Exceeds Undo-Limit! ");
                    displayStep.setVisibility(View.INVISIBLE);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            warning.setVisibility(View.INVISIBLE);
                            displayStep.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                }
            }
        });
    }

    private void popScoreWindow(Integer score, boolean newRecord) {
        Intent goToPopWindow = new Intent(getApplication(), popScore.class);
        goToPopWindow.putExtra("score", score);
        goToPopWindow.putExtra("user", logicalController.getUser());
        goToPopWindow.putExtra("gameType", GAME_NAME);
        goToPopWindow.putExtra("newRecord", newRecord);
        startActivity(goToPopWindow);
    }

    /**
     * Update the game activity when the observable objects notify the Activity
     * change(s) has/have been made.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void update(Observable o, Object arg) {
        display();
        logicalController.setSteps(logicalController.getSteps() + 1);
        displayStep.setText("Steps: " + Integer.toString(logicalController.getSteps()));
        if (logicalController.gameFinished()) {
            Toast.makeText(this, "YOU WIN!", Toast.LENGTH_SHORT).show();
            Integer score = logicalController.calculateScore(totalTimeTaken);
            boolean newRecord = logicalController.updateScore(score);
            logicalController.saveToFile(logicalController.getUserFile());
            logicalController.setGameRunning(false);
            popScoreWindow(score, newRecord);
        }
    }
}
