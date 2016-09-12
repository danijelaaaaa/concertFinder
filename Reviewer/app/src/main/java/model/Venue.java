package model;

/**
 * Created by Danijela on 8/26/2016.
 */
public class Venue {

    private String display_name;
    private String name;
    private String city;
    private String region;
    private String country;
    private Number latitude;
    private Number longitude;


    public Venue() {
    }


    public Venue(String display_name, String name, String city, String region, String country, Number latitude, Number longitude) {
        this.display_name = display_name;
        this.name = name;
        this.city = city;
        this.region = region;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Number getLatitude() {
        return latitude;
    }

    public void setLatitude(Number latitude) {
        this.latitude = latitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Number getLongitude() {
        return longitude;
    }

    public void setLongitude(Number longitude) {
        this.longitude = longitude;
    }





}
