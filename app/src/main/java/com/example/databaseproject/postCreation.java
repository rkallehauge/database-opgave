package com.example.databaseproject;

import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link postCreation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class postCreation extends Fragment {

    // TODO: Rename and change types and number of parameters
    public static postCreation newInstance() {
        postCreation fragment = new postCreation();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_page, container, false);
        return v;
    }

}