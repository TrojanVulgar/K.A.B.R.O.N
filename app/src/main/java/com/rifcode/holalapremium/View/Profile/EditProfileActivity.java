package com.rifcode.holalapremium.View.Profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rifcode.holalapremium.Adapter.ImagesRecyclerViewAdapter;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.Models.Images;
import com.rifcode.holalapremium.Models.Users;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.Utils.BSDSelectPhoto;
import com.rifcode.holalapremium.Utils.ImageOrientation;
import com.rifcode.holalapremium.Utils.PathUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity  implements BSDSelectPhoto.BottomSheetListener{

    String mCurrentPhotoPath;
    private RecyclerView rcvimages;
    private GridLayoutManager mLayoutManager;
    private ImageView imgvEditProfileBack,imgvEditProfileSave;
    private EditText edtEditProfileName,edtEditProfileAbout,edtEditProfileEducation,edtEditProfileWork;
    private TextView tvEditProfileAge,tvEditProfileAboutMeLittresCount;
    private DataFire dataFire;
    private ArrayList<Images> arrayListImages;
    private Users user;

    private static final int MY_GALLERYPERMISSION_CODE = 100;
    private static final int MY_CAMERA_PERMISSION_CODE = 500;
    private static final int CAMERA_REQUEST = 600;
    private static final int GALLERY_PICK = 200;
    @SuppressLint("SimpleDateFormat")
    DateFormat dfYears = new SimpleDateFormat("dd-mm-yyyy-hh-mm-ss");
    private Bitmap bitmapNew;
    private String TAG="EditProfileActivity";
    private ProgressDialog pd;
    private ImagesRecyclerViewAdapter adapter;
    private Bitmap resizedBitmapNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        dataFire=new DataFire();
        arrayListImages = new ArrayList<>();
        wedgets();
        getDataUser();

        imgvEditProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgvEditProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(edtEditProfileName.getText())) {
                    if (edtEditProfileName.getText().length() < 6) {
                        Toast.makeText(EditProfileActivity.this, R.string.greater6name, Toast.LENGTH_SHORT).show();
                    }else{
                        saveDataUser();
                    }
                }else{
                    Toast.makeText(EditProfileActivity.this, R.string.entyname, Toast.LENGTH_SHORT).show();
                }
            }
        });

        edtEditProfileAbout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvEditProfileAboutMeLittresCount.setText(s.length() + "/150");
            }
        });

    }

    private void saveDataUser() {

        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("username")
                .setValue(edtEditProfileName.getText().toString());

        if(!TextUtils.isEmpty(edtEditProfileWork.getText().toString()))
            dataFire.getDbRefUsers().child(dataFire.getUserID())
                    .child("job")
                    .setValue(edtEditProfileWork.getText().toString());


        if(!TextUtils.isEmpty(edtEditProfileAbout.getText().toString()))
            dataFire.getDbRefUsers().child(dataFire.getUserID())
                    .child("about")
                    .setValue(edtEditProfileAbout.getText().toString());

        dataFire.getDbRefUsers()
                .child(dataFire.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String birthday = String.valueOf(dataSnapshot.child("birthday").getValue());
                        String [] dateParts = birthday.split("/");
                        int day = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]);
                        int year = Integer.parseInt(dateParts[2]);
                        String age = String.valueOf(getAge(year, month, day));
                        dataFire.getDbRefUsers().child(dataFire.getUserID()).child("age").setValue(age);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if(!TextUtils.isEmpty(edtEditProfileEducation.getText().toString()))
            dataFire.getDbRefUsers().child(dataFire.getUserID())
                    .child("school")
                    .setValue(edtEditProfileEducation.getText().toString());

        Toast.makeText(EditProfileActivity.this, R.string.data_save_seccff, Toast.LENGTH_SHORT).show();
        finish();
    }
    public int getAge (int _year, int _month, int _day) {

        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0)
            throw new IllegalArgumentException("Age < 0");
        return a;
    }



    private void getDataUser() {
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = String.valueOf(dataSnapshot.child("username").getValue());
                        String job = String.valueOf(dataSnapshot.child("job").getValue());
                        String age = String.valueOf(dataSnapshot.child("age").getValue());
                        String about = String.valueOf(dataSnapshot.child("about").getValue());
                        String school = String.valueOf(dataSnapshot.child("school").getValue());

                        edtEditProfileName.setText(name);
                        tvEditProfileAge.setText(age);
                        if(!about.equals("---")&& dataSnapshot.hasChild("about")){
                            edtEditProfileAbout.setText(about);
                        }
                        if(!job.equals("---") && dataSnapshot.hasChild("job")){
                            edtEditProfileWork.setText(job);
                        }
                        if(!school.equals("---") && dataSnapshot.hasChild("school")){
                            edtEditProfileEducation.setText(school);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        loading6ImagesUser();

    }

    public void loading6ImagesUser(){
        //get images user
        dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot imageSnapshot : dataSnapshot.child("images").getChildren()) {
                    Images images = imageSnapshot.getValue(Images.class);
                    Images img = new Images();
                    img.setThumb_picture(images.getThumb_picture());
                    arrayListImages.add(img);
                }
                user =new Users(arrayListImages);

                adapter = new ImagesRecyclerViewAdapter(EditProfileActivity.this,arrayListImages,user);
                rcvimages.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void wedgets() {

        // recycler view
        rcvimages= findViewById(R.id.rcvImages);
        rcvimages.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this,3
                , LinearLayoutManager.VERTICAL,false);
        rcvimages.setLayoutManager(mLayoutManager);
        // end recyclerview

        // imageview
        imgvEditProfileBack=findViewById(R.id.imgvEditProfileBack);
        imgvEditProfileSave=findViewById(R.id.imgvEditProfileSave);

        // edittext
        edtEditProfileName=findViewById(R.id.edtEditProfileName);
        edtEditProfileAbout=findViewById(R.id.edtEditProfileAbout);
        edtEditProfileEducation=findViewById(R.id.edtEditProfileEducation);
        edtEditProfileWork=findViewById(R.id.edtEditProfileWork);

        // textview
        tvEditProfileAge=findViewById(R.id.tvEditProfileAge);
        tvEditProfileAboutMeLittresCount=findViewById(R.id.tvEditProfileAboutMeLittresCount);

        //progress dialog
        pd = new ProgressDialog(EditProfileActivity.this);
        pd.setMessage(getString(R.string.pd_uplod_photo));
        pd.setCancelable(false);
    }

    @Override
    public void onButtonClicked(String text) {
        if(text.equals("ll_photos")){
            if(isStoragePermissionGalleryGranted()) {
                openGallery();
            }
        }
        if(text.equals("ll_camera")){
            if(isStoragePermissionCameraGranted()) {
                openCamera();
            }
        }

    }


    private void openCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(this, getPackageName() +".provider",
                                new File(mCurrentPhotoPath)));
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }



    public  boolean isStoragePermissionGalleryGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");

                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_GALLERYPERMISSION_CODE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public  boolean isStoragePermissionCameraGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");

                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_CAMERA_PERMISSION_CODE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.cg)), GALLERY_PICK);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private File createImageFile() throws IOException {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = String.format("JPEG_%s_", ts);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(filename, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            pd.show();
            String getDateYears = dfYears.format(Calendar.getInstance().getTime());

            final StorageReference filePath = dataFire.getStorageref()
                    .child("all_images_user")
                    .child(dataFire.getUserID())
                    .child(getDateYears + ".jpg");


            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 2;
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[16 * 1024];

            Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath,options);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmp, 960, 730, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Uri uriImage = Uri.fromFile(new File(mCurrentPhotoPath));
            try {
                resizedBitmapNew = ImageOrientation.modifyOrientation(this,resizedBitmap,uriImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            resizedBitmapNew.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            UploadTask uploadTask = filePath.putBytes(imageBytes);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {

                            Toast.makeText(EditProfileActivity.this, R.string.toast_image_change_succ_profile
                                    , Toast.LENGTH_SHORT).show();

                            final String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            dataFire.getDbRefUsers().child(dataFire.getUserID())
                                    .child("images").child(ImagesRecyclerViewAdapter.posImage)
                                    .child("thumb_picture").setValue(photoStringLink);
                            dataFire.getDbRefUsers().child(dataFire.getUserID())
                                    .child("images").child(ImagesRecyclerViewAdapter.posImage)
                                    .child("Time").setValue(-1 * (System.currentTimeMillis() / 1000));

                            // images reviews
                            String puchKey = dataFire.getdbRefImagesReviews().push().getKey();
                            HashMap<String, String> imagesReviewsMap = new HashMap<>();
                            imagesReviewsMap.put("userID", dataFire.getUserID());
                            imagesReviewsMap.put("image", photoStringLink);
                            imagesReviewsMap.put("type", "photos");
                            imagesReviewsMap.put("pos", ImagesRecyclerViewAdapter.posImage);
                            dataFire.getdbRefImagesReviews().child(puchKey).setValue(imagesReviewsMap);
                            dataFire.getdbRefImagesReviews().child(puchKey).child("time")
                                    .setValue(-1 * (System.currentTimeMillis() / 1000))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            user.getImages().clear();
                                            arrayListImages.clear();
                                            loading6ImagesUser();
                                            // notify adapter

                                        }
                                    });
                            //end images review
                            if (ImagesRecyclerViewAdapter.posImage.equals("0")) {
                                dataFire.getDbRefUsers().child(dataFire.getUserID()).child("photoProfile")
                                        .setValue(photoStringLink);
                            }

                            pd.dismiss();
                        }

                    }
                }
            });


        }
        // if i want just add photo profile //
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            pd.show();
            String getDateYears = dfYears.format(Calendar.getInstance().getTime());

            if (data!=null) {
                final Uri imageUri = data.getData();
                // setImage(imageUri);


                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inSampleSize = 2;
                options.inJustDecodeBounds = false;
                options.inTempStorage = new byte[16 * 1024];

                String filePathimage= null;
                try {
                    filePathimage = PathUtil.getPath(this,imageUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }


                Bitmap bmp = BitmapFactory.decodeFile(filePathimage,options);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    resizedBitmapNew = ImageOrientation.modifyOrientation(this,resizedBitmap,imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resizedBitmapNew.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();

//
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmapNew.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//                final byte[] data1 = byteArrayOutputStream.toByteArray();

                final StorageReference filePath = dataFire.getStorageref()
                        .child("all_images_user")
                        .child(dataFire.getUserID())
                        .child(getDateYears + ".jpg");


                UploadTask uploadTask = filePath.putBytes(imageBytes);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri downloadUri = task.getResult();
                            if (downloadUri != null) {

                                Toast.makeText(EditProfileActivity.this, R.string.toast_image_change_succ_profile
                                        , Toast.LENGTH_SHORT).show();

                                final String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                dataFire.getDbRefUsers().child(dataFire.getUserID())
                                        .child("images").child(ImagesRecyclerViewAdapter.posImage)
                                        .child("thumb_picture").setValue(photoStringLink);
                                dataFire.getDbRefUsers().child(dataFire.getUserID())
                                        .child("images").child(ImagesRecyclerViewAdapter.posImage)
                                        .child("Time").setValue(-1 * (System.currentTimeMillis() / 1000));

                                // images reviews
                                String puchKey = dataFire.getdbRefImagesReviews().push().getKey();
                                HashMap<String, String> imagesReviewsMap = new HashMap<>();
                                imagesReviewsMap.put("userID", dataFire.getUserID());
                                imagesReviewsMap.put("image", photoStringLink);
                                imagesReviewsMap.put("type", "photos");
                                imagesReviewsMap.put("pos", ImagesRecyclerViewAdapter.posImage);
                                dataFire.getdbRefImagesReviews().child(puchKey).setValue(imagesReviewsMap);
                                dataFire.getdbRefImagesReviews().child(puchKey).child("time")
                                        .setValue(-1 * (System.currentTimeMillis() / 1000))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                user.getImages().clear();
                                                arrayListImages.clear();
                                                loading6ImagesUser();
                                                // notify adapter

                                            }
                                        });
                                //end images review
                                if (ImagesRecyclerViewAdapter.posImage.equals("0")) {
                                    dataFire.getDbRefUsers().child(dataFire.getUserID()).child("photoProfile")
                                            .setValue(photoStringLink);
                                }

                                pd.dismiss();
                            }

                        }
                    }
                });
            }
        }

    }

    private void setImage(Uri imageUri){
        try {
            //..First convert the Image to the allowable size so app do not throw Memory_Out_Bound Exception
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);
            int resolution = 500;
            options.inSampleSize = calculateInSampleSize(options, resolution  , resolution);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);

            //...Now You have the 'bitmap' to rotate....
            //...Rotate the bitmap to its original Orientation...
            bitmapNew = ImageOrientation.modifyOrientation(this,bitmap,imageUri);

            //...After Rotation set the image to Image View...

        } catch (Exception e) {
            Log.d("Image_exception",e.toString());
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(EditProfileActivity.this, R.string.camera_perm_granted, Toast.LENGTH_LONG).show();
                openCamera();
            } else {
                Toast.makeText(EditProfileActivity.this, R.string.camera_perms_denied, Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == MY_GALLERYPERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                Toast.makeText(EditProfileActivity.this, R.string.cs , Toast.LENGTH_LONG).show();
                openGallery();
            }else{
                Toast.makeText(EditProfileActivity.this, R.string.csdd , Toast.LENGTH_LONG).show();
            }
        }

    }

}
