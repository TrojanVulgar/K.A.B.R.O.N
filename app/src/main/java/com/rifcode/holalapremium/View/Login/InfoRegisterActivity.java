package com.rifcode.holalapremium.View.Login;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.Profile;
import com.google.firebase.auth.UserInfo;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class InfoRegisterActivity extends AppCompatActivity {

    private ImageView imgvGenderInfoRegisterBoy,imgvGenderInfoRegisterGirl;
    private DatePicker datePicker;
    private long minidate = -1567468800000L;
    private TextView tvNextInfoRegister;
    public static String age;
    private EditText edtNameInfoRegister;
    public static String gender="guy", name, photoUrlfb, emailfb;
    private DataFire dataFire;
    public static int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_register);
        dataFire=new DataFire();
        wedgets();

        datePicker.setMinDate(minidate);
        datePicker.setMaxDate(System.currentTimeMillis());
        applyStyLe(datePicker);
        imgvGenderInfoRegisterBoy.setSelected(true);

        imgvGenderInfoRegisterBoy
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender="guy";
                imgvGenderInfoRegisterBoy.setSelected(true);
                imgvGenderInfoRegisterGirl.setSelected(false);
            }
        });

        imgvGenderInfoRegisterGirl
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender="girl";
                imgvGenderInfoRegisterBoy.setSelected(false);
                imgvGenderInfoRegisterGirl.setSelected(true);
            }
        });

        tvNextInfoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=edtNameInfoRegister.getText().toString();
                day = datePicker.getDayOfMonth();
                month = datePicker.getMonth();
                year = datePicker.getYear();

                    if (edtNameInfoRegister.getText().length() < 6) {
                        Toast.makeText(InfoRegisterActivity.this, R.string.greater6name, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    age = String.valueOf(getAge(year, month, day));
                    if (getAge(year, month, day) < 18) {
                        Toast.makeText(InfoRegisterActivity.this, getString(R.string.un13), Toast.LENGTH_SHORT).show();
                        return;
                    }

                Intent continueAddPic = new Intent(InfoRegisterActivity.this,AddPicActivity.class);
                startActivity(continueAddPic);
                Animatoo.animateSlideLeft(InfoRegisterActivity.this);
            }
        });

       edtNameInfoRegister.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               if(count>0){
                   tvNextInfoRegister.setEnabled(true);
               }else {
                   tvNextInfoRegister.setEnabled(false);
               }
           }

           @Override
           public void afterTextChanged(Editable s) {
           }
       });

       if(WelcomeActivity.typeAccount!=null) {
           if (WelcomeActivity.typeAccount.equals("facebook")) {
               getDataFromFacebook();
           }
       }

    }

    private void wedgets() {
        imgvGenderInfoRegisterBoy =findViewById(R.id.imgvGenderInfoRegisterBoy);
        imgvGenderInfoRegisterGirl =findViewById(R.id.imgvGenderInfoRegisterGirl);
        tvNextInfoRegister =findViewById(R.id.tvNextInfoRegister);
        datePicker = findViewById(R.id.datePicker);
        edtNameInfoRegister = findViewById(R.id.edtNameInfoRegister);
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

    private void applyStyLe(DatePicker datePickerDialog) {
        Resources system = Resources.getSystem();
        int dayNumberPickerId = system.getIdentifier("day","id", "android");
        int monthNumberPickerId = system.getIdentifier("month", "id", "android");
        int yearNumberPickerId = system.getIdentifier("year", "id", "android");

        NumberPicker dayNumberPicker = datePickerDialog.findViewById(dayNumberPickerId);
        NumberPicker monthNumberPicker = datePickerDialog.findViewById(monthNumberPickerId);
        NumberPicker yearNumberPicker= datePickerDialog.findViewById(yearNumberPickerId);

        overrideStyle(dayNumberPicker);
        overrideStyle(monthNumberPicker);
        overrideStyle(yearNumberPicker);
    }

    private void overrideStyle(NumberPicker number_picker) {
        final int count = number_picker.getChildCount();
        Typeface tf=Typeface.createFromAsset(getAssets(),"fonts/avenir_next_demibold.otf"); //declare your font here

        for (int i = 0; i < count; i++) {
            try {


                //this allows you to change the font
                Field wheelpaint_field = number_picker.getClass().getDeclaredField("mSelectorWheelPaint");
                wheelpaint_field.setAccessible(true);
                ((Paint) wheelpaint_field.get(number_picker)).setTypeface(tf);
                ((EditText) number_picker.getChildAt(i)).setTypeface(tf);

                number_picker.invalidate();
            } catch (NoSuchFieldException e) {
                Log.w("setNumberPickerTxtClr", e);
            } catch (IllegalAccessException e) {
                Log.w("setNumberPickerTxtClr", e);
            } catch (IllegalArgumentException e) {
                Log.w("setNumberPickerTxtClr", e);
            }
        }
    }

    private void getDataFromFacebook(){

        if(Profile.getCurrentProfile()!=null) {
            // Name, email address, and profile photo Url
            Profile profile1 = Profile.getCurrentProfile();
            for (UserInfo profiloo : dataFire.getCurrentUser().getProviderData()) {

                final String email = profiloo.getEmail();
                String FirstName = profile1.getFirstName();
                final String photoUrl = profile1.getProfilePictureUri(200, 200).toString();

                edtNameInfoRegister.setText(FirstName);
                emailfb = email;
                photoUrlfb=photoUrl;

            }
        }

    }


}
