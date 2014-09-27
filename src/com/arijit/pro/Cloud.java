package com.arijit.pro;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Cloud extends Activity  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the text view as the activity layout
        setContentView(R.layout.loading);

        // Cteate the Image view
        ImageView mImageView = (ImageView) findViewById(R.id.canvas);
        ProgressBar spinner = (ProgressBar) findViewById(R.id.spinner);   

        // Run async task to download image
        new downloadImageTask(mImageView, spinner).execute("https://farm6.staticflickr.com/5574/15138586768_9afee4767f.jpg");

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


    // Needed to Scale Images
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    // Download image from the web
    private Bitmap downloadImage(String url) {
        try {
            URL location = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) location.openConnection();
            urlConnection.setConnectTimeout(5000);   
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            final Bitmap image = BitmapFactory.decodeStream(in);
            urlConnection.disconnect();
            return image;

        }
        catch (MalformedURLException m) {
            return null;
        }
        catch (IOException e) {
            Log.e("downloadImage", "IOException occured");
            return null;
        }
    } 


    // Async Task for downloading images

    private class downloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private final ProgressBar progress;
        public downloadImageTask(ImageView mImageView, ProgressBar progress) {
            imageViewReference = new WeakReference<ImageView>(mImageView);
            this.progress = progress;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            return downloadImage(url[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            ImageView mImageView = imageViewReference.get();
            mImageView.setImageBitmap(result);            
            // Set the text view as the activity layout
            progress.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
        }
    }
}

