package charmingdev.d.notify.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import charmingdev.d.notify.Plain;
import charmingdev.d.notify.R;
import charmingdev.d.notify.Utils;
import charmingdev.d.notify.VolleySingleton;
import charmingdev.d.notify.prefs.UserInfo;
import charmingdev.d.notify.prefs.UserSession;

/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment {

    View fragView;

    private EditText PHONE;
    private Button DONE;

    private ProgressDialog progressDialog;
    private UserSession userSession;
    private UserInfo userInfo;

    private String TAG = Login.class.getSimpleName();

    private static int TIME_OUT = 4000;

    public Login() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragView = inflater.inflate(R.layout.fragment_login, container, false);
        progressDialog = new ProgressDialog(getContext());
        userSession = new UserSession(getActivity());
        userInfo = new UserInfo(getActivity());

        PHONE = (EditText) fragView.findViewById(R.id.phone);
        DONE = (Button) fragView.findViewById(R.id.sign_in);
        DONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        return fragView;
    }

    private void userLogin() {
        final String phone = PHONE.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            PHONE.setError("Please enter your phone number e.g 0900000000");
            PHONE.requestFocus();
            return;
        }

        progressDialog.setMessage("Validating...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        progressDialog.hide();

                        try {
                            JSONObject jobj = new JSONObject(response);
                            boolean error = jobj.getBoolean("error");

                            //checking error node in json
                            if (!error) {
                                //Now store in DB
                                JSONObject user = jobj.getJSONObject("user");
                                String fname = user.getString("first_name");
                                String lname = user.getString("last_name");
                                String phone = user.getString("phone");
                                String photo = user.getString("photo");

                               userInfo.setKeyFirstname(fname);
                               userInfo.setKeyLastname(lname);
                               userInfo.setKeyPhone(phone);
                               userInfo.setKeyPhoto(photo);

                                userSession.setLoggedIn(true);
                                progressDialog.setMessage("Redirecting...");
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
                                String errorMsg = jobj.getString("error_msg");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(errorMsg);
                                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.hide();
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
                params.put("phone", phone);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }


}