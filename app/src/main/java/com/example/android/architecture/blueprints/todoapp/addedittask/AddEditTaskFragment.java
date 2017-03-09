/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.addedittask;

import static com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskActivity.permissionGranted;
import static com.google.common.base.Preconditions.checkNotNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.architecture.blueprints.todoapp.R;
import com.frosquivel.magicalcamera.Functionallities.PermissionGranted;
import com.frosquivel.magicalcamera.MagicalCamera;

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
public class AddEditTaskFragment extends Fragment implements AddEditTaskContract.View {

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";
    public static final String ARGUMENT_EDIT_TASK_INTERNAL_ID = "EDIT_TASK_INTERNAL_ID";

    private AddEditTaskContract.Presenter mPresenter;

    private TextView mTitle;
    private TextView mHistory;
    private TextView mDescription;

   private TextView mImageUrl;
    //this is the image view for show your picture taken
    private ImageView imageView;
    //button to take picture
    private Button btntakephoto;
    //button to select picture of your device
    private Button btnselectedphoto;
    private Button saveImage;
    //Ever you need to call magical camera and permissionGranted
    private MagicalCamera magicalCamera;

    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 100;

    private Activity activity;

    public AddEditTaskFragment() {
        // Required empty public constructor
    }

    public static AddEditTaskFragment newInstance() {
        return new AddEditTaskFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull AddEditTaskContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveTask(mTitle.getText().toString(),mHistory.getText().toString(), mDescription.getText().toString(),mImageUrl.getText().toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.addtask_frag, container, false);
        mTitle = (TextView) root.findViewById(R.id.add_task_title);
        mHistory = (TextView) root.findViewById(R.id.add_task_history);
        mDescription = (TextView) root.findViewById(R.id.add_task_description);
        mImageUrl = (TextView) root.findViewById(R.id.add_task_imageUrl);
        activity= getActivity();


//instance magical camera
        magicalCamera = new MagicalCamera(activity ,RESIZE_PHOTO_PIXELS_PERCENTAGE, permissionGranted);

        imageView =  (ImageView) root.findViewById(R.id.imageView);
        btntakephoto =  (Button) root.findViewById(R.id.btntakephoto);
        btnselectedphoto =  (Button) root.findViewById(R.id.btnselectedphoto);
        saveImage = (Button) root.findViewById(R.id.saveImage);
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(magicalCamera != null) {
                    if (magicalCamera.getPhoto() != null) {
                        //save the photo in your memory external or internal of your device
                        String path = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(), "student", MagicalCamera.JPEG, true);

                        mImageUrl.setText(path);
                        if (mImageUrl != null) {
                            Toast.makeText(activity,
                                    "The photo is save manual in device, please check this path: " + mImageUrl,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity,
                                    "Sorry your photo dont write in devide, please contact with fabian7593@gmail and say this error",
                                    Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(activity,
                                "Your image is null, please select or take one",
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(activity,
                            "Please initialized magical camera, maybe in static context for use in all activity",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        btntakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is the form to take picture in fragment
                if (magicalCamera.takeFragmentPhoto()) {
                    startActivityForResult(magicalCamera.getIntentFragment(),
                            MagicalCamera.TAKE_PHOTO);
                }

            }
        });
        btnselectedphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is the form to select picture of device
                if (magicalCamera.selectedFragmentPicture()) {
                    startActivityForResult(
                            Intent.createChooser(magicalCamera.getIntentFragment(), "My Header Example"),
                            MagicalCamera.SELECT_PHOTO);
                }
            }
        });
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(mTitle, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }
    @Override
    public void setHistory(String history) {
        mTitle.setText(history);
    }
    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }
    @Override
    public void setImageUrl(String imageUrl) {
        mImageUrl.setText(imageUrl);
    }
    private boolean notNullNotFill(String validate){
        if(validate != null){
            if(!validate.trim().equals("")){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        magicalCamera.resultPhoto(requestCode, resultCode, data, MagicalCamera.ORIENTATION_ROTATE_NORMAL);

        if(magicalCamera.getPhoto()!=null) {
            imageView.setImageBitmap(magicalCamera.getPhoto());

            String path = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(), "myTestPhoto", MagicalCamera.JPEG, true);

            if (path != null) {
                Toast.makeText(activity, "The photo is save in device, please check this path: " + path, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Sorry your photo dont write in devide, please contact with fabian7593@gmail and say this error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        magicalCamera.permissionGrant(requestCode, permissions, grantResults);
    }
}
