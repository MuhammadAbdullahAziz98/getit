package com.example.lenovo.getit;

import java.io.Serializable;

public class Rate implements Serializable {
    String id;
    String name;
    int rated;

    Rate(){}

    public Rate(String id, String name, int rated) {
        this.id = id;
        this.name = name;
        this.rated = rated;
    }

    public String getId() {
        return id;
    }

    public int getRated() {
        return rated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRated(int rated) {
        this.rated = rated;
    }

    public String toString(){
        return name + ">> " + Integer.toString(rated);
    }
}
