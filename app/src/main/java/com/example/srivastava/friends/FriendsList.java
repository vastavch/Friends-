package com.example.srivastava.friends;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class FriendsList extends Activity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener  {

    List<ParseUser> objs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle("Friends List..");

        SearchView searchView = (SearchView) findViewById(R.id.srView);

        //Sets the default or resting state of the search field. If true, a single search icon is shown by default and
        // expands to show the text field and other buttons when pressed. Also, if the default state is iconified, then it
        // collapses to that state when the close button is pressed. Changes to this property will take effect immediately.
        //The default value is true.
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        loadData("");

    }
    public void loadData(String username){
        ParseQuery<ParseUser> query=ParseQuery.getQuery("_User");
        query.whereNotEqualTo("objectId",getIntent().getStringExtra("objectId"));
        query.whereEqualTo("AllowUserListing",true);
        if(!username.equals("")){
            query.whereContains("username",username);
        }
        try {
            objs= query.find();
            RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
                @Override
                public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
                    return new ViewHolder(getLayoutInflater().inflate(R.layout.activity_gd_view_adapter, parent, false));
                }

                @Override
                public void onBindViewHolder(ViewHolder viewHolder, int position) {
                    viewHolder.text1.setText(objs.get(position).getString("username"));
                    if(objs.get(position).getParseFile("avatar")!=null)
                        Picasso.with(FriendsList.this).load(objs.get(position).getParseFile("avatar").getUrl())
                                .error(R.drawable.avatar)
                                .placeholder(R.drawable.avatar)
                                .resize(200, 200).centerInside().transform(new Transformation() {
                            @Override
                            public Bitmap transform(Bitmap source) {
                                int size = Math.min(source.getWidth(), source.getHeight());

                                int x = (source.getWidth() - size) / 2;
                                int y = (source.getHeight() - size) / 2;

                                Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                                if (squaredBitmap != source) {
                                    source.recycle();
                                }

                                Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

                                Canvas canvas = new Canvas(bitmap);
                                Paint paint = new Paint();
                                BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                                paint.setShader(shader);
                                paint.setAntiAlias(true);

                                float r = size/2f;
                                canvas.drawCircle(r, r, r, paint);

                                squaredBitmap.recycle();
                                return bitmap;
                            }

                            @Override
                            public String key() {
                                return "circle";
                            }
                        }).into(viewHolder.img);
                    else
                        Picasso.with(FriendsList.this).load(R.drawable.avatar)
                                .resize(200, 200).centerInside().transform(new Transformation() {
                            @Override
                            public Bitmap transform(Bitmap source) {
                                int size = Math.min(source.getWidth(), source.getHeight());

                                int x = (source.getWidth() - size) / 2;
                                int y = (source.getHeight() - size) / 2;

                                Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                                if (squaredBitmap != source) {
                                    source.recycle();
                                }

                                Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

                                Canvas canvas = new Canvas(bitmap);
                                Paint paint = new Paint();
                                BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                                paint.setShader(shader);
                                paint.setAntiAlias(true);

                                float r = size / 2f;
                                canvas.drawCircle(r, r, r, paint);

                                squaredBitmap.recycle();
                                return bitmap;
                            }

                            @Override
                            public String key() {
                                return "circle";
                            }
                        }).into(viewHolder.img);


                }


                @Override
                public int getItemCount() {
                    return objs.size();
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
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
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        loadData(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        loadData(newText);
        return false;
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text1;
        ImageView img;
        public ViewHolder(View itemView) {
            super(itemView);
            text1 = (TextView) itemView.findViewById(android.R.id.text1);
            img=(ImageView) itemView.findViewById(R.id.imageView);
            itemView.findViewById(R.id.btnShare).setVisibility(View.GONE);
            itemView.findViewById(R.id.btnShow).setVisibility(View.GONE);
            itemView.findViewById(R.id.btnSettings).setVisibility(View.GONE);
            itemView.findViewById(R.id.btnDelete).setVisibility(View.GONE);
            itemView.setPadding(4, 4, 4, 4);
            itemView.setElevation(8);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            callProfile(objs.get(getPosition()).getObjectId());
        }

    }
    public void callProfile(String objID) {
        Intent intent=new Intent(FriendsList.this,Profile.class);
        intent.putExtra("objectId",objID);
        startActivity(intent);
    }
}
