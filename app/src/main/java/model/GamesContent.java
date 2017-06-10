package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vlad on 10-Jun-17.
 */
public class GamesContent {
    public static List<GameItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (GameItem) items, by ID.
     */
    public static Map<Integer, GameItem> ITEM_MAP = new HashMap<Integer, GameItem>();

    private static void addItem(GameItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id_game, item);
    }

    public static String[] getNames(){
        String [] names= new String[ITEMS.size()];
        for(int i=0; i<ITEMS.size(); i++)
            names[i]=ITEMS.get(i).getName();
        return names;
    }

    public static class GameItem
    {
        private int id_game;
        private String name;
        private String country;
        private String city;
        private int difficulty;
        private double rating;
        private String picture;
        private int duration;

        public GameItem(int id_game, String name, String country, String city, int difficulty, double rating, String picture, int duration) {
            this.id_game = id_game;
            this.name = name;
            this.country = country;
            this.city = city;
            this.difficulty = difficulty;
            this.rating = rating;
            this.picture = picture;
            this.duration = duration;
        }

        public int getId_game() {
            return id_game;
        }

        public void setId_game(int id_game) {
            this.id_game = id_game;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(int difficulty) {
            this.difficulty = difficulty;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public int getDuration() {
            return duration;
        }

        public void setRating(int duration) {
            this.duration = duration;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
