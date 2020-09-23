package com.rifcode.holalapremium.Models;

import java.util.ArrayList;

public class Users {


    private ArrayList<Images> images;

    public Users() {
    }

    public Users(ArrayList<Images> images) {
        this.images = images;
    }

    public ArrayList<Images> getImages() {
        return images;
    }

    public void setImages(ArrayList<Images> images) {
        this.images = images;
    }


}
