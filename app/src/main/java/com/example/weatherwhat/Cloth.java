package com.example.weatherwhat;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Cloth {
    public String uid;
    public String author;
    public String name;
    public String info;
    public String imgUrl;
    public String category;
    public String location;

    public Cloth() {
        // Default constructor required for calls to DataSnapshot.getValue(Cloth.class)
    }

    public Cloth(String uid, String author, String category, String name, String info, String imgUrl, String location) {
        this.uid = uid;
        this.author = author;
        this.name = name;
        this.info = info;
        this.imgUrl = imgUrl;
        this.category = category;
        this.location = location;
    }

    // [START cloth_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("name", name);
        result.put("category", category);
        result.put("info", info);
        result.put("imgUrl", imgUrl);
        result.put("location", location);

        return result;
    }
    // [END cloth_to_map]
}
