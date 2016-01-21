package com.example.srivastava.friends;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class Profile extends ActionBarActivity implements profile_UI_home.OnFragmentInteractionListener,messages_user.OnFragmentInteractionListener,conversation.OnFragmentInteractionListener,root.OnFragmentInteractionListener,Notifications.OnFragmentInteractionListener {
boolean unsubscribed=false;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ichome,
            R.drawable.icmessage,
            R.drawable.notification
    };
    ViewPagerAdapter adapter;
    static String user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    getIntent().putExtra("objectId",ParseUser.getCurrentUser().getObjectId());
                    setupViewPager(viewPager);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    getIntent().putExtra("objectId",ParseUser.getCurrentUser().getObjectId());
                    setupViewPager(viewPager);
                }
            }
        });
        setupTabIcons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile.this);
            alertDialogBuilder.setTitle("Settings");
            LayoutInflater li = LayoutInflater.from(Profile.this);
            final View dialogView = li.inflate(R.layout.activity_setting__alert, null);
            final Spinner spPrivacy = (Spinner) dialogView.findViewById(R.id.spPrivacy);
            final Spinner spNotifications = (Spinner) dialogView.findViewById(R.id.spNotifications);
            final Spinner spMessages = (Spinner) dialogView.findViewById(R.id.spMsgs);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Profile.this, R.array.yesno, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spPrivacy.setAdapter(adapter);
            spNotifications.setAdapter(adapter);
            spMessages.setAdapter(adapter);
            ParseQuery query=ParseQuery.getQuery("_User");
            query.whereEqualTo("objectId", getIntent().getStringExtra("objectId"));
            ParseObject obj= null;
            try {
                obj = query.getFirst();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            spPrivacy.setSelection(obj.getBoolean("AllowUserListing")?0:1);
            spNotifications.setSelection(obj.getBoolean("AllowNotifications")?0:1);
            if(spNotifications.getSelectedItemPosition()==1) {
                unsubscribed = true;
                unsubscribeChannels();
            }
            spMessages.setSelection(obj.getBoolean("AllowMessages")?0:1);
            alertDialogBuilder.setView(dialogView);
            final ParseObject finalObj = obj;
            alertDialogBuilder.setPositiveButton("Add",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finalObj.put("AllowUserListing", spPrivacy.getSelectedItemPosition() == 0 ? true : false);
                            finalObj.put("AllowNotifications", spNotifications.getSelectedItemPosition() == 0 ? true : false);
                            finalObj.put("AllowMessages", spMessages.getSelectedItemPosition() == 0 ? true : false);
                            try {
                                finalObj.save();
                                if(spNotifications.getSelectedItemPosition()==1) {
                                    unsubscribed = true;
                                    unsubscribeChannels();
                                }
                                else
                                {
                                    if(unsubscribed)
                                    {
                                        subscribeChannels();
                                        unsubscribed=false;
                                    }

                                }

                                Toast.makeText(Profile.this, "Settings has been updated successfully", Toast.LENGTH_LONG).show();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    });

            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }
        if (id == R.id.logout) {

            /*ParsePush.unsubscribeInBackground("all", new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {


                        ParsePush.unsubscribeInBackground(ParseUser.getCurrentUser().getString("username"), new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {*/
                                    if(!unsubscribed)
                                    {
                                        unsubscribeChannels();
                                    }
                                    ParseUser current = ParseUser.getCurrentUser();
                                    current.logOut();
                                    Intent intent = new Intent(Profile.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return true;
                                /*}
                                else{
                                    Toast.makeText(Profile.this,e.getMessage()+" user",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else{
                            Toast.makeText(Profile.this,e.getMessage()+ "all",Toast.LENGTH_LONG).show();
                    }
                }
            });
            return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    private void unsubscribeChannels() {
        if(unsubscribed){
        ParsePush.unsubscribeInBackground("all", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {


                    ParsePush.unsubscribeInBackground(ParseUser.getCurrentUser().getString("username"), new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                        }
                    });
                }
                else{
                    Toast.makeText(Profile.this,e.getMessage()+ "all",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    }
    private void subscribeChannels() {
        if(unsubscribed){
            ParsePush.subscribeInBackground("all", new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {


                        ParsePush.subscribeInBackground(ParseUser.getCurrentUser().getString("username"), new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });
                    }
                    else{
                        Toast.makeText(Profile.this,e.getMessage()+ "all",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void setupTabIcons() {

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);

        Drawable image = ContextCompat.getDrawable(Profile.this, tabIcons[1]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        tabLayout.getTabAt(1).setIcon(image);
        image = ContextCompat.getDrawable(Profile.this, tabIcons[2]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        tabLayout.getTabAt(2).setIcon(image);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter= new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new profile_UI_home(), "Home");
        adapter.addFragment(new messages_user(), "Messages");
        adapter.addFragment(new Notifications(), "Notifications");
        viewPager.setAdapter(adapter);
    }

    public void openConversationThread(String username) {
        Intent intent=new Intent(Profile.this,conversationActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }
        @Override
        public CharSequence getPageTitle(int position) {


            return  mFragmentTitleList.get(position);
        }
    }



}
