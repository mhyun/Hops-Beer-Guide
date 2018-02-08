package com.michaelhyun.hopsbeerguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by michaelhhyun on 1/12/18.
 */

public class Tab2Explore extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_explore, container, false);
        return rootView;
    }

//    private class SearchBeerTask implements AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            return null;
//        }
//    }
}
