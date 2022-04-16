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

    public postCreation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static postCreation newInstance(String param1, String param2) {
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
    /*
        Button b = (Button) v.findViewById(R.id.postButton);
        System.out.print("Button aquired");
        System.out.print(b);
*/
        return v;
    }

}