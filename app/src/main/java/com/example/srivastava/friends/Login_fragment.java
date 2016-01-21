package com.example.srivastava.friends;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Login_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Login_fragment extends android.app.Fragment {

    EditText txtUsername,txtPassWord;
    private OnFragmentInteractionListener mListener;
    public Login_fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_fragment, container, false);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getPackageName()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }






    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
        if (currentUser != null) {
            mListener.goToProfile();
            return;
        }
        txtUsername = (EditText) getActivity().findViewById(R.id.txtUserName);
        txtPassWord=(EditText) getActivity().findViewById(R.id.txtPassword);

        getActivity().findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user, password;
                user = txtUsername.getText().toString();
                password = txtPassWord.getText().toString();
                if (!user.equals(null) && !user.equals("") && !password.equals(null) && !password.equals("")) {
                    ParseUser.logInInBackground(user, password, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                mListener.goToProfile();
                                return;
                            } else {
                                mListener.showErrorMessage("Please enter correct user name and password");
                                return;
                            }
                        }
                    });
                } else {
                    mListener.showErrorMessage("Please enter correct user name and password");
                }
            }
        });
        getActivity().findViewById(R.id.btnCreateNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToSignUpFragment();
            }
        });

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void goToProfile();
        public void showErrorMessage(String msg);
        public void goToSignUpFragment();
    }

}

