package com.nnn.mandu;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class CurrencyActivity extends AppCompatActivity implements View.OnClickListener{

    Button button;
    RecyclerView recycler;

    CurrencyAdapter adapter;

    List<Currency> data = new ArrayList<Currency>();

    final String ENDPOINT_CURRENCY=Global.URL+"currency";
    final String ENDPOINT_POST=Global.URL+"curr_selected";

    RequestQueue requestQueue;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select Currency");
        actionBar.setDisplayHomeAsUpEnabled(true);

        adapter = new CurrencyAdapter(this,data);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.getItemAnimator().setChangeDuration(0);
        recycler.setAdapter(adapter);

        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        requestCurrency();
    }

    //region web service

    void requestCurrency(){
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT_CURRENCY, onGetCurrencySuccess, onGetCurrencyError);
        requestQueue.add(request);
    }

    void postData(){
        button.setVisibility(View.INVISIBLE);

        final String code = getSelected().getCode();

        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINT_POST, onPostDataSuccess, onPostDataError){

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("session_id", Global.getSessionId());
                params.put("curr_code",code);
                JSONObject jsonObject = new JSONObject(params);
                Log.i("POST DATA","sending "+jsonObject.toString());
                return jsonObject.toString().getBytes();
            }
        };
        requestQueue.add(request);
    }

    private final Response.Listener<String>  onGetCurrencySuccess = new Response.Listener<String>(){
        @Override
        public void onResponse(String response) {
            Log.i("GET CURRENCY", response);

            List<Currency> currs = Arrays.asList(gson.fromJson(response, Currency[].class));

            Log.i("GET Currency", currs.size() + " curres loaded.");
            for (Currency c : currs) {
                data.add(c);
            }
            adapter.notifyDataSetChanged();
        }
    };

    private final Response.ErrorListener onGetCurrencyError = new Response.ErrorListener() {
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
        button.setVisibility(View.VISIBLE);
        Toast.makeText(this,"Success! "+getSelected().getCode(),Toast.LENGTH_SHORT).show();
    }

    void postFail(String msg){
        button.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    //endregion web service

    @Override
    public void onClick(View v) {
        if(v.getId()==button.getId()){
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

    Currency getSelected(){
        return data.get(adapter.getLastSelectedPos());
    }
}
