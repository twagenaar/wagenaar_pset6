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

    public MyMovie(String ID, String aTitle, String aPlot, String releaseDate, String posterURL) {
        id = ID;
        title = aTitle;
        plot = aPlot;
        released = releaseDate;
        poster = posterURL;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return released;
    }

    public String getPosterURL() {
        return poster;
    }

    public String getPlot() {
        return plot;
    }

    public String getId() {
        return id;
    }
}
