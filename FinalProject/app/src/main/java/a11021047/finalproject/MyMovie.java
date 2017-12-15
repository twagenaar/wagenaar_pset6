/*
 * MyMovie
 * This class stores the information about a movie.
 * You can store the title, release date, poster URL and plot.
 */

package a11021047.finalproject;

/**
 * Created by Tessa on 13-12-2017.
 */

public class MyMovie {
    private String title;
    private String released;
    private String poster;
    private String plot;
    private String id;

    public MyMovie() {

    }

    /*
     * Create a movie object with the given ID, title, plot, release date and poster URL
     */
    public MyMovie(String ID, String aTitle, String aPlot, String releaseDate, String posterURL) {
        id = ID;
        title = aTitle;
        plot = aPlot;
        released = releaseDate;
        poster = posterURL;
    }

    /*
     * Return the title
     */
    public String getTitle() {
        return title;
    }

    /*
     * Return the release date
     */
    public String getReleaseDate() {
        return released;
    }

    /*
     * Return the poster URL
     */
    public String getPosterURL() {
        return poster;
    }

    /*
     * Return the plot
     */
    public String getPlot() {
        return plot;
    }

    /*
     * Return the ID
     */
    public String getId() {
        return id;
    }
}
