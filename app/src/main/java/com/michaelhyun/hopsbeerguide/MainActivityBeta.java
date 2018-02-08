package com.michaelhyun.hopsbeerguide;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by michaelhhyun on 1/13/18.
 */

public class MainActivityBeta extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainbeta);

        // Sets Hops Logo onto the Action Bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

//        // Executes API call to pull random beer and load into views on startup
//        String apiKey = getString(R.string.API_URL);
//        BrewApiTask randomBeerTask = new BrewApiTask();
//        randomBeerTask.execute(apiKey);
    }

    // Executes API call to pull random beer and load into views on button click
    public void onClick(View view) {
        String apiKey = getString(R.string.API_URL);
        BrewApiTask randomBeerTask = new BrewApiTask();
        randomBeerTask.execute(apiKey);
    }

    // API call to pull from BreweryDB API
    private class BrewApiTask extends AsyncTask<String, Void, String> {
        TextView mRandomBeerNameTextView = (TextView) findViewById(R.id.beer_name);
        TextView mRandomBeerDescriptionTextView = (TextView) findViewById(R.id.beer_description);
        ImageView beerImage = (ImageView) findViewById(R.id.beer_iv);

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection cc = (HttpURLConnection) url.openConnection();

                // Try building JSON object to pull data
                try {
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(cc.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = buffer.readLine()) != null)
                    {
                        builder.append(line).append("\n");
                    }
                    buffer.close();
                    return builder.toString();
                }
                finally {
                    cc.disconnect();
                }
            }
            catch (Exception e) {
                Log.e("ERROR: ", e.getMessage(), e);
                return null;
            }
        }


        protected void onPostExecute(String apiResponse) {
            // Returns error when there is no JSON
            if (apiResponse == null) {
                apiResponse = "An Error occured while trying to contact the API";
                Log.i("Info on request: ", apiResponse);
            }

            // Tries to grab Name and Description
            try {
                JSONObject randBeerObj = new JSONObject(apiResponse).getJSONObject("data");
                String name = randBeerObj.getString("nameDisplay");
                mRandomBeerNameTextView.setText(name);

                if(randBeerObj.has("description")) {
                    // Checks to see if there is a description of the beer
                    if(!randBeerObj.isNull("description")) {
                        // If there is a description, sets it in TextView
                        String desc = randBeerObj.getString("description");
                        mRandomBeerDescriptionTextView.setText(desc);
                    }
                }
                else {
                    mRandomBeerDescriptionTextView.setText(getResources().getString(R.string.no_desc));
                }

                // TODO Review later
                // Checks to see if JSON has labels field AND is not empty
                if (randBeerObj.has("labels") && !randBeerObj.isNull("labels")) {
                    // could easily use getstring here but use optstring because it will return the empty string if it finds nothing
                    new getBeerImage().execute(randBeerObj.getJSONObject("labels").optString("large"));
                    // if a label is found then pass "large" label to Async task to download and apply the label image to the imageview
                }
                else {
                    //If no beer label found set the imageview to a stock "no image available" image
                    Bitmap noBeerLabel = BitmapFactory.decodeResource(getResources(), R.drawable.random_beer_image);
                    beerImage.setImageBitmap(noBeerLabel);
                }

            }
            catch (JSONException e) {
                //Logging error output
                Log.e("Error: ", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // TODO Review Later
    //Gets called if there is a beer image for the beer retrieved from the API
    private class getBeerImage extends AsyncTask<String, Void, Bitmap> {
        //Reference to the beer image imageview
        ImageView beerImage = (ImageView) findViewById(R.id.beer_iv);

        protected Bitmap doInBackground(String... urls) {
            //Url to the beer image
            String imgLocale = urls[0];
            Bitmap image = null;
            try {
                //Creates a bitmap object by decoding an input stream
                InputStream inputS = new URL(imgLocale).openStream();
                image = BitmapFactory.decodeStream(inputS);
            }
            catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            //sets the imageview to the bitmap got from the URL
            beerImage.setImageBitmap(result);
        }
    }
}
