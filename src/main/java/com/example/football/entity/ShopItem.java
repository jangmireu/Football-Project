package com.example.football.entity;
import java.util.ArrayList;
import java.util.List;


public class ShopItem {
    private int id;
    private String name;
    private int price;
    private String imageUrl;
    private String link;


    // 생성자와 getter 메서드 추가
    public ShopItem(int id, String name, int price, String imageUrl, String link) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.link = link;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getImageUrl() { return imageUrl; } // Getter 추가
    public String getLink() {return link;}

    // 샘플 아이템 목록을 제공하는 메서드
    public static List<ShopItem> getShopItems() {
        List<ShopItem> items = new ArrayList<>();
        items.add(new ShopItem(1, "맨시티 유니폼", 200000, "/mancity.jpg", "https://shop.mancity.com/en/manchester-city-home-jersey-2024-25/701230876-teamlightblue.html"));
        items.add(new ShopItem(2, "리버풀 유니폼",200000 , "/liverpool.jpg","https://store.liverpoolfc.com/kr/lfc-nike-mens-24-25-home-match-jersey"));
        items.add(new ShopItem(3, "아스날 유니폼", 200000, "/asenal.jpg","https://arsenaldirect.arsenal.com/Home-Kit/Arsenal-adidas-24-25-Home-Shirt/p/MIT6141"));
        items.add(new ShopItem(4, "첼시 유니폼", 200000, "/chelsea.jpg","https://www.chelseamegastore.com/ko/chelsea-nike-home-stadium-shirt-2024-25-with-palmer-20-printing/p-806667321736158707+z-8-1573916724?_ref=p-GALP:m-GRID:i-r0c0:po-0"));
        items.add(new ShopItem(5, "토트넘 유니폼", 200000, "/tottenham.jpg","https://shop.tottenhamhotspur.com/Heung-Min-Son-Mens-Elite-Premier-League-Tottenham-Hotspur-Home-Shirt-2024-25/EMHSON24"));
        items.add(new ShopItem(6, "맨유 유니폼", 200000, "/manu.jpg","https://store.manutd.com/ko-kr/p/manchester-united-2425-home-jersey-1629"));
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
