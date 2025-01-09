package com.example.football.entity;
import java.util.ArrayList;
import java.util.List;


public class ShopItem {
    private int id;
    private String name;
    private int price;
    private String imageUrl; // 이미지 URL 필드 추가


    // 생성자와 getter 메서드 추가
    public ShopItem(int id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getImageUrl() { return imageUrl; } // Getter 추가

    // 샘플 아이템 목록을 제공하는 메서드
    public static List<ShopItem> getShopItems() {
        List<ShopItem> items = new ArrayList<>();
        items.add(new ShopItem(1, "맨시티 유니폼", 1500000, "/mancity.jpg"));
        items.add(new ShopItem(2, "리버풀 유니폼",1500000 , "/liverpool.jpg"));
        items.add(new ShopItem(3, "아스날 유니폼", 1500000, "/asenal.jpg"));
        items.add(new ShopItem(4, "첼시 유니폼", 1500000, "/chelsea.jpg"));
        items.add(new ShopItem(5, "토트넘 유니폼", 1500000, "/tottenham.jpg"));
        items.add(new ShopItem(6, "맨유 유니폼", 1500000, "/manu.jpg"));
        return items;
    }

    // ID로 아이템을 가져오는 메서드
    public static ShopItem getItemById(int id) {
        for (ShopItem item : getShopItems()) {
            if (item.getId() == id) return item;
        }
        return null;
    }
}
