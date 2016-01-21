package com.example.srivastava.friends;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailedAlbum extends Activity implements gvPhotoAdapter.ReLoad,UserSelectionAdapter.ReLoad {

    List<ParseObject> photos;
    List<ParseUser> users;
    List<String> selectedUsers;
    String AlbumID,PhotoId,AlbumName,userID;
    int pos=0,GET_FROM_GALLERY=1;
    GridView gv;
    boolean isCurrentUser=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_album);

        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle("Photos");

        AlbumID =getIntent().getStringExtra("AlbumID");
        userID=getIntent().getStringExtra("objectId");
        isCurrentUser=getIntent().getBooleanExtra("isCurrentUser", true);
        ParseQuery obj=ParseQuery.getQuery("User_Album_Sharing");
        obj.whereEqualTo("AlbumID",ParseObject.createWithoutData("Albums",AlbumID));
        obj.whereEqualTo("SharedUser", ParseObject.createWithoutData("_User", userID));
        ParseObject po=new ParseObject("User_Album_Sharing");
        try {
            po =obj.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        loadData(AlbumID);

        if(!isCurrentUser || po==null)
            findViewById(R.id.fab).setVisibility(View.GONE);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

            }
        });
    }

    private void loadData(String albumID) {
        photos=new ArrayList<ParseObject>();
        photos=getPhotosObjects(albumID);
        ParseQuery obj=ParseQuery.getQuery("Albums");
        ImageView img=(ImageView) findViewById(R.id.imageView2);
        obj.whereEqualTo("objectId",albumID);
        ParseObject po=ParseObject.create("Albums");
        try {
            po=obj.getFirst();
            if(po!=null && po.getParseObject("AlbumCover")!=null && !po.getParseObject("AlbumCover").getObjectId().equals(null)){
                img.setVisibility(View.VISIBLE);
                obj=ParseQuery.getQuery("Photos");
                obj.whereEqualTo("objectId",po.getParseObject("AlbumCover").getObjectId());
                po=ParseObject.create("Photos");
                po=obj.getFirst();
                Picasso.with(DetailedAlbum.this).load(po.getParseFile("Image").getFile()).into(img);
            }
            else{
                img.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        gv=(GridView) findViewById(R.id.gridView);
        gv.setAdapter(new CustomAdapter(this,photos));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            final Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmapLogo= BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapLogo.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] image = stream.toByteArray();
            String filename="profile.png";
            ParseQuery query=ParseQuery.getQuery("Albums");
            query.whereEqualTo("objectId",AlbumID);
            ParseObject objUser=ParseObject.create("_User");
            try {
                ParseObject obj=query.getFirst();
                AlbumName=obj.getString("AlbumName");
                query=ParseQuery.getQuery("_User");
                query.whereEqualTo("objectId",obj.getParseUser("CreatedBy").getObjectId());
                objUser=query.getFirst();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            final ParseFile file = new ParseFile(filename, image);
            final ParseObject finalObjUser1 = objUser;
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    ParseObject obj = new ParseObject("Photos");
                    obj.put("Image", file);
                    obj.put("CreatedBy", ParseUser.getCurrentUser());
                    obj.put("AlbumID", ParseObject.createWithoutData("Albums", AlbumID));
                    if(!finalObjUser1.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                        obj.put("SetVisible",false);
                    }
                    else{
                        obj.put("SetVisible",true);
                    }
                    try {
                        obj.save();
                        PhotoId =obj.getObjectId();
                        loadData(AlbumID);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            if(!objUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                //params.put("recipientId", objects.get(0).getObjectId());
                final ParseObject finalObjUser = objUser;
                params.put("message",  ParseUser.getCurrentUser().getUsername() + " wants to add a photo. To approve please check you album " + AlbumName);
                params.put("album_id", AlbumID);
                params.put("photo_id", PhotoId);
                params.put("username", finalObjUser.getString("username"));
                params.put("sender", ParseUser.getCurrentUser().getUsername());

                ParseCloud.callFunctionInBackground("photosharing", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String object, com.parse.ParseException e) {
                        if (e == null) {
                            Toast.makeText(DetailedAlbum.this, "Photo has been added successfully. Photo will be visible when owner approves it", Toast.LENGTH_LONG).show();
                            ParseObject obj = new ParseObject("Notifications");
                            obj.put("SentTo", finalObjUser.getString("username"));
                            obj.put("CreatedBY", ParseUser.getCurrentUser().getUsername());
                            obj.put("Message", ParseUser.getCurrentUser().getUsername() + " wants to add a photo. To approve please check you album " + AlbumName);
                            try {
                                obj.save();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
        }

    }
    private List<ParseObject> getPhotosObjects(String albumID) {
        photos=new ArrayList<ParseObject>();
        ParseQuery query=new ParseQuery("Photos");
        query.whereEqualTo("AlbumID", ParseObject.createWithoutData("Albums", albumID));
        if(!isCurrentUser){
            query.whereEqualTo("SetVisible",true);
        }
        try {
            photos=query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return photos;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void showAlertDialog(final int position) {
        pos=position;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailedAlbum.this);
        alertDialogBuilder.setTitle("SlideShow..");
        LayoutInflater li = LayoutInflater.from(DetailedAlbum.this);
        final View dialogView = li.inflate(R.layout.activity_slide_show, null);
        ImageButton btnPrev=(ImageButton) dialogView.findViewById(R.id.imgPrev);
        ImageButton btnNext=(ImageButton) dialogView.findViewById(R.id.imgNext);
        final ImageView imgView=(ImageView) dialogView.findViewById(R.id.imgPhotoSlideShow);
        try {
            Picasso.with(getBaseContext()).load(photos.get(pos).getParseFile("Image").getFile()).error(R.drawable.avatar).into(imgView);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(pos>=0) {
                        Picasso.with(getBaseContext()).load(photos.get(pos--).getParseFile("Image").getFile()).error(R.drawable.avatar).into(imgView);

                    }
                    else
                    {
                        pos=photos.size()-1;
                        Picasso.with(getBaseContext()).load(photos.get(pos).getParseFile("Image").getFile()).error(R.drawable.avatar).into(imgView);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(pos<photos.size()){
                        Picasso.with(getBaseContext()).load(photos.get(pos++).getParseFile("Image").getFile()).error(R.drawable.avatar).into(imgView);
                    }
                    else{
                        pos=0;
                        Picasso.with(getBaseContext()).load(photos.get(pos).getParseFile("Image").getFile()).error(R.drawable.avatar).into(imgView);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialogBuilder.setView(dialogView);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void AssignData(String ObjectID,Boolean isChecked) {
        if(isChecked)
            selectedUsers.add(ObjectID);
        else
            selectedUsers.remove(ObjectID);
    }

    public class CustomAdapter extends BaseAdapter {

        List<ParseObject> lstImages;
        Context context;
        private LayoutInflater inflater = null;

        public CustomAdapter(DetailedAlbum mainActivity, List<ParseObject> lstImages) {
            // TODO Auto-generated constructor stub
            this.lstImages = lstImages;
            context = mainActivity;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return lstImages.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {
            ImageView img;
            ImageButton btnDelete;
            Button btnMakeascover;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final Holder holder = new Holder();
            View rowView;

            rowView = getLayoutInflater().inflate(R.layout.activity_gridview_item, parent, false);
            holder.img = (ImageView) rowView.findViewById(R.id.img_thumbnail);
            holder.btnDelete = (ImageButton) rowView.findViewById(R.id.btndelete);
            holder.btnMakeascover = (Button) rowView.findViewById(R.id.btnMakeitCover);
            ParseQuery query=ParseQuery.getQuery("Albums");
            query.whereEqualTo("objectId", AlbumID);
            String user= null;
            try {
                user = query.getFirst().getParseUser("CreatedBy").getObjectId();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(!isCurrentUser || !user.equals(ParseUser.getCurrentUser().getObjectId())){
                holder.btnDelete.setVisibility(View.GONE);
                holder.btnMakeascover.setVisibility(View.GONE);
            }

            if (lstImages.get(position).getParseFile("Image") != null)
                try {
                    Picasso.with(DetailedAlbum.this).load(lstImages.get(position).getParseFile("Image").getFile())
                            .error(R.drawable.avatar)
                            .placeholder(R.drawable.avatar).into(holder.img);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            else
                Picasso.with(DetailedAlbum.this).load(R.drawable.avatar)
                        .into(holder.img);

                if(!lstImages.get(position).getParseObject("CreatedBy").getObjectId().equals(user) && user.equals(ParseUser.getCurrentUser().getObjectId()) && !lstImages.get(position).getBoolean("SetVisible")){
                    holder.btnMakeascover.setText("Approve");
                }

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        lstImages.get(position).delete();
                        loadData(AlbumID);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.btnMakeascover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.btnMakeascover.getText().equals("Approve")) {
                        ParseObject obj = ParseObject.create("Photos");
                        obj = lstImages.get(position);
                        obj.put("SetVisible", true);
                        try {
                            obj.save();
                            holder.btnMakeascover.setText("Make as cover");
                            Toast.makeText(DetailedAlbum.this, "Now the photo will be visible to other users", Toast.LENGTH_LONG).show();
                            loadData(AlbumID);
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            //params.put("recipientId", objects.get(0).getObjectId());
                            params.put("message", ParseUser.getCurrentUser().getUsername() + " has approved your request to add photo");
                            // params.put("album_id", );
                            //params.put("photo_id", PhotoId);

                            params.put("username", lstImages.get(position).getParseObject("CreatedBy").getString("username"));
                            params.put("sender", ParseUser.getCurrentUser().getUsername());

                            ParseCloud.callFunctionInBackground("approved", params, new FunctionCallback<String>() {
                                @Override
                                public void done(String object, com.parse.ParseException e) {
                                    if (e == null) {

                                        ParseObject obj1 = new ParseObject("Notifications");
                                        obj1.put("SentTo", lstImages.get(position).getParseObject("CreatedBy").getString("username"));
                                        obj1.put("CreatedBY", userID);
                                        obj1.put("Message", "photo has been approved by " + ParseUser.getCurrentUser().getUsername());
                                        try {
                                            obj1.save();
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            });
                            Toast.makeText(DetailedAlbum.this, "photo has been approved", Toast.LENGTH_LONG).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        ParseQuery<ParseObject> albumObj = ParseQuery.getQuery("Albums");
                        albumObj.whereEqualTo("objectId", AlbumID);
                        ParseObject obj = null;
                        try {
                            obj = albumObj.getFirst();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        obj.put("AlbumCover", ParseObject.createWithoutData("Photos", lstImages.get(position).getObjectId()));
                        try {
                            obj.save();
                            Toast.makeText(DetailedAlbum.this, "Album cover has been updated successfully", Toast.LENGTH_LONG).show();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                        });
            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showAlertDialog(position);
                }
            });

            return rowView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }}
