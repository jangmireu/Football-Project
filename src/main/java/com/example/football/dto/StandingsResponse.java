package com.example.football.dto;

import java.util.List;

public class StandingsResponse {

    private List<StandingItem> standings;

    public List<StandingItem> getStandings() {
        return standings;
    }

    public void setStandings(List<StandingItem> standings) {
        this.standings = standings;
    }

    public static class StandingItem {
        private List<TableEntry> table;

        public List<TableEntry> getTable() {
            return table;
        }

        public void setTable(List<TableEntry> table) {
            this.table = table;
        }
    }

    public static class TableEntry {
        private int position;
        private Team team;
        private int points;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public Team getTeam() {
            return team;
        }

        public void setTeam(Team team) {
            this.team = team;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }

    public static class Team {
        private String name;
        private String crest;
        private String tla;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
}
