package com.example.databaseproject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


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

    // One Preview Image
    private ImageView IVPreviewImage;

    // constant to compare
    // the activity result code
    private final int SELECT_PICTURE = 200;

    private Uri imagePath;

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
            Log.d("Post creation", "Postbutton pushed");

            String input = ((EditText) getView().findViewById(R.id.userTextInput)).getText().toString();
            //If imagePath is not null it is a post and not comment
            if(imagePath != null)
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(imagePath);
                    byte[] imageBytes = readAllBytes(inputStream);
                    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> remote.uploadImage(imageBytes));
                    ((postFeed) getActivity()).post(input, future.get());
                } catch (IOException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            //No image is given therefore information if needs to be found if the post is comment or a post with only text
            else {
                //Fragment is parent when a comment is posted
                if (getArguments().getBoolean(PARENT_IS_FRAGMENT)) {
                    // Find parent fragment
                    ((feed_post) listener.getSupportFragmentManager().findFragmentByTag(parentId)).post(input);
                    getFragmentManager().popBackStack();
                }
                else ((postFeed) getActivity()).post(input, null);
            }
        });

        //Cancel button handler
        View cancelButton = getView().findViewById(R.id.inputCancel);
        cancelButton.setOnClickListener((View view) -> {
                if(startedFromFragment)
                    ((feed_post) listener.getSupportFragmentManager().findFragmentByTag(parentId)).close();
                else
                    ((postFeed) getActivity()).close(parentId);
            }
        );

        IVPreviewImage = getView().findViewById(R.id.IVPreviewImage);

        //Image upload button handler
        View imageButton = getView().findViewById(R.id.inputImage);
        imageButton.setOnClickListener((View view) -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
        });
        //Make image and image button visible if it is a post creation
        if(!getArguments().getBoolean(PARENT_IS_FRAGMENT)) {
            IVPreviewImage.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.VISIBLE);
        }
    }

    //Called method once user is returned from gallery
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == 200) if (data != null) {
                    IVPreviewImage.setImageURI(data.getData());
                    imagePath = data.getData();
            }
    }
    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyAllBytes(in, out);
        return out.toByteArray();
    }

    public static int copyAllBytes(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[4096];
        while (true) {
            int read = in.read(buffer);
            if (read == -1) break;

            out.write(buffer, 0, read);
            byteCount += read;
        }
        return byteCount;
    }


}