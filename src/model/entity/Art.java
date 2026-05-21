package model.entity;

import java.time.LocalDateTime;

public class Art extends Item {

    private String artist;
    private String style;

    public Art(String name,String description,double startPrice,
               String artist,String style) {

        super(name,description,startPrice);
        this.artist = artist;
        this.style = style;
    }

    @Override
    public void printInfo() {
        System.out.println("ID: " + getId());
        System.out.println("Tên: " + getName());
        System.out.println("Giá khởi điểm: " + getStartPrice());
        System.out.println("Tác giả: " + artist);
        System.out.println("Phong cách: " + style);
    }
}