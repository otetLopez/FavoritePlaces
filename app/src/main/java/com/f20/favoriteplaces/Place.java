package com.f20.favoriteplaces;

public class Place {
    private double lat;
    private double lng;
    private String addr;
    private String date;
    private boolean visited;

    public Place(double lat, double lng, String addr, String date, boolean visited) {
        this.lat = lat;
        this.lng = lng;
        this.addr = addr;
        this.date = date;
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
}
