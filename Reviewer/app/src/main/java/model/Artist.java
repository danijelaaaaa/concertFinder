package model;

/**
 * Created by Danijela on 8/26/2016.
 */
public class Artist {


    private Number id;
    private String name;
    private String mbid; //music brainz id or null if not available
    private String image_url;
    private String thumb_url;
    private Number concert_id;

    public Artist() {
    }


    public Artist(String name, String mbid, String image_url, String thumb_url) {
        this.name = name;
        this.mbid = mbid;
        this.image_url = image_url;
        this.thumb_url = thumb_url;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public Number getConcert_id() {
        return concert_id;
    }

    public void setConcert_id(Number concert_id) {
        this.concert_id = concert_id;
    }


    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mbid='" + mbid + '\'' +
                ", image_url='" + image_url + '\'' +
                ", thumb_url='" + thumb_url + '\'' +
                ", concert_id=" + concert_id +
                '}';
    }
}
