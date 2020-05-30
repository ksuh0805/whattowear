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

    public Cloth() {
        // Default constructor required for calls to DataSnapshot.getValue(Cloth.class)
    }

    public Cloth(String uid, String author, String category, String name, String info, String imgUrl) {
        this.uid = uid;
        this.author = author;
        this.name = name;
        this.info = info;
        this.imgUrl = imgUrl;
        this.category = category;
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

        return result;
    }
    // [END cloth_to_map]
}
