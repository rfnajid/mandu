package com.nnn.mandu;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifyConfirmationActivity extends AppCompatActivity implements View.OnClickListener{

    Button button;
    TextView resend,text;
    EditText editCode;

    final String ENDPOINT=Global.URL+"val_phone";

    RequestQueue requestQueue;
    Gson gson;

    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_confirmation);

        Intent i = getIntent();
        phone = i.getStringExtra("phone");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Verify Phone");
        actionBar.setDisplayHomeAsUpEnabled(true);

        editCode = (EditText) findViewById(R.id.edit_code);

        text = (TextView) findViewById(R.id.text);
        resend = (TextView) findViewById(R.id.text_resend);
        resend.setOnClickListener(this);
        setText();

        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }


    //region web service

    void postResend(){
        button.setVisibility(View.INVISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINT, onResendSuccess, onResendError){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("session_id", Global.getSessionId());
                params.put("phone_no", phone);
                return params;
            }
        };
        requestQueue.add(request);
    }

    void postData(){
        button.setVisibility(View.INVISIBLE);
        final String code = editCode.getText()+"";

        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINT, onPostDataSuccess, onPostDataError){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("session_id", Global.getSessionId());
                params.put("val_code",code);
                return params;
            }
        };
        requestQueue.add(request);
    }

    private final Response.Listener<String>  onResendSuccess = new Response.Listener<String>(){
        @Override
        public void onResponse(String response) {
            Log.i("POST PHONE", response);
            String msg="Failed";
            try {
                JSONObject json = new JSONObject(response);
                msg = json.getString("response");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(msg.equals("success")){
                resendSuccess();
            }else{
                resendFail(msg);
            }
        }
    };

    private final Response.ErrorListener onResendError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("POST PHONE", error.toString());
            resendFail("Unexpected Error");
        }
    };

    private final Response.Listener<String>  onPostDataSuccess = new Response.Listener<String>(){
        @Override
        public void onResponse(String response) {
            Log.i("POST PHONE", response);
            String msg="Failed";
            try {
                JSONObject json = new JSONObject(response);
                msg = json.getString("response");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(msg.contains("success")){
                postSuccess();
            }else{
                postFail(msg);
            }
        }
    };

    private final Response.ErrorListener onPostDataError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("POST PHONE", error.toString());
            postFail("Unexpected Error");
        }
    };

    void postSuccess(){
        Intent i = new Intent(getApplicationContext(),CurrencyActivity.class);
        startActivity(i);
        finish();
    }

    void postFail(String msg){
        button.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    void resendSuccess(){
        resendFail("We've been resend the code");
    }

    void resendFail(String msg){
        button.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    //endregion web service

    @Override
    public void onClick(View v) {
        if(v.getId()==button.getId()){
            if(editCode.getText().toString().isEmpty()){
                Toast.makeText(this,"Code can't be empty",Toast.LENGTH_SHORT).show();
                return;
            }
            postData();
        }else if(v.getId()==resend.getId()){
            postResend();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    void setText(){

        //text confimration
        SpannableStringBuilder builder = new SpannableStringBuilder();
        ForegroundColorSpan grey = new ForegroundColorSpan(ContextCompat.getColor(this,R.color.darkgrey));
        ForegroundColorSpan primary = new ForegroundColorSpan(ContextCompat.getColor(this,R.color.colorPrimary));

        String string = getString(R.string.confirm_code);
        String pattern = "Mandu";
        int pIndex= string.indexOf(pattern);
        int pLength = pIndex+pattern.length();

        SpannableString str1= new SpannableString(string);
        str1.setSpan(grey,0,pIndex,0);
        str1.setSpan(primary,pIndex,pLength,0);
        str1.setSpan(grey,pLength,str1.length(),0);
        builder.append(str1);

        text.setText( builder, TextView.BufferType.SPANNABLE);


        //text resend
        SpannableStringBuilder builder2 = new SpannableStringBuilder();
        string = getString(R.string.resend);
        pattern = "Resend Code";
        pIndex=string.indexOf(pattern);

        str1 = new SpannableString(string);
        str1.setSpan(grey,0,pIndex,0);
        str1.setSpan(primary,pIndex,str1.length(),0);

        builder2.append(str1);
        resend.setText(builder2, TextView.BufferType.SPANNABLE);
    }
}
