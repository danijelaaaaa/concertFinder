package model;

/**
 * Created by Danijela on 9/11/2016.
 */
public class Track {

    private Number id;
    private String title;
    private String ytid;


    public Track() {
    }

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYtid() {
        return ytid;
    }

    public void setYtid(String ytid) {
        this.ytid = ytid;
    }


    @Override
    public String toString() {
        return "Track{" +
                ", title='" + title + '\'' +
                ", ytid='" + ytid + '\'' +
                '}';
    }
}
