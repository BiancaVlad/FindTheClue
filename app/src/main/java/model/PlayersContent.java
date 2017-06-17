package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vlad on 17-Jun-17.
 */
public class PlayersContent {
    public static List<PlayerItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (GameItem) items, by ID.
     */
    public static Map<Integer, PlayerItem> ITEM_MAP = new HashMap<Integer, PlayerItem>();

    private static void addItem(PlayerItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId_player(), item);
    }

    public static class PlayerItem
    {
        int id_player;
        String first_name;
        String last_name;
        String email;
        String phone_number;
        String password;
        double score;
        String profile_picture;

        public PlayerItem(int id_player, String first_name, String last_name, String email, String phone_number, String password, double score, String profile_picture) {
            this.id_player = id_player;
            this.first_name = first_name;
            this.last_name = last_name;
            this.email = email;
            this.phone_number = phone_number;
            this.password = password;
            this.score = score;
            this.profile_picture = profile_picture;
        }

        public int getId_player() {
            return id_player;
        }

        public void setId_player(int id_player) {
            this.id_player = id_player;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getProfile_picture() {
            return profile_picture;
        }

        public void setProfile_picture(String profile_picture) {
            this.profile_picture = profile_picture;
        }
    }

}
