package com.daisy.flappybird.domain;

public class Pipe {

    private float positionX;

    private float height;

    public Pipe(float positionX, float height) {
        this.positionX = positionX;
        this.height = height;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getHeight() {
        return height;
    }

}
