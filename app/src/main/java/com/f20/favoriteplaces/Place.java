package com.f20.favoriteplaces;

import java.io.Serializable;

public class Place implements Serializable {
    private int id;
    private String addr;
    private String date;
    private boolean visited;
    private double user_lat;
    private double user_lng;
    private double lat;
    private double lng;


    public Place() {
        this.id = 0;
        this.addr = "";
        this.date = "";
        this.visited = false;
        this.user_lat = 0;
        this.user_lng = 0;
        this.lat = 0;
        this.lng = 0;
    }

    public Place(int id, String addr, String date, boolean visited, double user_lat, double user_lng, double lat, double lng) {
        this.id = id;
        this.addr = addr;
        this.date = date;
        this.visited = visited;
        this.user_lat = user_lat;
        this.user_lng = user_lng;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }


    public double getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(double user_lat) {
        this.user_lat = user_lat;
    }

    public double getUser_lng() {
        return user_lng;
    }

    public void setUser_lng(double user_lng) {
        this.user_lng = user_lng;
    }

}
