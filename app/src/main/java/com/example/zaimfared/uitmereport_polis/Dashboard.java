package com.example.zaimfared.uitmereport_polis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tooltip.Tooltip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {

    public static final String id = "ID";
    public static final String pekerjaPrefs = "pekerjaPref";
    private String pekerja_id;
    private LaporanAdapter laporanAdapter;
    private List<Laporan> laporanList = new ArrayList<>();
    private static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = getSharedPreferences(Dashboard.pekerjaPrefs, Context.MODE_PRIVATE);
        pekerja_id = sharedPreferences.getString(id, "");

        RecyclerView recyclerView = findViewById(R.id.rcyLaporan);
        laporanAdapter = new LaporanAdapter(laporanList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(laporanAdapter);
        prepareLaporan();

    }

    private void prepareLaporan(){
        final ProgressDialog pDialog = new ProgressDialog(Dashboard.this);
        RequestQueue requestQueue = Volley.newRequestQueue(Dashboard.this);
        StringRequest laporanRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.url_dashboard), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    //Get the JSON object from the server
                    JSONObject obj = new JSONObject(response);

                    //Get status from the server. 0 - Failed, 1 - Success
                    if (obj.getString("status").equalsIgnoreCase("1")){
                        //Redirect to dashboard after laporan success
                        JSONObject data = obj.getJSONObject("data");
                        JSONArray laporan_list = data.getJSONArray("laporan_list");
                        for (int i=0; i<laporan_list.length(); i++){
                            JSONObject o = laporan_list.getJSONObject(i);
                            Laporan laporan = new Laporan();
                            laporan.setId(o.getString("id"));
                            laporan.setLaporan_imej(o.getString("laporan_imej"));
                            laporan.setLaporan_tempat(o.getString("laporan_tempat"));
                            laporan.setLaporan_tarikh(o.getString("laporan_tarikh"));
                            laporan.setLaporan_masa(o.getString("laporan_masa"));
                            laporan.setLaporan_status(o.getString("laporan_status"));
                            laporanList.add(laporan);
                            laporanAdapter.notifyDataSetChanged();

                            if (sharedPreferences.getString("checkDashboard", "").isEmpty()){
                                showToolTipDashboard(0);

                                //Apply Editor
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("checkDashboard", "ada");
                                editor.apply();
                            }
                        }
                    }else{
                        AlertDialog alertDialog = new AlertDialog.Builder(Dashboard.this)
                                .setMessage("Gagal mengakses internet anda")
                                .create();
                        alertDialog.show();
                    }
                }catch (Exception e){ e.printStackTrace(); }

                if(pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog alertDialog = new AlertDialog.Builder(Dashboard.this)
                        .setMessage("Gagal mengakses internet anda")
                        .create();
                alertDialog.show();

                if(pDialog.isShowing())
                    pDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("polis_id", pekerja_id);
                return params;
            }
        };

        requestQueue.add(laporanRequest);
        pDialog.setMessage("Sedang memuat turun data...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnProfil:
                startActivity(new Intent(this, Profil.class));
                break;
        }
    }

    public void showToolTipDashboard(final int i){
        Tooltip tooltip;
        switch (i){
            case 0:
                tooltip = new Tooltip.Builder(findViewById(R.id.rcyLaporan), R.style.Tooltip).setText("DI SINI MERUPAKAN SENARAI LAPORAN YANG TELAH DIAGIHKAN OLEH ADMIN").show();
                break;
            case 1:
                tooltip = new Tooltip.Builder(findViewById(R.id.imgLaporan), R.style.Tooltip).setText("SETIAP LAPORAN AKAN MENUNJUKKAN NAMA TEMPAT LAPORAN ITU DILAPORKAN TARIKH DAN MASA LAPORAN SERTA STATUS LAPORAN TERSEBUT").show();
                break;
            default:
                //SharedPreferences.Editor editor = sharedPreferences.edit();
                //editor.putString("checkInfo", "ada");
                //editor.apply();
                return;
        }
        Timer t = new Timer(false);
        final Tooltip finalTooltip = tooltip;
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        finalTooltip.dismiss();
                        int j = i + 1;
                        showToolTipDashboard(j);
                    }
                });
            }
        }, 4000);
    }
}
