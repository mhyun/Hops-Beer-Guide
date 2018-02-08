package com.michaelhyun.hopsbeerguide;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by michaelhhyun on 1/12/18.
 */

public class Tab3Random extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3_random, container, false);

        //TODO make BrewApiTask run at start
        //TODO save the last beer so when you come to back to tab3, it shows a beer
        //TODO clean up layout
        //TODO only show beers with descriptions

        Button mRandomButton = rootView.findViewById(R.id.tab3_button);
        mRandomButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        String apiKey = getString(R.string.API_URL);
        BrewApiTask randomBeerTask = new BrewApiTask();
        randomBeerTask.execute(apiKey);
    }

    private class BrewApiTask extends AsyncTask<String, Void, String> {
        TextView mRandomBeerNameTextView = (TextView) getView().findViewById(R.id.tab3_name_textview);
        TextView mRandomBeerDescriptionTextView = (TextView) getView().findViewById(R.id.tab3_description_textview);

        @Override
        protected String doInBackground(String... params) {
            try
            {
                URL url = new URL(params[0]);
                HttpURLConnection cc = (HttpURLConnection) url.openConnection();
                try
                {
                    //Tries to read in the response from the API request (should be a JSON object)
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
                finally
                {
                    cc.disconnect();
                }
            }
            catch (Exception e)
            {
                Log.e("ERROR: ", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String apiResponse)
        {
            if (apiResponse == null)
            {
                //if there is no json returned by the api get request then an error is logged!
                apiResponse = "An Error occured when trying to contact the API!";
                Log.i("Info on request: ", apiResponse);
            }

            try
            {
                JSONObject beerObject = new JSONObject(apiResponse).getJSONObject("data");
                String name = beerObject.getString("nameDisplay");
                mRandomBeerNameTextView.setText(name);

                if(beerObject.has("description"))
                {
                    //checks to see if there is a description of the beer
                    if(!beerObject.isNull("description"))
                    {
                        //if there is a description then set it to the textview
                        String desc = beerObject.getString("description");
                        mRandomBeerDescriptionTextView.setText(desc);
                        //sets the text view to scrollable if it overflows
                        //mRandomBeerDescriptionTextView.setMovementMethod(new ScrollingMovementMethod());
                    }
                }
                else
                {
                    mRandomBeerDescriptionTextView.setText(getResources().getString(R.string.no_desc));
                }
            }
            catch (JSONException e)
            {
                //Logging error output
                Log.e("Error: ", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
