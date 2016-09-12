package model;

import java.util.ArrayList;

/**
 * Created by Danijela on 8/25/2016.
 */
public class Concert {

    private Number id;
    private Number apiID;
    private String title; //title
    private String datetime; //datetime of the event expressed in ISO 8601 format with no timezone.YYYY-MM-DDThh:mm:ss
    private String formatted_datetime; //readable datetime string for display purposes
    private String formatted_location; //readable location string for display purposes -City, State (within US) City, Country (outside US)
    private String facebook_rsvp_url; //link to RSVP to the event
    private ArrayList<Artist> artists; //	array of artists performing at the event
    private String description;
    private String avatar;

    private String venue_display_name;
    private String venue_name;
    private String city;
    private String region;
    private String country;
    private Number latitude;
    private Number longitude;



    //..

    public Concert(){

    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Number getApiID() {
        return apiID;
    }

    public void setApiID(Number id) {
        this.apiID = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getFormatted_datetime() {
        return formatted_datetime;
    }

    public void setFormatted_datetime(String formatted_datetime) {
        this.formatted_datetime = formatted_datetime;
    }

    public String getFormatted_location() {
        return formatted_location;
    }

    public void setFormatted_location(String formatted_location) {
        this.formatted_location = formatted_location;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
    }

    public String getFacebook_rsvp_url() {
        return facebook_rsvp_url;
    }

    public void setFacebook_rsvp_url(String facebook_rsvp_url) {
        this.facebook_rsvp_url = facebook_rsvp_url;
    }

    public String getVenue_display_name() {
        return venue_display_name;
    }

    public void setVenue_display_name(String venue_display_name) {
        this.venue_display_name = venue_display_name;
    }

    public String getVenue_name() {
        return venue_name;
    }

    public void setVenue_name(String venue_name) {
        this.venue_name = venue_name;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Number getLatitude() {
        return latitude;
    }

    public void setLatitude(Number latitude) {
        this.latitude = latitude;
    }

    public Number getLongitude() {
        return longitude;
    }

    public void setLongitude(Number longitude) {
        this.longitude = longitude;
    }

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public Concert(Number id, Number longitude, Number latitude, String country, String region, String city,
                   String venue_name, String venue_display_name, String avatar, String description,
                   ArrayList<Artist> artists, String facebook_rsvp_url, String formatted_location,
                   String formatted_datetime, String datetime, String title) {
        this.apiID = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
        this.region = region;
        this.city = city;
        this.venue_name = venue_name;
        this.venue_display_name = venue_display_name;
        this.avatar = avatar;
        this.description = description;
        this.artists = artists;
        this.facebook_rsvp_url = facebook_rsvp_url;
        this.formatted_location = formatted_location;
        this.formatted_datetime = formatted_datetime;
        this.datetime = datetime;
        this.title = title;
    }


    @Override
    public String toString() {
        return "Concert{" +
                "appId=" + apiID +
                ", title='" + title + '\'' +
                ", datetime='" + datetime + '\'' +
                ", formatted_datetime='" + formatted_datetime + '\'' +
                ", formatted_location='" + formatted_location + '\'' +
                ", facebook_rsvp_url='" + facebook_rsvp_url + '\'' +
                ", artists=" + artists +
                ", description='" + description + '\'' +
                ", avatar='" + avatar + '\'' +
                ", venue_display_name='" + venue_display_name + '\'' +
                ", venue_name='" + venue_name + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
