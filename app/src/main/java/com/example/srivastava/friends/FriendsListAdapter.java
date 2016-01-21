package com.example.srivastava.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendsListAdapter extends ArrayAdapter<ParseUser> {
        List<ParseUser> mbjects;
    List<ParseUser> mDisplayedValues;
        Context mcontext;
        int res;
        public ReLoad act;
        public FriendsListAdapter(ReLoad re,Context context, int resource, List<ParseUser> objects) {
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
            final ParseUser c=mbjects.get(position);

            TextView txtUserName=(TextView) convertView.findViewById(R.id.txtUserName);
            txtUserName.setText(c.getUsername());
            final View finalConvertView1 = convertView;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    act.callProfile(c.getObjectId());
                    }
                });
                return convertView;
            }
        public interface ReLoad
        {
             public void callProfile(String objID);
        }
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mDisplayedValues = (ArrayList<ParseUser>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<ParseUser> FilteredArrList = new ArrayList<ParseUser>();

                if (mbjects == null) {
                    mbjects = new ArrayList<ParseUser>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mbjects.size();
                    results.values = mbjects;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mbjects.size(); i++) {
                        String data = mbjects.get(i).getUsername();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(mbjects.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}
