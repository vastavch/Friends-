package com.example.srivastava.friends;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;

public class RequestAdding extends Activity implements View.OnClickListener {
    HashMap<String,String> details = new HashMap<>();
    Button button_approve,button_reject,button_ok;
    TextView tv_desc1,tv_desc2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_adding);
        details= (HashMap<String, String>) getIntent().getExtras().get("rcvd_info");
        button_approve= (Button) findViewById(R.id.button_approve);
        button_reject= (Button) findViewById(R.id.button_reject);
        button_ok= (Button) findViewById(R.id.button_ok);
        tv_desc1= (TextView) findViewById(R.id.textView_desc1);
        tv_desc2= (TextView) findViewById(R.id.textView_desc2);
        button_ok.setOnClickListener(this);
        button_approve.setOnClickListener(this);
        button_reject.setOnClickListener(this);
        switch(details.get("notification_id")) {
            case "add_photo": {
                tv_desc1.setText(details.get("sender").toString() + " wants to add a photo,");
                tv_desc2.setText("to the album");
                button_ok.setVisibility(View.GONE);
                break;

            }
            case "Request_approved":{
                tv_desc1.setText(details.get("sender").toString() + " approved your request to add photo,");
                tv_desc2.setText("to the album");
                button_approve.setVisibility(View.GONE);
                button_reject.setVisibility(View.GONE);
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.button_approve:
            {
                HashMap<String, Object> params = new HashMap<String, Object>();
                //params.put("recipientId", objects.get(0).getObjectId());
                params.put("message", "Has approved your request to add photo");
                params.put("album_id",details.get("album_id"));
                params.put("photo_id",details.get("photo_id"));
                params.put("username",details.get("sender"));
                params.put("sender", ParseUser.getCurrentUser().getUsername());
                ParseCloud.callFunctionInBackground("approved",params, new FunctionCallback<String>(){

                            @Override
                            public void done(String object, ParseException e) {
                                if(e==null)
                                {
                                    Log.d("apprvl_response",object);
                                    Toast.makeText(RequestAdding.this,"apprvl_response"+"\t"+object,Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Log.d("apprvl_respon",e.getMessage());
                                    Toast.makeText(RequestAdding.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );

            }
            case R.id.button_reject:
            {
                  break;
            }
            case R.id.button_ok:
            {
                finish();
                break;
            }
        }


    }
}