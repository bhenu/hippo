package com.arijit.pro;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

public class BetterCloud extends Activity  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the text view as the activity layout
        setContentView(R.layout.loading);
        final TextView mTextView = (TextView) findViewById(R.id.webresponse);
        final ProgressBar spinner = (ProgressBar) findViewById(R.id.spinner);  
        final ImageView mImageView = (ImageView) findViewById(R.id.canvas);

        String url ="http://www.google.com";
        String imgurl = "https://farm6.staticflickr.com/5574/15138586768_9afee4767f.jpg";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the first 500 characters of the response string.
                spinner.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("Response is: "+ response.substring(0,500));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
                spinner.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
            }
        });

        // Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest imageRequest = new ImageRequest(imgurl,
            new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    mImageView.setImageBitmap(bitmap);
                    spinner.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                }
            }, 0, 0, null,
            new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    mTextView.setText("That didn't work!");
                    spinner.setVisibility(View.GONE);
                    mTextView.setVisibility(View.VISIBLE);
                }
            });


        // Get the Volley instance and add the request to the RequestQueue.
        NetworkHandler.getInstance(this).addToRequestQueue(imageRequest);

        // Insert the back button 
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}