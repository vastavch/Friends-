package com.example.srivastava.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by srivastava on 12/11/2015.
 */
public class gdAdapter extends ArrayAdapter<ParseObject> {
    List<ParseObject> mbjects;
    Context mcontext;
    int res;
    public ReLoad act;
    public gdAdapter(ReLoad re,Context context, int resource, List<ParseObject> objects) {
        super(context, resource, objects);
        mbjects=objects;
        mcontext=context;
        res=resource;
        act=re;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflate= (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView= inflate.inflate(res,parent,false);
        }
//        final ParseObject c=mbjects.get(position);
//        TextView txtAlbum=(TextView) convertView.findViewById(R.id.txtAlbum);
//        txtAlbum.setText(c.getString("AlbumName"));
//        ImageView imgCover=(ImageView) convertView.findViewById(R.id.imgIcon);
//        ParseObject img=c.getParseObject("AlbumCover");
//        try {
//            if(img!=null) {
//                Picasso.with(convertView.getContext()).load(img.getParseFile("Image").getFile()).error(R.drawable.avatar).into(imgCover);
//            }
//            else{
//                Picasso.with(convertView.getContext()).load(R.drawable.avatar).into(imgCover);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        final View finalConvertView1 = convertView;
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              act.callDetailedAlbum(c.getObjectId());
//            }
//        });
        return convertView;
    }
    public interface ReLoad
    {
        public void callDetailedAlbum(String AlbumID);
    }
}
