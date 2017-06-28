package com.dissertation.findtheclue;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import model.QuestionContent;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final TextView firstName = (TextView) findViewById(R.id.sign_up_first_name);
        final TextView lastName = (TextView) findViewById(R.id.sign_up_last_name);
        final TextView email = (TextView) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.sign_up_password);
        final EditText confPassword = (EditText) findViewById(R.id.sign_up_conf_password);

        String emailStr = "";
        if (getIntent().hasExtra("username")) {
            emailStr = getIntent().getStringExtra("username");
            if(emailStr!=null && !emailStr.isEmpty())
            {
                email.setText(emailStr);
            }
        }

        String passStr = "";
        if (getIntent().hasExtra("password")) {
            passStr = getIntent().getStringExtra("password");
            if(passStr!=null && !passStr.isEmpty())
            {
                password.setText(passStr);
            }
        }

        Button signUp = (Button) findViewById(R.id.sign_up_btn);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernameStr = email.getText().toString();
                if (TextUtils.isEmpty(usernameStr)) {
                    email.setError(getString(R.string.error_field_required));
                    return;
                }

                String passStr = password.getText().toString();

                if (TextUtils.isEmpty(passStr)) {
                    password.setError(getString(R.string.error_field_required));
                    return;
                }

                String confPass = confPassword.getText().toString();
                if (TextUtils.isEmpty(confPass)) {
                    confPassword.setError(getString(R.string.error_field_required));
                    return;
                }

                String myUrl = "http://findthecluebe.azurewebsites.net/api/account/register";
                HttpResponse httpResponse;
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httpPUT = new
                            HttpPost(myUrl);
                    String json = "";
                    JSONObject jsonObject = new JSONObject();
                    String name= firstName.getText().toString()+" " + lastName.getText().toString();
                    jsonObject.put("Name", name);
                    jsonObject.put("Email", email.getText().toString());
                    jsonObject.put("Password", password.getText().toString());
                    jsonObject.put("ConfirmPassword", confPassword.getText().toString());
                    jsonObject.put("ProfileImageUrl", "");
                    jsonObject.put("Points", 0);
                    json = jsonObject.toString();
                    StringEntity se = new StringEntity(json);
                    httpPUT.setEntity(se);
                    httpPUT.setHeader("Accept", "application/json");
                    httpPUT.setHeader("Content-type", "application/json");
                    httpResponse = httpclient.execute(httpPUT);

                    if(httpResponse.getStatusLine().getStatusCode() == 200)
                    {
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    Log.d("InputStream", e.getLocalizedMessage());
                }
            }
        });
    }
}
