package com.example.srivastava.friends;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link messages_user.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link messages_user#newInstance} factory method to
 * create an instance of this fragment.
 */
public class messages_user extends android.support.v4.app.Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "FriendsFragment";
    private ListView friendsListView;
    private ArrayAdapter<String> friendsAdapter;
    private ArrayList<String> friendNames;

    private OnFragmentInteractionListener mListener;


    // TODO: Rename and change types and number of parameters
    public static messages_user newInstance() {
        messages_user fragment = new messages_user();
        return fragment;
    }

    public messages_user() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messages_user, container, false);
        friendsListView = (ListView) v.findViewById(R.id.friends_list_view);
        populateListWithUsers();
        return v;
    }
    private void populateListWithUsers() {
        friendNames = new ArrayList<>();
        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("objectId", currentUserId);
        query.whereEqualTo("AllowMessages",true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < parseUsers.size(); i++) {
                        friendNames.add(parseUsers.get(i).getUsername().toString());
                    }
                    friendsAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                            R.layout.activity_user_list_item, friendNames);
                    friendsListView.setAdapter(friendsAdapter);
                    friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                            ((Profile) getActivity()).openConversationThread(friendNames.get(i));
                        }
                    });
                } else {
                    Log.e(TAG, "Error finding users:" + e.getMessage(), e);
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
