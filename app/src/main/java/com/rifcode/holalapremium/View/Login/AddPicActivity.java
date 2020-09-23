package com.rifcode.holalapremium.View.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.Utils.BSDSelectPhoto;
import com.rifcode.holalapremium.Utils.ImageOrientation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPicActivity extends AppCompatActivity implements BSDSelectPhoto.BottomSheetListener{

    private static final int MY_GALLERYPERMISSION_CODE = 100;
    private static final int MY_CAMERA_PERMISSION_CODE = 500;
    private static final int CAMERA_REQUEST = 600;
    private static final int GALLERY_PICK = 200;
    private CircleImageView imgvAddPicUserRegister;
    private TextView tvNextAddPic;
    private String TAG="AddPicActivity : ";
    private RelativeLayout rlRegisterLoading;
    private DataFire dataFire;
    @SuppressLint("SimpleDateFormat")
    DateFormat dfYears = new SimpleDateFormat("dd-mm-yyyy-hh-mm-ss");
    String getDateYears = dfYears.format(Calendar.getInstance().getTime());

    private SharedPreferences.Editor photoUser;
    private Bitmap bitmapNew;
    private Bitmap resizedBitmapNew;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pic);
        dataFire=new DataFire();
        photoUser = getSharedPreferences("photoUser", MODE_PRIVATE).edit();
        wedgets();

        if(WelcomeActivity.typeAccount!=null) {
            if (WelcomeActivity.typeAccount.equals("facebook")) {
                tvNextAddPic.setEnabled(true);
                photoUser.putString("photoUser", InfoRegisterActivity.photoUrlfb);
                photoUser.apply();
                Picasso.get().load(InfoRegisterActivity.photoUrlfb).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.register_avatar_default).into(imgvAddPicUserRegister, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(InfoRegisterActivity.photoUrlfb).placeholder(R.drawable.register_avatar_default)
                                .into(imgvAddPicUserRegister);
                    }
                });

            }
        }

        imgvAddPicUserRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSDSelectPhoto bsdSelectPhoto = new BSDSelectPhoto();
                bsdSelectPhoto.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        tvNextAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextact = new Intent(AddPicActivity.this,RegisterActivity.class);
                startActivity(nextact);
                Animatoo.animateSlideLeft(AddPicActivity.this);
            }
        });

    }

    private void wedgets() {
        imgvAddPicUserRegister=findViewById(R.id.imgvAddPicUserRegister);
        tvNextAddPic=findViewById(R.id.tvNextAddPic);
        rlRegisterLoading=findViewById(R.id.rl_register_loading);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(AddPicActivity.this, R.string.camera_perm_granted, Toast.LENGTH_LONG).show();
                openCamera();
            } else {
                Toast.makeText(AddPicActivity.this, R.string.camera_perms_denied, Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == MY_GALLERYPERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                Toast.makeText(AddPicActivity.this, R.string.cs , Toast.LENGTH_LONG).show();
                openGallery();
            }else{
                Toast.makeText(AddPicActivity.this, R.string.csdd , Toast.LENGTH_LONG).show();
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

    private File createImageFile() throws IOException {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = String.format("JPEG_%s_", ts);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(filename, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            rlRegisterLoading.setVisibility(View.VISIBLE);

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

                            Toast.makeText(AddPicActivity.this, R.string.toast_image_change_succ_profile
                                    , Toast.LENGTH_SHORT).show();

                            rlRegisterLoading.setVisibility(View.GONE);

                            final String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            photoUser.putString("photoUser", photoStringLink);
                            photoUser.apply();
                            Picasso.get().load(photoStringLink).placeholder(R.drawable.icon_register_select_header)
                                    .into(imgvAddPicUserRegister);

                            tvNextAddPic.setEnabled(true);
                        }

                    } else {
                        rlRegisterLoading.setVisibility(View.GONE);
                    }
                }
            });



        }


        // if i want just add photo profile //
        if ((requestCode == GALLERY_PICK && resultCode == RESULT_OK) && data != null) {


            rlRegisterLoading.setVisibility(View.VISIBLE);
            final Uri imageUri = data.getData();
            setImage(imgvAddPicUserRegister,imageUri);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapNew.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            final byte[] data1 = byteArrayOutputStream.toByteArray();


            final StorageReference filePath = dataFire.getStorageref()
                    .child("all_images_user")
                    .child(dataFire.getUserID())
                    .child(getDateYears + ".jpg");


            UploadTask uploadTask = filePath.putBytes(data1);

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

                            Toast.makeText(AddPicActivity.this, R.string.toast_image_change_succ_profile
                                    , Toast.LENGTH_SHORT).show();

                            rlRegisterLoading.setVisibility(View.GONE);

                            final String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            photoUser.putString("photoUser", photoStringLink);
                            photoUser.apply();

                            tvNextAddPic.setEnabled(true);
                        }

                    } else {
                        rlRegisterLoading.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    private void setImage(CircleImageView imgv,Uri imageUri){
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
            bitmapNew = ImageOrientation.modifyOrientation(getApplicationContext(),bitmap,imageUri);

            //...After Rotation set the image to Image View...
            imgv.setImageBitmap(bitmapNew);

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
}
