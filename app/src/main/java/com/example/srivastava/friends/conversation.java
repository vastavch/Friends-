package com.example.srivastava.friends;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link conversation.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link conversation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class conversation extends android.support.v4.app.Fragment implements MessageAdapter.Reload {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment conversation.
     */
    // TODO: Rename and change types and number of parameters

    private String mParam1;
    private String mParam2;
    private EditText msgField;
    private ListView msgListView;
    private Button btnSend;
    private ParseUser friend;
    private MessageAdapter msgAdapter;
    private List<Message> messages;
    private OnFragmentInteractionListener mListener;
    public static conversation newInstance(String param1, String param2) {
        conversation fragment = new conversation();

        return fragment;
    }

    public conversation() {
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
        // Inflate the layout for this fragmentView v = inflater.inflate(R.layout.fragment_conversation, container, false);
        View v = inflater.inflate(R.layout.fragment_conversation, container, false);

        msgField = (EditText) v.findViewById(R.id.messageBodyField);
        msgListView = (ListView) v.findViewById(R.id.listMessages);
        messages = new ArrayList<>();
        loadConversation();
        btnSend = (Button) v.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                loadConversation_reload();
            }
        });
        return v;
    }
    private void loadConversation() {
        messages = new ArrayList<>();
        msgAdapter = new MessageAdapter(this,(conversationActivity) getActivity(), messages);
        msgListView.setAdapter(msgAdapter);
        ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
        friendQuery.whereEqualTo("username",getActivity().getIntent().getStringExtra("username"));

        friendQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {

                if (e == null) {
                    if (parseUsers.size() > 0) {
                        friend = parseUsers.get(0);
                        ParseQuery<Message> sentMessagesQuery = ParseQuery.getQuery(Message.class);
                        sentMessagesQuery.whereEqualTo("senderId", ParseUser.getCurrentUser().getObjectId());
                        sentMessagesQuery.whereEqualTo("receiverId", friend.getObjectId());
                        sentMessagesQuery.whereEqualTo("isSenderDeleted",false);

                        ParseQuery<Message> receivedMessagesQuery = ParseQuery.getQuery(Message.class);
                        receivedMessagesQuery.whereEqualTo("senderId", friend.getObjectId());
                        receivedMessagesQuery.whereEqualTo("receiverId", ParseUser.getCurrentUser().getObjectId());
                        receivedMessagesQuery.whereEqualTo("isReceiverDeleted",false);
                        // Combine the queries
                        List<ParseQuery<Message>> queries = new ArrayList<>();
                        queries.add(sentMessagesQuery);
                        queries.add(receivedMessagesQuery);

                        // Get the messages
                        ParseQuery<Message> mainQuery = ParseQuery.or(queries);
                        mainQuery.orderByAscending("timeSent");
                        mainQuery.findInBackground(new FindCallback<Message>() {

                            @Override
                            public void done(List<Message> messages, ParseException e) {
                                if (e == null) {
                                    msgAdapter.addMessages(messages);
                                } else {
                                    showConversationFetchError(e);
                                }
                            }
                        });
                    } else {
                        showConversationFetchError(e);
                    }
                }
            }
        });
    }
    private void showConversationFetchError(ParseException e) {
        Toast.makeText(getActivity(), "Error fetching conversation", Toast.LENGTH_LONG).show();

        // Go back
        getFragmentManager().popBackStack();
    }

    private void sendMessage() {
        String messageBody = msgField.getText().toString().trim();

        if (!messageBody.isEmpty()) {

            // Empty the field
            msgField.setText("");


            String receiverId = friend.getObjectId();
            final String senderId =  ParseUser.getCurrentUser().getObjectId();


            // Create message object
            Message message = Message.newInstance(senderId, receiverId, messageBody);

            // Create Pubnub object
            Pubnub pubnub = new Pubnub(getString(R.string.pubnub_publish_key),
                    getString(R.string.pubnub_subscribe_key));

            // Send the message
            sendMessage(getActivity(), pubnub, message, msgAdapter);
            HashMap<String, Object> params = new HashMap<String, Object>();
            //params.put("recipientId", objects.get(0).getObjectId());
            params.put("message", ParseUser.getCurrentUser().getUsername() + " has sent a message to you!");
            // params.put("album_id", );
            //params.put("photo_id", PhotoId);

            params.put("username",friend.getString("username") );
            params.put("sender", ParseUser.getCurrentUser().getUsername());

            ParseCloud.callFunctionInBackground("messaging", params, new FunctionCallback<String>() {
                @Override
                public void done(String object, com.parse.ParseException e) {
                    if (e == null) {

                        ParseObject obj1 = new ParseObject("Notifications");
                        obj1.put("SentTo", friend.getString("username"));
                        obj1.put("CreatedBY", ParseUser.getCurrentUser().getUsername());
                        obj1.put("Message", ParseUser.getCurrentUser().getUsername() + " has sent you a message");
                        try {
                            obj1.save();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Please enter message in field", Toast.LENGTH_LONG).show();
        }
    }
    public void sendMessage(final Activity activity, final Pubnub pubnub, final Message message, final MessageAdapter msgAdapter) {
        final String receiverId = message.getReceiverId();
        message.setTimeSent(new Date());
        message.put("isSenderDeleted",false);
        message.put("isReceiverDeleted",false);
        message.put("isReceiverRead",false);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    JSONObject messageObject = message.toJSON();

                    try {
                        pubnub.subscribe(receiverId, new Callback() {
                            @Override
                            public void successCallback(String channel, Object message) {
                                super.successCallback(channel, message);
                            }

                            @Override
                            public void errorCallback(String channel, PubnubError error) {
                                super.errorCallback(channel, error);
                            }
                        });
                    } catch (PubnubException pubNubException) {
                        pubNubException.printStackTrace();
                    }

                    Callback messageCallback = new Callback() {
                        public void successCallback(String channel, Object response) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Add the message to the adapter
                                    msgAdapter.addMessage(message);

                                }
                            });

                            // Unsubscribe from the channel once the message is sent
                            pubnub.unsubscribe(receiverId);
                        }

                        public void errorCallback(String channel, PubnubError error) {

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Error sending message", Toast.LENGTH_LONG).show();

                                }
                            });

                            // Unsubscribe from the channel once the message is sent
                            pubnub.unsubscribe(receiverId);
                        }
                    };

                    pubnub.publish(receiverId, messageObject, messageCallback);
                } else {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Error sending message", Toast.LENGTH_LONG).show();


                        }
                    });
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

    @Override
    public void loadConversation_reload() {
        loadConversation();
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
