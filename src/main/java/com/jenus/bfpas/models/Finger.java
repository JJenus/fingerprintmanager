package com.jenus.bfpas.models;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class Finger {
    private final String name;
    private Image Image;
    private byte[] imageBytes;

    Finger(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return Image;
    }

    private Image getFromRawImage() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        return new Image(inputStream);
    }

    public void setImage(Image image) {
        Image = image;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
    private void setImageBytes(String base64String) {
        this.imageBytes = Base64.getDecoder().decode(base64String);
    }

    private String getImageString() {
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
