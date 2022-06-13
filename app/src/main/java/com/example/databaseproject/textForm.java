package com.example.databaseproject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link textForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class textForm extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PARENT_IS_FRAGMENT = "parentType";
    private static final String PARENT_ID = "parentId";

    private boolean startedFromFragment;
    private FragmentActivity listener;

    //Preview viewer for image
    private ImageView IVPreviewImage;

    // Successful response code for selecting image
    private final int SELECT_PICTURE = 200;

    private Uri imagePath;


    public textForm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment textInput.
     */
    public static textForm newInstance(boolean startedFromFragment, String fragmentId) {
        textForm fragment = new textForm();
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
        return inflater.inflate(R.layout.fragment_textform, container, false);
    }


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

    /**
     * Initialize the textForm with handlers
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart(){
        super.onStart();
        String parentId = getArguments().getString(PARENT_ID);

        // Post button handler
        View postButton = getView().findViewById(R.id.inputPost);
        postButton.setOnClickListener((View view) -> textFormSubmit(parentId));

        //Cancel button handler
        View cancelButton = getView().findViewById(R.id.inputCancel);
        cancelButton.setOnClickListener((View view) -> {
            if(startedFromFragment)
                ((feed_post) listener.getSupportFragmentManager().findFragmentByTag(parentId)).closeCommentAndShowReaction();
            else
                ((feed) getActivity()).close();
        });

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

    /**
     * textForm submit handler method
     * @param parentId The id of parent of the textForm
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void textFormSubmit(String parentId) {
        Log.d("Post creation", "Post-button pressed");

        String input = ((EditText) getView().findViewById(R.id.userTextInput)).getText().toString();
        //If imagePath is not null it is a post and not comment
        if(imagePath != null)
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imagePath);
                byte[] imageBytes = readAllBytes(inputStream);
                CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> remote.uploadImage(imageBytes));
                ((feed) getActivity()).insertPostintoDatabases(input, future.get());
            } catch (IOException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            //No image is given therefore information if needs to be found if the post is comment or a post with only text
        else {
            //Fragment is parent when a comment is posted
            if (getArguments().getBoolean(PARENT_IS_FRAGMENT)) {
                // Find parent fragment
                ((feed_post) listener.getSupportFragmentManager().findFragmentByTag(parentId)).makeComment(input);
                getFragmentManager().popBackStack();
            }
            else ((feed) getActivity()).insertPostintoDatabases(input, null);
        }
    }

    /**
     * Method called when a image is selected from gallery
     * @param requestCode The code which tells if an image is selected
     * @param resultCode The code which tells if the activity was done successfully
     * @param data The data of the selected image
     */
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

    /**
     * Reads an image from input stream and converts it into a byte array
     * @param in Inputstream with image data
     * @return byte array with image information
     * @throws IOException
     */
    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        while (true) {
            int read = in.read(buffer);
            if (read == -1) break;
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

}