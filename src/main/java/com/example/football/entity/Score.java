package com.example.football.entity;

public class Score {
    private FullTime fullTime;
    private String winner;

    // Getters and Setters
    public FullTime getFullTime() {
        return fullTime;
    }

    public void setFullTime(FullTime fullTime) {
        this.fullTime = fullTime;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public static class FullTime {
        private Integer home;
        private Integer away;

        public Integer getHome() {
            return home;
        }

        public void setHome(Integer home) {
            this.home = home;
        }

        public Integer getAway() {
            return away;
        }

        public void setAway(Integer away) {
            this.away = away;
        }
    }
}