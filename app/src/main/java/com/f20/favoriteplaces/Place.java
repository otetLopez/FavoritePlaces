package com.f20.favoriteplaces;

public class Place {
    private int id;
    private String addr;
    private String date;
    private boolean visited;
    private double lat;
    private double lng;

    public Place(int id, String addr, String date, boolean visited, double lat, double lng) {
        this.id = id;
        this.addr = addr;
        this.date = date;
        this.visited = visited;
        this.lat = lat;
        this.lng = lng;
    }

    public Place(String addr, String date, boolean visited, double lat, double lng) {
        this.id = 0;
        this.addr = addr;
        this.date = date;
        this.visited = visited;
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
}
