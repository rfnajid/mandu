package com.nnn.mandu;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifyPhoneActivity extends AppCompatActivity implements View.OnClickListener{

    Spinner spinner;
    Button button;
    TextView text;
    EditText editPhone;

    List<String> data = new ArrayList<>();
    ArrayAdapter<String> adapter;

    final String ENDPOINT_COUNTRY=Global.URL+"phone_countrycode";
    final String ENDPOINT_POST=Global.URL+"val_phone";

    RequestQueue requestQueue;
    Gson gson;

    String phone="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Verify Phone");
        actionBar.setDisplayHomeAsUpEnabled(true);

        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, data);
        spinner.setAdapter(adapter);

        editPhone = (EditText) findViewById(R.id.edit_phone);

        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(this);

        text = (TextView) findViewById(R.id.text);
        setText();

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        requestCountry();
    }


    //region web service

    void requestCountry(){
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT_COUNTRY, onGetCountrySuccess, onGetCountryError);
        requestQueue.add(request);
    }

    void postData(){
        button.setVisibility(View.INVISIBLE);
        getPhoneNumber();

        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINT_POST, onPostDataSuccess, onPostDataError){
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

    private final Response.Listener<String>  onGetCountrySuccess = new Response.Listener<String>(){
        @Override
        public void onResponse(String response) {
            Log.i("GET COUNTRY", response);

            String json="";
            try {
                JSONObject obj = new JSONObject(response);
                json = obj.getString("ccode");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<CountryCode> countries = Arrays.asList(gson.fromJson(json, CountryCode[].class));

            Log.i("GET COUNTRY", countries.size() + " countries loaded.");
            for (CountryCode c : countries) {
                String s = c.getCountry()+"("+c.getCode()+")";
                Log.i("GET COUNTRY",s+" LOADED");
                data.add(s);
            }
            adapter.notifyDataSetChanged();
        }
    };

    private final Response.ErrorListener onGetCountryError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("GET COUNTRY", error.toString());
            Toast.makeText(getApplicationContext(),"Error "+error.toString(),Toast.LENGTH_SHORT).show();
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

            if(msg.equals("success")){
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
        Intent i = new Intent(getApplicationContext(),VerifyConfirmationActivity.class);
        i.putExtra("phone",phone);
        startActivity(i);
    }

    void postFail(String msg){
        button.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    //endregion web service

    @Override
    public void onClick(View v) {
        if(v.getId()==button.getId()){
            if(editPhone.getText().toString().isEmpty()){
                Toast.makeText(this,"Phone number is empty",Toast.LENGTH_SHORT).show();
                return;
            }
            postData();
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
        SpannableStringBuilder builder = new SpannableStringBuilder();
        ForegroundColorSpan grey = new ForegroundColorSpan(ContextCompat.getColor(this,R.color.darkgrey));
        ForegroundColorSpan primary = new ForegroundColorSpan(ContextCompat.getColor(this,R.color.colorPrimary));

        String string = getString(R.string.confirm_phone);
        String pattern = "Mandu";
        int pIndex= string.indexOf(pattern);
        int pLength = pIndex+pattern.length();

        SpannableString str1= new SpannableString(string);
        str1.setSpan(grey,0,pIndex,0);
        str1.setSpan(primary,pIndex,pLength,0);
        str1.setSpan(grey,pLength,str1.length(),0);
        builder.append(str1);

        text.setText( builder, TextView.BufferType.SPANNABLE);
    }

    void getPhoneNumber(){
        String code=data.get(spinner.getSelectedItemPosition());
        code = code.split("\\+")[1];
        code = code.split("\\)")[0];
        phone= code+editPhone.getText();
    }
}
