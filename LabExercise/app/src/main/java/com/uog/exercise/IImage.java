package com.uog.exercise;

public class IImage {
    public static final String IMAGE_TYPE_URL ="url";
    public static final String IMAGE_TYPE_FILE ="file";
    private int id;
    private String path;
    private String type;
    private Double latitude;
    private Double longitude;

    public IImage() {
    }

    public IImage(String path, String type, Double latitude, Double longitude) {
        this.path = path;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public IImage(int id, String path, String type, Double latitude, Double longitude) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public IImage(int id, String path, String type) {
        this.id = id;
        this.path = path;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
