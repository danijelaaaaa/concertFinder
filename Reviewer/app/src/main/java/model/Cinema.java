package model;

/**
 * Created by milossimic on 3/21/16.
 */
public class Cinema {
    private long id;
    private String name;
    private String description;
    private String avatar;

    public Cinema(){

    }

    public Cinema(String name, String description, String avatar) {

        this.name = name;
        this.description = description;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    @Override
    public String toString() {
        return  "name: " + name + "\n"  +
                "description: " + description + "\n" ;
    }
}
