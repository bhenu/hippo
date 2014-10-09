package com.arijit.pro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Arrays;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class FlickrGallery extends Activity  {

    private ImageAdapter mImageAdapter;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Activity that = this;

        // Set layout
        setContentView(R.layout.gallery);

        // Set layout adapter
        mImageAdapter = new ImageAdapter(this);
        GridView flickr_gallery = (GridView) findViewById(R.id.flickr_gallery);
        flickr_gallery.setAdapter(mImageAdapter);

        // Define onclick action
        flickr_gallery.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {                
                Intent intent = new Intent(that, ShowImage.class);
                intent.putExtra("photo_id", id);
                startActivity(intent);
            }
        });

        if (savedInstanceState == null) {      
            // Get images from flickr
            String apiUrl = "https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=6c68709c9c10022f4df15a7c39637811&format=json&nojsoncallback=1&per_page=20";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject photos = response.getJSONObject("photos");
                        ArrayList<String> urlList = URLConstructor(photos.getJSONArray("photo"));
                        mImageAdapter.addData(urlList);
                    }
                    catch (JSONException e){
                        Log.e("JSONResponse", e.toString());
                    }
                    
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("JSONResponse", "something went wrong");

                }
            });
            NetworkHandler.getInstance(this).addToRequestQueue(jsObjRequest);
        }

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putStringArrayList("URLS_FINAL", mImageAdapter.getURLList());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        mImageAdapter.addData(savedInstanceState.getStringArrayList("URLS_FINAL"));
    }


    private ArrayList<String> URLConstructor(JSONArray photos){
        int n = photos.length();
        int i;
        ArrayList<String> result = new ArrayList<String>();
        String fullURL, farm, server, id, secret;
        JSONObject photodata;
        for (i=0; i < n; i++){
            try {
                photodata = photos.getJSONObject(i);
                farm = photodata.getString("farm");
                server = photodata.getString("server");
                id = photodata.getString("id");
                secret = photodata.getString("secret");
                fullURL = "https://farm" 
                        + farm
                        + ".staticflickr.com/"
                        + server
                        + "/"
                        + id
                        + "_"
                        + secret
                        + "_q.jpg";
                result.add(fullURL);
            }
            catch (JSONException e){
                Log.e("JSONObject", "Error parsing: " + e.toString());
            }
        }

        return result;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private int IMAGE_DIMENSION;
        private int page = 2;
        // Get Volley's image loader to load the images.
        private ImageLoader mImageLoader;

        public ImageAdapter(Context c) {            
            mContext = c;
            int displayWidth = c.getResources().getDisplayMetrics().widthPixels;
            Display display = ((WindowManager) c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int rotation = display.getRotation();
            mImageLoader = NetworkHandler.getInstance(mContext).getImageLoader();
            if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180){
                IMAGE_DIMENSION = (displayWidth -4)/3;
            }
            else {
                IMAGE_DIMENSION = (displayWidth -6)/4;
            }
        }

        public int getCount() {
            // return URLS.length;
            return URLS_FINAL.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public ArrayList<String> getURLList() {
            return URLS_FINAL;
        }

        @Override
        public long getItemId(int position) {
            return mThumbIds[position];
        }

        // create a new NetworkImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            NetworkImageView netImageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                netImageView = new NetworkImageView(mContext);
                netImageView.setLayoutParams(new GridView.LayoutParams(IMAGE_DIMENSION, IMAGE_DIMENSION));
                netImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } 
            else {
                netImageView = (NetworkImageView) convertView;
            }

            // netImageView.setImageUrl(URLS[position], mImageLoader)  ;
            netImageView.setImageUrl(URLS_FINAL.get(position), mImageLoader);
            netImageView.setDefaultImageResId(R.drawable.image_background);

            // load more images from flickr
            if(position == getCount() -4){
                String apiUrl = "https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=6c68709c9c10022f4df15a7c39637811&format=json&nojsoncallback=1&per_page=20&page=" + page;
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject photos = response.getJSONObject("photos");
                            ArrayList<String> urlList = URLConstructor(photos.getJSONArray("photo"));
                            addData(urlList);
                            page++;
                        }
                        catch (JSONException e){
                            Log.e("JSONResponse", e.toString());
                        }
                        
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("JSONResponse", "something went wrong");

                    }
                });
                NetworkHandler.getInstance(mContext).addToRequestQueue(jsObjRequest);
            }

            return netImageView;
        }

        // change url set
        public void addData(ArrayList<String> newUrls){
            URLS_FINAL.addAll(newUrls);
            notifyDataSetChanged();
        }

        // references to our images
        private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
        };

        private ArrayList<String> URLS_FINAL = new ArrayList<String>();
    }
};


