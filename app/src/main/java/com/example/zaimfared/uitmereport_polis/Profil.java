package com.example.zaimfared.uitmereport_polis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profil extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgProfil;
    private TextView txtNoPekerja, txtIcPekerja, txtNoHP, txtNoPejabat, txtJawatan, txtPos;
    private String pekerjaPrefs = "pekerjaPref";
    private String id = "ID";
    private String pekerja_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        //Get id from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(pekerjaPrefs, Context.MODE_PRIVATE);
        pekerja_id = sharedPreferences.getString(id, "");

        //Set profil
        setProfil();
    }

    private void setProfil(){
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = getResources().getString(R.string.url_profile);
            StringRequest profileRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        //Get the JSON object from the server, Response will return status and data
                        JSONObject obj = new JSONObject(response);
                        //Get status from the server. 0 - Failed, 1 - Success
                        if (obj.getString("status").equalsIgnoreCase("1")){
                            JSONObject data = obj.getJSONObject("data");

                            ((TextView)findViewById(R.id.txtNoPekerjaProfil)).setText(data.getString("no_pekerja"));
                            ((TextView)findViewById(R.id.txtNoICProfil)).setText(data.getString("no_ic"));
                            ((TextView)findViewById(R.id.txtNoHPProfil)).setText(data.getString("no_tel_hp"));
                            ((TextView)findViewById(R.id.txtNoPejabatProfil)).setText(data.getString("no_tel_pej"));
                            ((TextView)findViewById(R.id.txtJawatanProfil)).setText(data.getString("jawatan_nama"));
                            ((TextView)findViewById(R.id.txtPosProfil)).setText(data.getString("pos"));
                        }else{
                            //Redirect to log masuk
                            Toast.makeText(Profil.this, "Profil tidak dijumpai", Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Profil tidak dijumpai", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params;
                    params = new HashMap<>();
                    params.put("polis_id", pekerja_id);
                    return params;
                }
            };

            requestQueue.add(profileRequest);

        }catch (Exception e){
            Toast.makeText(Profil.this, "Terdapat masalah dengan rangkaian internet anda", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public Bitmap decodeBase64(String input){
        byte[] decodeBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodeBytes, 0, decodeBytes.length);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnUbahKataLaluan:
                startActivity(new Intent(Profil.this, Daftar.class));
                break;
            case R.id.btnKembali:
                finish();
                break;
            case R.id.btnLogOut:
                //Clear shared preferences
                SharedPreferences settings = getSharedPreferences(pekerjaPrefs, Context.MODE_PRIVATE);
                settings.edit().clear().apply();
                finish();
                break;
        }
    }
}
