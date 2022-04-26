package com.example.databaseproject;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link textInput#newInstance} factory method to
 * create an instance of this fragment.
 */
public class textInput extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PARENT_IS_FRAGMENT = "parentType";
    private static final String PARENT_ID = "parentId";

    // TODO: Rename and change types of parameters
    private boolean startedFromFragment;
    // If parent is fragment, this is the ID of that parent
    private String fragmentId;
    private FragmentActivity listener;

    public textInput() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment textInput.
     */
    // TODO: Rename and change types and number of parameters
    public static textInput newInstance(boolean startedFromFragment, String fragmentId) {
        textInput fragment = new textInput();
        Bundle args = new Bundle();
        args.putBoolean(PARENT_IS_FRAGMENT, startedFromFragment);
        args.putString(PARENT_ID, fragmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            startedFromFragment = getArguments().getBoolean(PARENT_IS_FRAGMENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_input, container, false);
    }
    // closes modal


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            this.listener = (FragmentActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    // TODO : handle text shit in here

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart(){
        super.onStart();
        String parentId = getArguments().getString(PARENT_ID);

        // Post button handler
        View postButton = getView().findViewById(R.id.inputPost);
        postButton.setOnClickListener((View view) -> {
                String input = ((EditText) getView().findViewById(R.id.userTextInput)).getText().toString();
                if(getArguments().getBoolean(PARENT_IS_FRAGMENT)){
                    // Find parent fragment
                    ((feed_post) listener.getSupportFragmentManager().findFragmentByTag(parentId)).post(input);
                    getFragmentManager().popBackStack();
                } else
                    ((postFeed)getActivity()).post(input);
            }
        );

        View cancelButton = getView().findViewById(R.id.inputCancel);
        cancelButton.setOnClickListener((View view) -> {
                if(startedFromFragment)
                    ((feed_post) listener.getSupportFragmentManager().findFragmentByTag(parentId)).close();
                else
                    ((postFeed) getActivity()).close(parentId);
            }
        );

    }
}