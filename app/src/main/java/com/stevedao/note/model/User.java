package com.stevedao.note.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thanh.dao on 8/6/2016.
 *
 */
public class User extends Entity{
    // Corresponding field in firebase database
    public static final String ROOT = "users";
    public static final String EMAIL = "email";
    public static final String DISPLAY_NAME = "display_name";
    public static final String PHOTO_URL = "photo_url";

    private String email;
    private String displayName;
    private String photoUrl;

    public User() {

    }

    public User(String email, String displayName, String photoUrl) {
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }

    public User(String id, String email, String displayName, String photoUrl) {
        super(id);
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(EMAIL, email);
        result.put(DISPLAY_NAME, displayName);
        result.put(PHOTO_URL, photoUrl);

        return result;
    }
}
