package fall2018.csc2017.GameCentre.data;

;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Please Wipe data on the emulator before using this unit test, memory could result in error;
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseUnitTest {

    @Test
    public void userExists() {
        Context context = InstrumentationRegistry.getTargetContext();
        SQLDatabase db = new SQLDatabase(context);
        User user = new User("admin", "admin");
        db.addUser(user);
        assertTrue(db.userExists("admin"));
        assertFalse(db.userExists("admin1"));
        db.close();
    }

    @Test
    public void dataExists() {
        Context context = InstrumentationRegistry.getTargetContext();
        SQLDatabase db = new SQLDatabase(context);
        db.addData("admin", "game");
        assertTrue(db.dataExists("admin", "game"));
        assertFalse(db.dataExists("admin1", "game"));
        assertFalse(db.dataExists("admin", "game1"));
        db.close();
    }

    @Test
    public void addUser() {
        Context context = InstrumentationRegistry.getTargetContext();
        SQLDatabase db = new SQLDatabase(context);
        User user = new User("admin", "admin");
        db.addUser(user);
        assertTrue(db.userExists("admin"));
        assertFalse(db.userExists("admin1"));
        db.close();
    }

    @Test
    public void addData() {
        Context context = InstrumentationRegistry.getTargetContext();
        SQLDatabase db = new SQLDatabase(context);
        db.addData("admin", "game");
        assertTrue(db.dataExists("admin", "game"));
        assertFalse(db.dataExists("admin1", "game"));
        assertFalse(db.dataExists("admin", "game1"));
        db.close();
    }

    @Test
    public void getUserFile() {
        Context context = InstrumentationRegistry.getTargetContext();
        SQLDatabase db = new SQLDatabase(context);
        User user = new User("admin", "admin");
        db.addUser(user);
        String output = db.getUserFile("admin");
        assertEquals("admin_user.ser", output);
        db.close();
    }

    @Test
    public void getScore() {
        Context context = InstrumentationRegistry.getTargetContext();
        SQLDatabase db = new SQLDatabase(context);
        db.addData("user", "game");
        assertEquals(0, db.getScore("user", "game"));
        assertEquals(-1, db.getScore("user1", "game"));
        db.close();
    }

    @Test
    public void getDataFile() {
        Context context = InstrumentationRegistry.getTargetContext();
        SQLDatabase db = new SQLDatabase(context);
        db.addData("user", "game");
        assertEquals("user_game_data.ser", db.getDataFile("user", "game"));
        assertEquals("File Does Not Exist!", db.getDataFile("user1", "game"));
        db.close();
    }

    @Test
    public void updateScore() {
        Context context = InstrumentationRegistry.getTargetContext();
        SQLDatabase db = new SQLDatabase(context);
        User user = new User("admin", "pass");
        if (!db.dataExists("admin", "game"))
            db.addData("admin", "game");
        db.updateScore(user, "game");
        assertEquals(-1, db.getScore("admin", "game"));
        user.updateScore("game", 80);
        db.updateScore(user, "game");
        assertEquals(80, db.getScore("admin", "game"));
        db.close();

    }

    @Test
    public void getScoreByGame() {
        Context context = InstrumentationRegistry.getTargetContext();
        SQLDatabase db = new SQLDatabase(context);
        String[] user = {"Apple", "Banana", "Orange"};
        int[] score = {100, 50, 10};
        User Orange = new User("Orange", "pass");
        User Apple = new User("Apple", "pass");
        User Banana = new User("Banana", "pass");
        db.addUser(Orange);
        db.addUser(Apple);
        db.addUser(Banana);
        Orange.updateScore("game1", 10);
        Apple.updateScore("game1", 100);
        Banana.updateScore("game1", 50);
        db.addData("Orange", "game1");
        db.addData("Apple", "game1");
        db.addData("Banana", "game1");
        db.updateScore(Orange, "game1");
        db.updateScore(Apple, "game1");
        db.updateScore(Banana, "game1");
        ArrayList<ArrayList<String>> data = db.getScoreByGame("game1");
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).size(); j++) {
                assertEquals(data.get(i).get(0), String.valueOf(i + 1));
                assertEquals(data.get(i).get(2), user[i]);
                assertEquals(data.get(i).get(3), String.valueOf(score[i]));
            }
        }

    }
}
