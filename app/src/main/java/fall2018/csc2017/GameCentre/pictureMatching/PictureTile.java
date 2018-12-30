package fall2018.csc2017.GameCentre.pictureMatching;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * A Tile in a sliding tiles puzzle.
 */
public class PictureTile implements Comparable<PictureTile>, Serializable {

    /**
     * The unique id.
     */
    private int id;
    /**
     * The solved state of pictureTile
     */
    static final String SOLVED = "solved";
    /**
     * The flip state of pictureTile.
     */
    static final String FLIP = "flip";
    /**
     * The covered state of pictureTile.
     */
    static final String COVERED = "covered";
    /**
     * the state of pictureTile
     */
    private String state;

    /**
     * Return the tile id.
     *
     * @return the tile id
     */
    public int getId() {
        return id;
    }

    /**
     * get the state of the current tile.
     *
     * @return the state of the current tile.
     */
    String getState() {
        return state;
    }

    /**
     * set the state.
     *
     * @param state the new state for this tile.
     */
    void setState(String state) {
        this.state = state;
    }

    /**
     * A tile with a background id; look up and set the id.
     *
     * @param id the id of pictureTile.
     */
    PictureTile(int id) {
        this.id = id;
        this.state = COVERED;
    }

    @Override
    public int compareTo(@NonNull PictureTile tile) {
        return tile.id - this.id;
    }
}
