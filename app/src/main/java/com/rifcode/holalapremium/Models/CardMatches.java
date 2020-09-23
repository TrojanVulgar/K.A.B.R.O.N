package com.rifcode.holalapremium.Models;


import java.util.ArrayList;

public class CardMatches {
    private String userId;
    private String username;
    private String photoProfile;
    private String age;
    private String gender;
    private String country;
    private String about;
    private String job;
    private String school;
    private String city;
    private ArrayList<Images> images;


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public CardMatches() {
    }

    public CardMatches(String userId,String username, String age, String gender,String job, String school, ArrayList<Images> images,String about,String city,String country) {
        this.username = username;
        this.age = age;
        this.gender = gender;
        this.job = job;
        this.school = school;
        this.images = images;
        this.about=about;
        this.userId=userId;
        this.city=city;
        this.country=country;
    }

    public CardMatches(String userId, String username, String photoProfile, String age, String gender, String country, String about, String job, String school, ArrayList<Images> images) {
        this.userId = userId;
        this.username = username;
        this.photoProfile = photoProfile;
        this.age = age;
        this.gender = gender;
        this.country = country;
        this.about = about;
        this.job = job;
        this.school = school;
        this.images = images;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public ArrayList<Images> getImages() {
        return images;
    }

    public void setImages(ArrayList<Images> images) {
        this.images = images;
    }


    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
