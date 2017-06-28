package model;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vlad on 27-Jun-17.
 */
public class QuestionsAdapter {
    public static List<String> ListViewQuestions = new ArrayList<>();

    public static List<QuestionContent.QuestionItem> GameQuestions = new ArrayList<>();

    QuestionsAdapter()
    {
        GameQuestions.clear();
        ListViewQuestions.clear();
    }
}
