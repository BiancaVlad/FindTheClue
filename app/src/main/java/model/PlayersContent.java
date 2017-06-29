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

    public static class PlayerItem
    {
        String name;
        double score;
        String profile_picture;

        public PlayerItem(String name, double score, String profile_picture) {
            this.name = name;
            this.score = score;
            this.profile_picture = profile_picture;
        }

        public String getName(){return  name;}

        public void setName(String name) {this.name= name;}

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
