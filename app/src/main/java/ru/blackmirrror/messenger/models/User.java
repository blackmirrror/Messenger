package ru.blackmirrror.messenger.models;

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String link;
    private String phoneNumber;
    private String status;
    private String photoUrl;
    //private boolean isOnline;

    public User(){}

    public User(String firstName, String lastName, String link, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.link = link;
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /*public boolean isOnline() { return isOnline; }

    public void setOnline(boolean online) { isOnline = online; }*/

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getPhotoUrl() { return photoUrl; }

    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
