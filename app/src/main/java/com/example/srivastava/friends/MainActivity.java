package com.example.srivastava.friends;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView info;
    private CallbackManager callbackManager;
    EditText txtEmail, txtPassword;
    Button btnLogin, btnCreateNew, btnTwitter, btnFacebook2;
    private static ProgressDialog pd;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseTwitterUtils.initialize("aa3JwDpdWffGLngcpuy28Gpbm", "TjxIpC2x0m0JzfPuCBrhh8QOcQiCuZvu1gzR5viUIuTguVSIMh");
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);



        final Context context = getApplicationContext();

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        final List<String> permissions = Arrays.asList("public_profile", "email");
        btnFacebook2 = (Button) findViewById(R.id.btnFacebook2);
        btnFacebook2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {

                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            if (!ParseFacebookUtils.isLinked(user)) {
                                user.put("AllowUserListing", true);
                                user.put("AllowNotifications", true);
                                user.put("AllowMessages", true);
                                try {
                                    user.save();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                ParseFacebookUtils.linkWithReadPermissionsInBackground(user, MainActivity.this, permissions);
                                Intent intent=new Intent(MainActivity.this,Profile.class);
                                intent.putExtra("objectId", user.getObjectId());
                                startActivity(intent);
                                finish();

                            }

                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                        } else {
                            Intent intent = new Intent(MainActivity.this, Profile.class);
                            intent.putExtra("objectId", user.getObjectId());
                            startActivity(intent);

                            finish();
                            Log.d("MyApp", "User logged in through Facebook!");
                        }
                    }
                });
            }
        });
        ParseUser currentUser = ParseUser.getCurrentUser();
        /*if (currentUser != null) {
            Intent intent = new Intent(MainActivity.this, Messages.class);
            startActivity(intent);
            finish();
        } else {*/
        txtEmail = (EditText) findViewById(R.id.txtUserName);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String user, password;
                user = txtEmail.getText().toString();
                password = txtPassword.getText().toString();

                if (!user.equals(null) && !user.equals("") && !password.equals(null) && !password.equals("")) {
                    Log.d("login",txtEmail.getText().toString()+"\t"+txtPassword.getText().toString() );
                    ParseUser.logInInBackground(txtEmail.getText().toString(), txtPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, com.parse.ParseException e) {
                            if (user != null) {
                                final String usrnme = txtEmail.getText().toString();
                                ParseInstallation.getCurrentInstallation().saveInBackground();
                                ParsePush.subscribeInBackground("all", new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {


                                            ParsePush.subscribeInBackground(usrnme, new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
//                                                    user.saveInBackground(new SaveCallback() {
//                                                    @Override
//                                                     public void done(ParseException e) {
//                                                             if (e == null) {

                                                    } else {
//                                                mListener.showMessage(e.getMessage());
//                                                Log.d("excptn", e.getMessage());
                                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
// mListener.showMessage(e.getMessage());
//Log.d("excptn", e.getMessage());
                                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                                Intent intent = new Intent(MainActivity.this, Profile.class);
                                intent.putExtra("objectId", user.getObjectId());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Incorrect Email or Password" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Email and password cannot be empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCreateNew = (Button) findViewById(R.id.btnCreateNew);

        btnCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
                finish();
            }
        });

        btnTwitter = (Button) findViewById(R.id.twitter);

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseTwitterUtils.logIn(v.getContext(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {

                            Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                        } else if (user.isNew()) {
                            user.put("AllowUserListing", true);
                            user.put("AllowNotifications", true);
                            user.put("AllowMessages", true);
                            try {
                                user.save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Log.d("MyApp", "User signed up and logged in through Twitter!");
                            ParseTwitterUtils.linkInBackground(user, ParseTwitterUtils.getTwitter().getUserId(), ParseTwitterUtils.getTwitter().getScreenName(), ParseTwitterUtils.getTwitter().getAuthToken(), ParseTwitterUtils.getTwitter().getAuthTokenSecret());
                            user.setUsername(ParseTwitterUtils.getTwitter().getScreenName());
                            user.saveInBackground();

                            Intent intent = new Intent(MainActivity.this, Profile.class);
                            intent.putExtra("objectId", user.getObjectId());
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d("MyApp", "User logged in through Twitter!");
                            Intent intent = new Intent(MainActivity.this, Profile.class);
                            intent.putExtra("objectId", user.getObjectId());
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }

        });
        //}
        //printHashKey();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


//    private void homeAsUpByBackStack() {
//        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
//        if (backStackEntryCount > 0) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        } else {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.Logout) {
            ParseUser current = ParseUser.getCurrentUser();
            current.logOut();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    protected static void showProgress(String message) {
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    protected static void hideProgress() {
        pd.dismiss();
    }

    public void printHashKey() {
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo("mad.com.stayintouch",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("HASH KEY:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

    }
}
