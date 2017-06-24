package com.dissertation.findtheclue;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        WebView view = (WebView) findViewById(R.id.app_description);
        String text;
        text = "<html><body style=\"color: #808080; margin: 0; padding: 0\"><p align=\"justify\">";
        text+= "This is an entertainment application for everybody who wants to discover great places and also to learn something about them.";
        text+= "</p></body></html>";
        view.setBackgroundColor(Color.TRANSPARENT);
        view.loadData(text, "text/html", "utf-8");

        WebView viewFeedBack = (WebView) findViewById(R.id.feedback);
        String textFeedBack;
        textFeedBack = "<html><body style=\"color: #808080; margin: 0; padding: 0\"><p align=\"justify\">";
        textFeedBack+= "If you encounter problems when using this app of if you have suggestions please contact us at:";
        textFeedBack+= "</p></body></html>";
        viewFeedBack.setBackgroundColor(Color.TRANSPARENT);
        viewFeedBack.loadData(textFeedBack, "text/html", "utf-8");


        WebView mailView = (WebView) findViewById(R.id.mail_web_view);
        String mail_text;
        mail_text = "<html><body style=\"color: #808080; margin: 0; padding: 0\"><p>";
        mail_text+= "<a href=\"mailto:contact@findtheclue.com\" title=\"send an email to contact@findtheclue.com\">contact@findtheclue.com</a>";
        mail_text+= "</p></body></html>";
        mailView.setBackgroundColor(Color.TRANSPARENT);
        mailView.loadData(mail_text, "text/html", "utf-8");

        TextView terms = (TextView) findViewById(R.id.terms_web_view);
        terms.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent=new Intent(getApplicationContext(), TermsAndConditionsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                if(this.isTaskRoot())
                {
                    Intent intent=new Intent(getApplicationContext(), GamesListActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        if(this.isTaskRoot())
        {
            Intent intent=new Intent(getApplicationContext(), GamesListActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            finish();
            super.onBackPressed();
        }
        //super.onBackPressed();  // optional depending on your needs
    }
}
