package com.example.srivastava.friends;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class MessageAdapter extends BaseAdapter {

    // Direction of message
    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;
    Reload reload;

    private List<Message> messages;
    private LayoutInflater inflater;

    public MessageAdapter(conversation con,conversationActivity msgActivity, List<Message> messages) {
        this.messages = messages;
        inflater = LayoutInflater.from(msgActivity);
        reload= (Reload) con;
    }

    public void addMessage(Message msg) {
        messages.add(msg);
        notifyDataSetChanged();
    }

    public void addMessages(List<Message> messageList) {
        this.messages.addAll(messageList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int direction = getItemViewType(position);
        final Message message = messages.get(position);
        int res = 0;
        String senderid="";
        if (direction == DIRECTION_INCOMING) {
            res = R.layout.activity_message_left;
            senderid=message.getSenderId();
        } else if (direction == DIRECTION_OUTGOING) {
            res = R.layout.activity_message_right;
            senderid=message.getSenderId();
        }
        convertView = inflater.inflate(res, parent, false);


        TextView txtSender = (TextView) convertView.findViewById(R.id.txtSender);
        txtSender.setText(message.getUserName(senderid));
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        txtMessage.setText(message.getMessageBody());

        TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
        txtDate.setText(message.getFormattedTimeSent());
        if (direction == DIRECTION_OUTGOING) {
            ImageButton imgbtn = (ImageButton) convertView.findViewById(R.id.btnDelete);
            imgbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(!message.getBoolean("isReceiverDeleted")) {
                            message.put("isSenderDeleted", true);
                            message.save();
                        }
                        else{
                            message.delete();
                        }
                        reload.loadConversation_reload();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            if(!message.getBoolean("isReceiverRead")){
                ImageView img=(ImageView) convertView.findViewById(R.id.imgTick);
                img.setVisibility(View.VISIBLE);
                message.put("isReceiverRead", true);
                try {
                    message.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            ImageButton imgbtn = (ImageButton) convertView.findViewById(R.id.btnDel);
            imgbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(!message.getBoolean("isSenderDeleted")) {
                            message.put("isReceiverDeleted", true);
                            message.save();
                        }
                        else
                        {
                            message.delete();
                        }
                        reload.loadConversation_reload();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        return getMessageDirection(msg);
    }

    private int getMessageDirection(Message msg) {
        if (msg.getSenderId().equals(ParseUser.getCurrentUser().getObjectId())) {
            return DIRECTION_OUTGOING;
        }

        return DIRECTION_INCOMING;
    }
    public interface Reload{
        public void loadConversation_reload();
    }
}
