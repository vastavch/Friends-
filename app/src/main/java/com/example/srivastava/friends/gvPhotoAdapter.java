package com.example.srivastava.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.List;


public class gvPhotoAdapter  extends ArrayAdapter<ParseObject> {
    List<ParseObject> mbjects;
    Context mcontext;
    int res;
    public ReLoad act;
    public gvPhotoAdapter(ReLoad re,Context context, int resource, List<ParseObject> objects) {
        super(context, resource, objects);
        mbjects=objects;
        mcontext=context;
        res=resource;
        act=re;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflate= (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView= inflate.inflate(res,parent,false);
        }
        final ParseObject c=mbjects.get(position);

        ImageView imgCover=(ImageView) convertView.findViewById(R.id.imgPhoto);
       try {
            Picasso.with(convertView.getContext()).load(c.getParseFile("Image").getFile()).error(R.drawable.avatar).into(imgCover);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final View finalConvertView1 = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.showAlertDialog(position);
            }
        });
        return convertView;
    }
    public interface ReLoad
    {
        public void showAlertDialog(int position);
    }
}
