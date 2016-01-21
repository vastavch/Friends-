package com.example.srivastava.friends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {

        EditText txtName,txtEmail,txtPassword,txtConfirm;
        Button btnCancel;
        Button btnSignup;
        Spinner spGender;
        boolean hasEmail;
        String usrnme;
        ParseUser user = new ParseUser();
        public Signup() {
            // Required empty public constructor
        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_signup);
            txtName = (EditText) findViewById(R.id.txtUserName);
            txtEmail = (EditText) findViewById(R.id.txtEmail);
            txtPassword = (EditText) findViewById(R.id.txtPassword);
            txtConfirm = (EditText) findViewById(R.id.txtConfirm);
            btnSignup = (Button) findViewById(R.id.btnSignup);
            btnCancel = (Button) findViewById(R.id.btnCancel);
            spGender = (Spinner) findViewById(R.id.spGender);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gender)); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spGender.setAdapter(spinnerArrayAdapter);
            btnSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateUserInfo()) {

                        user.setUsername(txtName.getText().toString());
                        user.setEmail(txtEmail.getText().toString());
                        user.setPassword(txtPassword.getText().toString());
                        user.put("gender",getResources().getStringArray(R.array.gender)[spGender.getSelectedItemPosition()]);
                        user.put("username", txtName.getText().toString());
                        user.put("AllowUserListing", true);
                        user.put("AllowNotifications", true);
                        user.put("AllowMessages", true);
                        user.signUpInBackground(new SignUpCallback() {
                            public void done(ParseException e) {
                                if (e == null) {

                                    createNotification(txtName.getText().toString());
                                /*final Intent intent = new Intent(Signup.this, MainActivity.class);
                                startActivity(intent);*/
//                                ParseUser.logInInBackground(txtEmail.getText().toString(), txtPassword.getText().toString(), new LogInCallback() {
//                                    @Override
//                                    public void done(ParseUser user, ParseException e) {
//                                        intent.putExtra("objectId",user.getObjectId());
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                });
                                } else {
                                    Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Signup.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }

    private void createNotification(final String s) {

        List<String> channels = new ArrayList<String>();
        channels.add("all");
        channels.add(s);
        usrnme = s;
            /*ParsePush.subscribeInBackground("all", new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {


                        ParsePush.subscribeInBackground(s, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
//                                                    user.saveInBackground(new SaveCallback() {
//                                                    @Override
//                                                     public void done(ParseException e) {
//                                                             if (e == null) {*/
        final String message = usrnme + " has been added to the community!!!";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Message", message);
        ParseCloud.callFunctionInBackground("hello", params, new FunctionCallback<String>() {
            @Override
            public void done(String object, ParseException e) {
                if (e == null) {
                    //                                  mListener.showMessage(object);
                    //                                     Log.d("pushrspnse", object);
                    ParseObject obj = new ParseObject("Notifications");
                    obj.put("SentTo", "all");
                    obj.put("CreatedBY", s);
                    obj.put("Message", message);
                    try {
                        obj.save();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    Toast.makeText(Signup.this, "Sign Up Successful", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(Signup.this, MainActivity.class);
                    startActivity(i);


                } else {
                    //mListener.showMessage(e.getMessage());
                    //Log.d("excptn_in_subscribing", e.getMessage());
                    Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    } /*else {
//                                                mListener.showMessage(e.getMessage());
//                                                Log.d("excptn", e.getMessage());
                                    Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
// mListener.showMessage(e.getMessage());
//Log.d("excptn", e.getMessage());
                        Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }*/
//    else {
//                                Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//                }
//                });
    //upon successful login
           /*    try {
                   usrnme = s;
            List<String> channels=new ArrayList<String>();
            channels.add("all");
            channels.add(s);
            //parseuser.signUp();
            ParsePush.subscribeInBackground(usrnme, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ParsePush.subscribeInBackground("all", new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    user.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                            String message = usrnme + " started StayinTouch!say hello";
                                            HashMap<String, String> params = new HashMap<String, String>();
                                            params.put("Message", message);
                                            if (e == null) {
                                                ParseCloud.callFunctionInBackground("hello", params, new FunctionCallback<String>() {
                                                    @Override
                                                    public void done(String object, ParseException e) {
                                                        if (e == null) {
                                                            //mListener.showMessage(object);
                                                            //Log.d("pushrspnse", object);
                                                            //Intent i = new Intent(getActivity(), DetailedAlbum.class);
                                                            //startActivity(i);
                                                        } else {
                                                            //mListener.showMessage(e.getMessage());
                                                            Log.d("excptn", e.getMessage());
                                                        }
                                                    }
                                                });
                                            } else {
                                                //mListener.showMessage(e.getMessage());
                                                Log.d("excptn", e.getMessage());
                                            }

                                        }
                                    });


                                }

                                else

                                {
                                    //mListener.showMessage(e.getMessage());
                                    Log.d("excptn_in_subscribing", e.getMessage());
                                }

                            }
                        });
                    }

                    else

                    {
                        //mListener.showMessage(e.getMessage());
                        Log.d("extn_subscribing_usrnme", e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            //mListener.showMessage(e.getMessage());
            Log.d("excptn_signup", e.getMessage());
        }*/

    boolean isValid(CharSequence s) {
        final Pattern sPattern = Pattern.compile("^[a-z]*$");
        return sPattern.matcher(s).matches();
    }
        private boolean validateUserInfo() {
            String username,email,password,confirmpass;
            username=txtName.getText().toString();
            email=txtEmail.getText().toString();
            password=txtPassword.getText().toString();
            confirmpass=txtConfirm.getText().toString();
            if(isValid(username)) {
                if (!username.equals(null) && !username.equals("") && !email.equals(null) && !email.equals("") && !password.equals("") && !password.equals(null) && !confirmpass.equals(null) && !confirmpass.equals("")) {
                    if (password.equals(confirmpass)) {
                        hasEmail = false;
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("Email", email);
                        query.findInBackground(new FindCallback<ParseUser>() {
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.size() > 1) {
                                        hasEmail = true;
                                    }
                                } else {
                                    hasEmail = true;
                                }
                            }
                        });
                        if (hasEmail) {
                            Toast.makeText(Signup.this, "Email id already exists", Toast.LENGTH_LONG).show();
                            return false;
                        }

                    } else {
                        Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Toast.makeText(Signup.this, "Please enter all the fields", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            else{
                Toast.makeText(Signup.this, "Username must contain only small letters", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

    }
