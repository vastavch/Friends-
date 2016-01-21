package com.example.srivastava.friends;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class Albums extends AppCompatActivity implements gdAdapter.ReLoad,UserSelectionAdapter.ReLoad {

    List<ParseObject> objs;
    List<String> selectedUsers;
    String selectedAlbum="";
    String uname;
    String userID;
    boolean isCurrentUser=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        selectedUsers=new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
       ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle("Albums");
      userID=getIntent().getStringExtra("objectId");
        isCurrentUser=getIntent().getBooleanExtra("isCurrentUser", true);
        objs=new ArrayList<ParseObject>();
        loadData(userID);
        if(!isCurrentUser){
            findViewById(R.id.fab).setVisibility(View.GONE);
        }
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Albums.this);
                alertDialogBuilder.setTitle("Create Album..");
                LayoutInflater li = LayoutInflater.from(Albums.this);
                final View dialogView = li.inflate(R.layout.activity_dialog, null);
                final EditText input = (EditText) dialogView.findViewById(R.id.txtAlbumName);
                final Spinner spinnercategory = (Spinner) dialogView.findViewById(R.id.spPrivacyOptions);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Albums.this, R.array.Privacy, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnercategory.setAdapter(adapter);
                alertDialogBuilder.setView(dialogView);
                alertDialogBuilder.setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ParseObject obj = new ParseObject("Albums");
                                obj.put("AlbumName", input.getText().toString());
                                obj.put("CreatedBy", ParseUser.getCurrentUser());
                                obj.put("Privacy", spinnercategory.getSelectedItemPosition() == 1 ? true : false);
                                try {
                                    obj.save();
                                    Toast.makeText(Albums.this, "Album has been saved successfully", Toast.LENGTH_LONG).show();
                                    loadData(userID);
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
            }
        });

    }
    public void loadData(String objID){
        ParseQuery query=ParseQuery.getQuery("Albums");
        List<ParseObject> lstAlbums=new ArrayList<ParseObject>();
        List<ParseObject> lstFinalAlbums=new ArrayList<ParseObject>();
        if(isCurrentUser){
            query.whereEqualTo("CreatedBy",ParseObject.createWithoutData("_User",objID));
            try {
                lstAlbums=query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ParseQuery<ParseObject> shareAlbums=ParseQuery.getQuery("User_Album_Sharing");
            shareAlbums.whereEqualTo("SharedUser",ParseObject.createWithoutData("_User",objID));
             try {
                for(Object o:shareAlbums.find()){
                    ParseObject po=(ParseObject)o;
                    ParseQuery q=ParseQuery.getQuery("Albums");
                    q.whereEqualTo("objectId",po.getParseObject("AlbumID").getObjectId());
                    lstFinalAlbums.add(q.getFirst());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            lstFinalAlbums.addAll(lstAlbums);
        }
        else{
            query.whereEqualTo("CreatedBy",ParseObject.createWithoutData("_User",objID));
            try {
                lstAlbums=query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for(ParseObject po:lstAlbums){
                if(po.getBoolean("Privacy")) {
                    lstFinalAlbums.add(po);
                }
            }
            for(ParseObject po:lstFinalAlbums){
                lstAlbums.remove(po);
            }
            ParseQuery qry = ParseQuery.getQuery("User_Album_Sharing");
            qry.whereEqualTo("SharedUser",ParseObject.createWithoutData("_User",objID));
            try {
                for(Object o:qry.find()){
                    ParseObject po=(ParseObject)o;
                    ParseQuery q=ParseQuery.getQuery("Albums");
                    q.whereEqualTo("objectId",po.getParseObject("AlbumID").getObjectId());
                    lstFinalAlbums.add(q.getFirst());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        objs=lstFinalAlbums;
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
                return new ViewHolder(getLayoutInflater().inflate(R.layout.activity_gd_view_adapter, parent, false));
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, final int position) {
                if(!isCurrentUser || !objs.get(position).getParseUser("CreatedBy").getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    viewHolder.imgDelete.setVisibility(View.GONE);
                    viewHolder.imgSettings.setVisibility(View.GONE);
                    viewHolder.imgShare.setVisibility(View.GONE);
                    viewHolder.imgSharedUsers.setVisibility(View.GONE);
                }
                viewHolder.text1.setText(objs.get(position).getString("AlbumName"));
                viewHolder.text2.setText(objs.get(position).getCreatedAt().toString());
                try {
                    if(objs.get(position).getParseObject("AlbumCover")!=null)
                    Picasso.with(Albums.this).load(objs.get(position).getParseObject("AlbumCover").fetchIfNeeded().getParseFile("Image").getUrl())
                            .error(R.drawable.avatar)
                            .placeholder(R.drawable.avatar)
                           .resize(200, 200).centerInside().into(viewHolder.img);
                    else
                        Picasso.with(Albums.this).load(R.drawable.avatar)
                                .resize(200, 200).centerInside().into(viewHolder.img);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                viewHolder.imgShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedAlbum= objs.get(position).getObjectId();
                        selectedUsers=new ArrayList<String>();
                        List<String> usersList=new ArrayList<String>();
                        ParseQuery obj = ParseQuery.getQuery("User_Album_Sharing");
                        obj.whereEqualTo("AlbumID",ParseObject.createWithoutData("Albums",selectedAlbum));
                        try {
                            for(Object po:obj.find())
                            {
                                ParseObject p=(ParseObject) po;
                               // usersList.add((ParseUser)ParseUser.createWithoutData("_User",p.getParseUser("SharedUser").getObjectId()));
                                usersList.add(p.getParseUser("SharedUser").getObjectId());
                            }
                           // usersList.add((ParseUser)ParseObject.createWithoutData("_User", ParseUser.getCurrentUser().getObjectId()));
                            usersList.add(ParseUser.getCurrentUser().getObjectId());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Albums.this);
                        alertDialogBuilder.setTitle("Select Users..");
                        LayoutInflater li = LayoutInflater.from(Albums.this);
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereNotContainedIn("objectId",usersList);
                        List<ParseUser> users=new ArrayList<ParseUser>();
                        try {
                            users=query.find();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        final View dialogView = li.inflate(R.layout.activity_select__user, null);
                        ListView lv=(ListView)dialogView.findViewById(R.id.lstUsers);
                        UserSelectionAdapter adapter = new UserSelectionAdapter(Albums.this, dialogView.getContext(), R.layout.activity_list__user__adapter, users);
                        lv.setAdapter(adapter);
                        alertDialogBuilder.setView(dialogView);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                for (String userid : selectedUsers) {
                                    ParseObject object = new ParseObject("User_Album_Sharing");
                                    ParseObject obj = new ParseObject("Albums");
                                    ParseUser user = new ParseUser();

                                    object.put("AlbumID", ParseObject.createWithoutData("Albums", selectedAlbum));
                                    object.put("SharedUser", ParseObject.createWithoutData("_User", userid));
                                    try {
                                        object.save();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    ParseQuery parseQuery = ParseQuery.getQuery("_User");
                                    parseQuery.whereEqualTo("objectId", userid);

                                    try {
                                        uname = parseQuery.getFirst().getString("username");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    HashMap<String, Object> params = new HashMap<String, Object>();
                                    //params.put("recipientId", objects.get(0).getObjectId());
                                    params.put("message",ParseUser.getCurrentUser().getUsername()+ " wants share an album with you");
                                    // params.put("album_id", );
                                    //params.put("photo_id", PhotoId);

                                    params.put("username", uname);
                                    params.put("sender", ParseUser.getCurrentUser().getUsername());

                                    ParseCloud.callFunctionInBackground("albumsharing", params, new FunctionCallback<String>() {
                                        @Override
                                        public void done(String object, com.parse.ParseException e) {
                                            if (e == null) {

                                                ParseObject obj1 = new ParseObject("Notifications");
                                                obj1.put("SentTo", uname);
                                                obj1.put("CreatedBY", ParseUser.getCurrentUser().getUsername());
                                                obj1.put("Message", "Album has been shared to you by " + ParseUser.getCurrentUser().getUsername());
                                                try {
                                                    obj1.save();
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                }
                                        Toast.makeText(Albums.this, "Album has been shared to users successfully", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
                viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedAlbum= objs.get(position).getObjectId();
                        if(!selectedAlbum.equals("")) {
                            new AlertDialog.Builder(Albums.this)
                                    .setTitle("Delete entry")
                                    .setMessage("Are you sure you want to delete this Album all photos will be deleted in the album?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            ParseQuery<ParseObject> notifications = ParseQuery.getQuery("Notifications");
                                            ParseQuery<ParseObject> obj = ParseQuery.getQuery("Albums");
                                            obj.whereEqualTo("objectId", selectedAlbum);
                                            ParseObject album = new ParseObject("Albums");
                                            try {
                                                List<ParseObject> lstObjs = obj.find();
                                                if (lstObjs != null) {
                                                    album = lstObjs.get(0);
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            notifications.whereEqualTo("AlbumID", album);
                                            try {
                                                if (notifications.find() != null) {
                                                    for (ParseObject obj1 : notifications.find()) {
                                                        try {
                                                            obj1.delete();
                                                        } catch (ParseException e1) {
                                                            e1.printStackTrace();
                                                        }
                                                    }
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            ParseQuery<ParseObject> phto = ParseQuery.getQuery("Photos");
                                            phto.whereEqualTo("AlbumID", ParseObject.createWithoutData("Albums", album.getObjectId()));
                                            try {
                                                if (phto.find() != null) {
                                                    for (ParseObject obj1 : phto.find()) {
                                                        try {
                                                            obj1.delete();
                                                        } catch (ParseException e1) {
                                                            e1.printStackTrace();
                                                        }
                                                    }
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            ParseQuery<ParseObject> user_album_sharing = ParseQuery.getQuery("User_Album_Sharing");

                                            user_album_sharing.whereEqualTo("AlbumID", ParseObject.createWithoutData("Albums", album.getObjectId()));
                                            try {
                                                if (user_album_sharing.find() != null) {
                                                    for (ParseObject obj1 : user_album_sharing.find()) {
                                                        try {
                                                            obj1.delete();
                                                        } catch (ParseException e1) {
                                                            e1.printStackTrace();
                                                        }
                                                    }
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            try {
                                                album.delete();
                                                loadData(userID);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                });
                viewHolder.imgSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedAlbum= objs.get(position).getObjectId();
                        AlertDialog.Builder adb = new AlertDialog.Builder(Albums.this);
                        adb.setTitle("Change Settings..");
                        LayoutInflater LI = LayoutInflater.from(Albums.this);
                        final View dv = LI.inflate(R.layout.activity_dialog, null);
                        final EditText input = (EditText) dv.findViewById(R.id.txtAlbumName);
                        input.setVisibility(View.GONE);
                        ParseQuery obj = ParseQuery.getQuery("Albums");
                        obj.whereEqualTo("objectId",selectedAlbum);
                        ParseObject obj1= null;
                        try {
                            obj1 = (ParseObject)obj.find().get(0);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        final Spinner spinnercategory = (Spinner) dv.findViewById(R.id.spPrivacyOptions);
                        ArrayAdapter<CharSequence> adaadpter = ArrayAdapter.createFromResource(Albums.this, R.array.Privacy, android.R.layout.simple_spinner_item);
                        adaadpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnercategory.setAdapter(adaadpter);
                        if(obj1.getBoolean("Privacy")){
                            spinnercategory.setSelection(1);
                        }
                        adb.setView(dv);
                        final ParseObject finalObj = obj1;
                        adb.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        finalObj.put("Privacy", spinnercategory.getSelectedItemPosition() == 1 ? true : false);
                                        try {
                                            finalObj.save();
                                            Toast.makeText(Albums.this, "settings has been updated successfully", Toast.LENGTH_LONG).show();

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                        adb.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog ad = adb.create();
                        ad.show();
                    }
                });
                viewHolder.imgSharedUsers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedAlbum= objs.get(position).getObjectId();
                        ParseQuery obj = ParseQuery.getQuery("User_Album_Sharing");
                        obj.whereEqualTo("AlbumID", ParseObject.createWithoutData("Albums", selectedAlbum));
                        List<String> usrs=new ArrayList<String>();
                        try {
                            for(Object po:obj.find())
                            {
                                ParseObject p=(ParseObject) po;
                                if(p.getParseUser("SharedUser")!=null) {
                                    ParseQuery q=ParseQuery.getQuery("_User");
                                    q.whereEqualTo("objectId",p.getParseUser("SharedUser").getObjectId());
                                    usrs.add(q.getFirst().getString("username"));
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        final CharSequence[] sharedusers = usrs.toArray(new String[usrs.size()]);
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Albums.this);
                        dialogBuilder.setTitle("Shared Users...");
                        dialogBuilder.setItems(sharedusers, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                String selectedText = sharedusers[item].toString();  //Selected item in listview
                            }
                        });
                        //Create alert dialog object via builder
                        AlertDialog alertDialogObject = dialogBuilder.create();
                        //Show the dialog
                        alertDialogObject.show();
                    }
                });

            }


            @Override
            public int getItemCount() {
                return objs.size();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(userID);
    }

    @Override
    public void AssignData(String ObjectID,Boolean isChecked) {
        if(isChecked)
            selectedUsers.add(ObjectID);
        else
            selectedUsers.remove(ObjectID);
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text1;
        TextView text2;
        ImageView img;
        ImageButton imgShare,imgDelete,imgSettings,imgSharedUsers;
        public ViewHolder(View itemView) {
            super(itemView);
            text1 = (TextView) itemView.findViewById(android.R.id.text1);
            text2 = (TextView) itemView.findViewById(android.R.id.text2);
            imgShare= (ImageButton) itemView.findViewById(R.id.btnShare);
            imgDelete= (ImageButton) itemView.findViewById(R.id.btnDelete);
            imgSettings= (ImageButton) itemView.findViewById(R.id.btnSettings);
            imgSharedUsers= (ImageButton) itemView.findViewById(R.id.btnShow);
            img=(ImageView) itemView.findViewById(R.id.imageView);
            text2.setTypeface(Typeface.SANS_SERIF);
            itemView.setPadding(4, 4, 4, 4);
            itemView.setElevation(8);
            itemView.setOnClickListener(this);
                    }

        @Override
        public void onClick(View view) {

                Intent intent = new Intent(Albums.this, DetailedAlbum.class);
                intent.putExtra("AlbumID", objs.get(getPosition()).getObjectId());
                intent.putExtra("objectId",userID);
                intent.putExtra("isCurrentUser",isCurrentUser);
                startActivity(intent);

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_albums, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

    }



    @Override
    public void callDetailedAlbum(String AlbumID) {

    }
}
