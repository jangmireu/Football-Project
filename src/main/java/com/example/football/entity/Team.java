package com.example.football.entity;

public class Team {
    private Long id;
    private String name;
    private String shortName;
    private String tla; 
    private String crest; // 팀 로고 URL

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCrest() {
        return crest;
    }

    public void setCrest(String crest) {
        this.crest = crest;
    }
    public String getTla() {
        return tla;
    }

    public void setTla(String tla) {
        this.tla = tla;
    }
}
