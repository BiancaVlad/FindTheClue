package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionContent {

    public static List<QuestionItem> ITEMS = new ArrayList<>();
    public static int questionCounter;
    /**
     * A map of sample (GameItem) items, by ID.
     */
    public static Map<Integer, QuestionItem> ITEM_MAP = new HashMap<Integer, QuestionItem>();

    private static void addItem(QuestionItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.idQuestion, item);
    }
    /**
     * Created by Vlad on 16-Jun-17.
     */
    public static class QuestionItem {
        private int idQuestion;
        private String textAnswer;
        private double score;
        private int gameId;
        private double longitude;
        private double latitude;
        private String questionText;

        public QuestionItem() {
        }

        public QuestionItem(int idQuestion, String textAnswer, double score, int gameId, double longitude, double latitude, String questionText) {
            this.idQuestion = idQuestion;
            this.textAnswer = textAnswer;
            this.score = score;
            this.gameId = gameId;
            this.longitude = longitude;
            this.latitude = latitude;
            this.questionText = questionText;
        }

        public int getIdQuestion() {
            return idQuestion;
        }

        public void setIdQuestion(int idQuestion) {
            this.idQuestion = idQuestion;
        }

        public String getTextAnswer() {
            return textAnswer;
        }

        public void setTextAnswer(String textAnswer) {
            this.textAnswer = textAnswer;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getGameId() {
            return gameId;
        }

        public void setGameId(int gameId) {
            this.gameId = gameId;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getQuestionText() {
            return questionText;
        }

        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }
    }
}