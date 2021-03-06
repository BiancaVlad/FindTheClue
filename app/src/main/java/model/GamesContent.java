package model;

import com.google.android.gms.games.Game;

import java.io.ByteArrayInputStream;
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

    public static GameItem getGameById(int gameID)
    {
        for (GamesContent.GameItem item: GamesContent.ITEMS) {
            if(item.getId_game() == gameID)
            {
                return item;
            }
        }

        return null;
    }

    public static class GameItem
    {
        private int id_game;
        private String name;
        private String country;
        private String city;
        private int difficulty;
        private double rating;
        private int rating_counter;
        private String picture;
        private int duration;
        private String description;

        public GameItem(int id_game, String name, String country, String city, String description, int difficulty, double rating, int rating_counter, String picture, int duration) {
            this.id_game = id_game;
            this.name = name;
            this.country = country;
            this.description = description;
            this.city = city;
            this.difficulty = difficulty;
            this.rating = rating;
            this.rating_counter = rating_counter;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public int getRatingCounter() {return rating_counter; }

        public void setRatingCounter(int rating_counter) {this.rating_counter = rating_counter; }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
