package charmingdev.d.notify.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import charmingdev.d.notify.Plain;
import charmingdev.d.notify.R;
import charmingdev.d.notify.Utils;
import charmingdev.d.notify.VolleySingleton;
import charmingdev.d.notify.prefs.UserInfo;
import charmingdev.d.notify.prefs.UserSession;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Register extends Fragment {

    View fragView;

    private ProgressDialog progressDialog;
    private UserInfo userInfo;
    private UserSession userSession;
    private String TAG = Register.class.getSimpleName();
    private static int TIME_OUT = 4000;

    private EditText FNAME,LNAME,PHONE;
    private Button PHOTO,JOIN;
    private ImageView LOADED;
    String LOCATION;

    private FusedLocationProviderClient mFusedLocationClient;


    public Register() {
        // Required empty public constructor
    }

    public static String locationStringFromLocation(final Location location) {
        return Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + " " + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragView = inflater.inflate(R.layout.fragment_register, container, false);
        progressDialog = new ProgressDialog(getContext());
        userInfo = new UserInfo(getActivity());
        userSession = new UserSession(getActivity());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

       try {
           mFusedLocationClient.getLastLocation()
                   .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                       @Override
                       public void onSuccess(Location location) {
                           // Got last known location. In some rare situations this can be null.
                           if (location != null) {
                               // Logic to handle location object
                               LOCATION = locationStringFromLocation(location);
                           }
                       }
                   });
       }catch (SecurityException e){
           e.printStackTrace();
       }

        FNAME = (EditText) fragView.findViewById(R.id.fname);
        LNAME = (EditText) fragView.findViewById(R.id.lname);
        PHONE = (EditText) fragView.findViewById(R.id.phone);

        PHOTO = (Button) fragView.findViewById(R.id.photo);
        JOIN = (Button) fragView.findViewById(R.id.join);

        LOADED = (ImageView) fragView.findViewById(R.id.loaded);

        PHOTO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGallery();
            }
        });

        JOIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister();
            }
        });

        return fragView;
    }

    private void loadGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                LOADED.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void userRegister() {
        final String firstname = FNAME.getText().toString().trim();
        final String lastname = LNAME.getText().toString().trim();
        final String phone = PHONE.getText().toString().trim();

        if (TextUtils.isEmpty(firstname)) {
            FNAME.setError("Please enter firstname");
            FNAME.requestFocus();
            return;
        } else if (TextUtils.isEmpty(lastname)) {
            LNAME.setError("Please enter lastname");
            LNAME.requestFocus();
            return;
        } else if (LOADED.getDrawable() == null) {
            Toast.makeText(getActivity(), "Please select an image to upload", Toast.LENGTH_LONG).show();
        } else {

            LOADED.buildDrawingCache();
            Bitmap bitmap = LOADED.getDrawingCache();
            String encodedImageData = getEncoded64ImageStringFromBitmap(bitmap);

            final String photo = encodedImageData;

            progressDialog.setMessage("Sending data...");
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "server Response: " + response.toString());
                            progressDialog.hide();

                            if (response.equalsIgnoreCase("success")) {
                                    //Now store in DB


                                    progressDialog.setMessage("Registration successful! Redirecting...");
                                    progressDialog.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(getActivity(), Plain.class));
                                            getActivity().finish();
                                        }
                                    }, TIME_OUT);


                                } else {
                                    //error report
                                    String errorMsg = response.toLowerCase();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage(response);
                                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder.show();
                                }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "connection error: " + error.getMessage());
                            Toast.makeText(getActivity(), "Connection error", Toast.LENGTH_LONG).show();
                            progressDialog.hide();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("firstname", firstname);
                    params.put("lastname", lastname);
                    params.put("phone", phone);
                    params.put("photo", photo);

                    return params;
                }
            };

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        }
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

}
