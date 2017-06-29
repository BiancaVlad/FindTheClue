package com.dissertation.findtheclue;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.view.SupportActionModeWrapper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.ConsoleMessage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.util.EntityUtils;
import model.ExternalLoginViewModel;
import model.GamesContent;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.RestClient;
import utils.ServiceHandler;
import utils.TokenSaver;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button fb_btn;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private static String url = "https://findthecluebe.azurewebsites.net/api/Account/ExternalLogins?returnUrl=%2F&generateState=true";
    private static final String TAG_NAME = "Name";
    private static final String TAG_URL = "Url";
    private static final String TAG_STATE = "State";
    List<ExternalLoginViewModel> externalLoginViewModels;

    String currentToken;
    boolean ok = false;

    android.widget.RelativeLayout.LayoutParams layoutparams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        showProgress(true);
        currentToken = TokenSaver.getToken(getApplicationContext());
        if(currentToken != null && !currentToken.isEmpty())
        {
            new CheckTokenTask().execute();
            //boolean ok = checkIfTokenOk();

            /*if (ok) {
                Intent intent = new Intent(LoginActivity.this, GamesListActivity.class);
                startActivity(intent);
                finish();
            }*/
        }

        setContentView(R.layout.activity_login);
        fb_btn = (Button) findViewById(R.id.fb_btn);
        fb_btn.setVisibility(View.INVISIBLE);

        //setActionBarTextColor("Find the Clue");

        ServiceHandler sh = new ServiceHandler();
        String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
        externalLoginViewModels = new ArrayList<>();

        final TextView email = (TextView) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);

        if (jsonStr != null) {
            try {
                // Getting JSON Array node
                JSONArray externalLogins = new JSONArray(jsonStr);

                for (int i = 0; i < externalLogins.length(); i++) {
                    JSONObject g = externalLogins.getJSONObject(i);

                    String name = g.getString(TAG_NAME);
                    String url = g.getString(TAG_URL);
                    String state = g.getString(TAG_STATE);

                    externalLoginViewModels.add(new ExternalLoginViewModel(name, url, state));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("utils.ServiceHandler", "Couldn't get any data from the url");
        }

        if(externalLoginViewModels.size() > 0)
        {
            fb_btn.setVisibility(View.VISIBLE);
        }

        fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onfbClick();
            }
        });

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
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

                String urlParameters  = "grant_type=password&username="+ usernameStr + "&password="+passStr;
                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(mediaType, urlParameters);
                Request request = new Request.Builder()
                        .url("http://findthecluebe.azurewebsites.net/Token")
                        .post(body)
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .addHeader("accept", "application/json")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful())
                    {
                        String responseData = response.body().string();

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String token = jsonObject.getString("access_token");
                            TokenSaver.setToken(getApplicationContext(), token);

                        } catch (JSONException e) {
                        }

                        String token = TokenSaver.getToken(getApplicationContext());
                        if(token != null && !token.isEmpty())
                        {
                            Intent intent = new Intent(LoginActivity.this, GamesListActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Login unsuccessful.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Intent intent = new Intent(view.getContext(), SignUpActivity.class);
                        intent.putExtra("username", usernameStr);
                        intent.putExtra("password", passStr);
                        view.getContext().startActivity(intent);
                        finish();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                /*try {
                    StringEntity entity = new StringEntity(urlParameters);
                    httpPUT.setEntity(entity);
                    httpPUT.setHeader("Accept", "application/json");
                    httpPUT.setHeader("Content-type", "x-www-form-urlencoded");
                    httpResponse = httpclient.execute(httpPUT);

                    String authTok = httpResponse.toString();

                    //callToSaveToken(httpResponse);
                    Log.d("InputStream", authTok);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (Exception e) {
                    Log.d("InputStream", e.getLocalizedMessage());
                }*/
                   /*Intent intent = new Intent(view.getContext(), SignUpActivity.class);
                view.getContext().startActivity(intent);*/
                //attemptLogin();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private boolean checkIfTokenOk() {

        String myUrl = "http://findthecluebe.azurewebsites.net/api/account/userinfo";

        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url(myUrl)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("authorization", "bearer " + currentToken)
                    .build();
            Response response = client.newCall(request).execute();

            boolean ok = false;
            if (response.isSuccessful())
            {
                ok = true;
                Log.d("InputStream",String.valueOf(ok));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

        return false;
    }

    private void setActionBarTextColor(String title)
    {
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        TextView textview = new TextView(LoginActivity.this);
        layoutparams = new RelativeLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.WRAP_CONTENT);
        textview.setLayoutParams(layoutparams);
        textview.setText(title);
        textview.setTextColor(Color.parseColor("#fc6902"));
        //textview.setTextColor(getResources().getColor(R.color.colorAccent));
        if (actionbar != null) {
            actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        }
        actionbar.setCustomView(textview);
    }

    private void onfbClick() {

        WebView webview = new WebView(this);

        setContentView(webview);
 /*       String myUrl = "https://findthecluebe.azurewebsites.net" + externalLoginViewModels.get(0).getUrl();
        webview.loadUrl(myUrl);

        webview.setWebViewClient(new WebViewClient()
        {
            public void onPageFinished(WebView view, String url)
            {
                try {
                    URL fbUrl = new URL(url);
                    String domain = fbUrl.getHost();

                    if(!domain.isEmpty() && domain.equalsIgnoreCase("findthecluebe.azurewebsites.net"))
                    {
                        String query = fbUrl.getQuery();

                        if(query != null) {
                            String code = getQueryMap(query).get("code");
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });*/
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                String myUrl = "http://findthecluebe.azurewebsites.net/api/account/facebooklogin";
                MediaType jsonMedia
                        = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("Token", accessToken.getToken().toString());
                    RequestBody body = RequestBody.create(jsonMedia, String.valueOf(jsonObject));

                        Request request = new Request.Builder()
                                .url(myUrl)
                                .post(body)
                                .addHeader("content-type", "application/json")
                                .addHeader("accept", "application/json")
                                .build();

                        Response response = client.newCall(request).execute();

                    if (response.isSuccessful())
                    {
                        String responseData = response.body().string();

                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            String token = jsonResponse.getString("access_token");
                            TokenSaver.setToken(getApplicationContext(), token);
                        } catch (JSONException e) {
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*HttpResponse httpResponse;
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httpPUT = new
                            HttpPost(myUrl);
                    String json = "";
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("Token", accessToken.getToken().toString());
                    json = jsonObject.toString();
                    StringEntity se = new StringEntity(json);
                    httpPUT.setEntity(se);
                    httpPUT.setHeader("Accept", "application/json");
                    httpPUT.setHeader("Content-type", "application/json");
                    httpResponse = httpclient.execute(httpPUT);
                    String authToken = httpResponse.getEntity().toString();

                    try {
                        JSONObject jsonObjfb = new JSONObject(authToken);
                        String token = jsonObjfb.getString("access_token");
                        String checktoken = token;

                    } catch (JSONException e) {
                    }
                    //callToSaveToken(httpResponse);
                    Log.d("authToken", authToken);
                } catch (Exception e) {
                    Log.d("InputStream", e.getLocalizedMessage());
                }*/

                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name,last_name");

                String token = TokenSaver.getToken(getApplicationContext());
                if(token != null && !token.isEmpty())
                {
                    Intent intent = new Intent(LoginActivity.this, GamesListActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Login with facebook unsuccessful!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancel() {
                showProgress(false);
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
    }

    public class CheckTokenTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return checkIfTokenOk();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success) {

                Intent intent = new Intent(LoginActivity.this, GamesListActivity.class);
                startActivity(intent);
                showProgress(false);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    public Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.dissertation.findtheclue/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.dissertation.findtheclue/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

