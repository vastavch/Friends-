package com.example.srivastava.friends;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link profile_UI_home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link profile_UI_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile_UI_home extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView txtUsername,txtGender;
    ImageView imgAvatar;
    static int GET_FROM_GALLERY=1;
    EditText txtEditUserName;
    ParseUser user;
    boolean isCurrentUser=false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile_UI_home.
     */
    // TODO: Rename and change types and number of parameters
    public static profile_UI_home newInstance(String param1, String param2) {
        profile_UI_home fragment = new profile_UI_home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public profile_UI_home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile__ui_home, container, false);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //        String userdata=getIntent().getStringExtra("user");
//        if(userdata==null)
//        user=ParseUser.getCurrentUser();
//        else {
//            ParseQuery query = new ParseQuery("Users");
//            query.whereEqualTo("ObjectID",userdata);
//            query.findInBackground(new FindCallback() {
//                @Override
//                public void done(List objects, ParseException e) {
//                    for(Object obj: objects){
//                        user=(ParseUser)obj;
//                    }
//                }
//
//                @Override
//                public void done(Object o, Throwable throwable) {
//
//                }
//            });
//        }
        try {
            if(getActivity().getIntent().getStringExtra("objectId").equals(ParseUser.getCurrentUser().getObjectId())){
                user=ParseUser.getCurrentUser();
                isCurrentUser=true;
            }
            else{
                ParseQuery<ParseUser> query= ParseQuery.getQuery("_User");
                query.whereEqualTo("objectId",getActivity().getIntent().getStringExtra("objectId"));
                user=query.getFirst();
                isCurrentUser=false;
            }

        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        txtUsername=(TextView) getActivity().findViewById(R.id.txtUserName);
        txtGender=(TextView) getActivity().findViewById(R.id.txtGender);
        txtEditUserName=(EditText) getActivity().findViewById(R.id.txtEditUserName);
        imgAvatar = (ImageView) getActivity().findViewById(R.id.imgAvatar);
        ImageView imgAlbums=(ImageView) getActivity().findViewById(R.id.img_thumbnail);
        Picasso.with(getActivity()).load(R.drawable.albumcover).into(imgAlbums);
        ImageView imgFriends=(ImageView) getActivity().findViewById(R.id.img_thumbnail1);
        Picasso.with(getActivity()).load(R.drawable.friends).into(imgFriends);
        final Spinner gender =(Spinner) getActivity().findViewById(R.id.spGender);
        if(!isCurrentUser){
            txtUsername.setEnabled(false);
            txtGender.setEnabled(false);
            imgAvatar.setEnabled(false);
        }
        try {
            ParseFile parseFile= user.getParseFile("avatar");
            if(parseFile!=null)
                Picasso.with(getActivity()).load(parseFile.getFile()).error(R.drawable.avatar).resize(300,300).centerInside().into(imgAvatar);
            else
                Picasso.with(getActivity()).load(R.drawable.avatar).resize(300, 300).centerInside().into(imgAvatar);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                txtUsername.setVisibility(View.GONE);
                txtEditUserName.setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.btnGo).setVisibility(View.VISIBLE);
            }
        });
        txtGender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                txtGender.setVisibility(View.GONE);
                gender.setVisibility(View.VISIBLE);
            }
        });
        txtUsername.setText(user.getString("username"));
        Profile.user_name=user.getString("username");
        txtGender.setText(user.getString("gender"));
        getActivity().findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUsername.setVisibility(View.VISIBLE);
                txtEditUserName.setVisibility(View.GONE);
                getActivity().findViewById(R.id.btnGo).setVisibility(View.GONE);
                ParseUser user = ParseUser.getCurrentUser();
                user.put("username", txtEditUserName.getText().toString());
                try {
                    user.save();
                    txtUsername.setText(txtEditUserName.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        String[] items = new String[] {"Male","Female"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                txtGender.setVisibility(View.VISIBLE);
                gender.setVisibility(View.GONE);
                String selectedItem = parent.getItemAtPosition(position).toString();
                txtGender.setText(selectedItem);
                ParseUser user =ParseUser.getCurrentUser();
                user.put("gender",selectedItem);
                try {
                    user.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        getActivity().findViewById(R.id.fmAlbums).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getBaseContext(), Albums.class);
                intent.putExtra("objectId", user.getObjectId());
                intent.putExtra("isCurrentUser",isCurrentUser);
                startActivity(intent);
            }
        });
        getActivity().findViewById(R.id.fmFriends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity().getBaseContext(),FriendsList.class);
                intent.putExtra("objectId", user.getObjectId());
                intent.putExtra("isCurrentUser",isCurrentUser);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            final Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmapLogo= BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapLogo.compress(Bitmap.CompressFormat.PNG,0, stream);
            byte[] image = stream.toByteArray();
            String filename="profile.png";
            final ParseFile file = new ParseFile(filename, image);
            try {
                file.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ParseUser user= ParseUser.getCurrentUser();
            user.put("avatar",file);
            try {
                user.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Picasso.with(getActivity()).load(selectedImage).into(imgAvatar);

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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
