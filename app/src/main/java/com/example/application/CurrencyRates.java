package com.example.application;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CurrencyRates {

    Context context;

    float EUR;
    float USD;
    float GBP;
    float CHF;

    public float getEUR() {
        return EUR;
    }

    public float getUSD() {
        return USD;
    }

    public float getGBP() {
        return GBP;
    }

    public float getCHF() {
        return CHF;
    }

    public CurrencyRates(Context context) {
        this.context = context;
    }

    public void updateCurrencyRates(){
        sendRequest("EUR");
        sendRequest("USD");
        sendRequest("GBP");
        sendRequest("CHF");
    }

    public void sendRequest(String currency_code) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String base_url = "http://api.nbp.pl/api/exchangerates/rates/A/";
        String format = "/?format=json";

        String url = base_url + currency_code + format;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObj = new JSONObject(response);

                            String table = jsonObj.getString("table");
                            String currency = jsonObj.getString("currency");
                            String code = jsonObj.getString("code");

                            // Getting JSON Array node
                            JSONArray rates = jsonObj.getJSONArray("rates");

                            String no = rates.getJSONObject(0).getString("no");
                            String effectiveDate = rates.getJSONObject(0).getString("effectiveDate");
                            String mid = rates.getJSONObject(0).getString("mid");

                            switch (code){
                                case "EUR":
                                    EUR = Float.parseFloat(mid);
                                    break;
                                case "USD":
                                    USD = Float.parseFloat(mid);
                                    break;
                                case "GBP":
                                    GBP = Float.parseFloat(mid);
                                    break;
                                case "CHF":
                                    CHF = Float.parseFloat(mid);
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + code);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Json parsing error", Toast.LENGTH_LONG).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Downloading data error!", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }



}
