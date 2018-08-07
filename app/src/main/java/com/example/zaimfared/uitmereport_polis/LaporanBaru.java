package com.example.zaimfared.uitmereport_polis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.zaimfared.uitmereport_polis.InfoLaporan.encodeToBase64;

public class LaporanBaru extends AppCompatActivity implements View.OnClickListener {

    public static final String id = "ID";
    public static final String pekerjaPrefs = "pekerjaPref";

    ImageView ivGambarLaporan;
    EditText etTempat, etNoKenderaan, etNoSiriPelekat, etNoPelajar, etNamaPelajar, etPenerangan;
    Spinner spnJenisKenderaan, spnStatusKenderaan, spnKursus, spnKolej, spnFakulti;
    RecyclerView rvSenaraiKesalahan;

    String polis_id, polis_imej;
    SharedPreferences sharedPreferences;
    List<LookUp> kesalahanList, kenderaanList, kenderaanStatusList, kursusList, kolejList, fakultiList;
    ArrayList<String> senaraiJenisKesalahan, senaraiJenisKenderaan, senaraiStatusKenderaan, senaraiKursus, senaraiKolej, senaraiFakulti;
    ArrayAdapter<String> kenderaanAdapter, statusKenderaanAdapter, kursusAdapter, kolejAdapter, fakultiAdapter;
    ArrayList<Long> kesalahanIndexList;
    KesalahanAdapter kesalahanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_baru);

        initialize();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ivGambarLaporan:
                captureImage();
                break;
            case R.id.rvSenaraiKesalahan:
                break;
            case R.id.btnLapor:
                if(polis_imej != null) {
                    for (int i=0 ; i<kesalahanList.size() ; i++){
                        View row = rvSenaraiKesalahan.getLayoutManager().findViewByPosition(i);
                        CheckBox chkBox = row.findViewById(R.id.chkBoxRowKesalahan);
                        if (chkBox.isChecked())
                            kesalahanIndexList.add(kesalahanList.get(i).getId());
                    }

                    hantarLaporan();
                } else
                    Toast.makeText(this, "Sila tangkap gambar dahulu.", Toast.LENGTH_LONG).show();
                break;
            case R.id.btnKembali:
                finish();
                break;
        }
    }

    public void captureImage(){
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data.getExtras() != null){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                polis_imej = encodeToBase64(bitmap);
                ivGambarLaporan.setImageBitmap(bitmap);
                findViewById(R.id.txtSentuh).setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initialize() {
        kesalahanIndexList = new ArrayList<>();

        senaraiJenisKesalahan = new ArrayList<>();
        senaraiJenisKenderaan = new ArrayList<>();
        senaraiStatusKenderaan = new ArrayList<>();
        senaraiKursus = new ArrayList<>();
        senaraiKolej = new ArrayList<>();
        senaraiFakulti = new ArrayList<>();

        sharedPreferences = getSharedPreferences(pekerjaPrefs, Context.MODE_PRIVATE);
        polis_id = sharedPreferences.getString(id, "");

        ivGambarLaporan = findViewById(R.id.ivGambarLaporan);
        etTempat = findViewById(R.id.etTempat);
        etNoKenderaan = findViewById(R.id.etNoKenderaan);
        etNoSiriPelekat = findViewById(R.id.etNoSiriPelekat);
        etNoPelajar = findViewById(R.id.etNoPelajar);
        etNamaPelajar = findViewById(R.id.etNamaPelajar);
        etPenerangan = findViewById(R.id.etPenerangan);
        spnJenisKenderaan = findViewById(R.id.spnJenisKenderaan);
        spnStatusKenderaan = findViewById(R.id.spnStatusKenderaan);
        spnKursus = findViewById(R.id.spnKursus);
        spnKolej = findViewById(R.id.spnKolej);
        spnFakulti = findViewById(R.id.spnFakulti);
        rvSenaraiKesalahan = findViewById(R.id.rvSenaraiKesalahan);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvSenaraiKesalahan.setLayoutManager(layoutManager);
        rvSenaraiKesalahan.setItemAnimator(new DefaultItemAnimator());

        kesalahanAdapter = new KesalahanAdapter(senaraiJenisKesalahan);
        kenderaanAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, senaraiJenisKenderaan);
        statusKenderaanAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, senaraiStatusKenderaan);
        kursusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, senaraiKursus);
        kolejAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, senaraiKolej);
        fakultiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, senaraiFakulti);

        prepareForm();

        rvSenaraiKesalahan.setAdapter(kesalahanAdapter);
        spnJenisKenderaan.setAdapter(kenderaanAdapter);
        spnStatusKenderaan.setAdapter(statusKenderaanAdapter);
        spnKursus.setAdapter(kursusAdapter);
        spnKolej.setAdapter(kolejAdapter);
        spnFakulti.setAdapter(fakultiAdapter);
    }

    private void prepareForm (){
        kenderaanList = new ArrayList<>();
        kenderaanStatusList = new ArrayList<>();
        kesalahanList = new ArrayList<>();
        kursusList = new ArrayList<>();
        kolejList = new ArrayList<>();
        fakultiList = new ArrayList<>();

        String url = getResources().getString(R.string.url_form_hantar_laporan); // Url hantar laporan
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest maklumRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Get the JSON object from the server, Response will return status and data
                    JSONObject obj = new JSONObject(response);
                    // Get status from the server. 0 - Failed, 1 - Success

                    if (obj.getString("status").equalsIgnoreCase("1")) {
                        JSONObject data = obj.getJSONObject("data");

                        JSONArray jenisKenderaan = data.getJSONArray("jenisKenderaanList");
                        for(int i=0 ; i<jenisKenderaan.length() ; i++) {
                            JSONObject temp = jenisKenderaan.getJSONObject(i);
                            LookUp lookup = new LookUp(temp.getInt("id"), temp.getString("nama"));

                            kenderaanList.add(lookup);
                        }

                        JSONArray statusKenderaan = data.getJSONArray("statusKenderaanList");
                        for(int i=0 ; i<statusKenderaan.length() ; i++) {
                            JSONObject temp = statusKenderaan.getJSONObject(i);
                            LookUp lookup = new LookUp(temp.getInt("id"), temp.getString("nama"));

                            kenderaanStatusList.add(lookup);
                        }

                        JSONArray jenisKesalahan = data.getJSONArray("jenisKesalahanList");
                        for(int i=0 ; i<jenisKesalahan.length() ; i++) {
                            JSONObject temp = jenisKesalahan.getJSONObject(i);
                            LookUp lookup = new LookUp(temp.getInt("id"), temp.getString("nama"));

                            kesalahanList.add(lookup);
                        }

                        JSONArray jenisKursus = data.getJSONArray("jenisKursusList");
                        for(int i=0 ; i<jenisKursus.length() ; i++) {
                            JSONObject temp = jenisKursus.getJSONObject(i);
                            LookUp lookup = new LookUp(temp.getInt("id"), temp.getString("nama"));

                            kursusList.add(lookup);
                        }

                        JSONArray jenisKolej = data.getJSONArray("jenisKolejList");
                        for(int i=0 ; i<jenisKolej.length() ; i++) {
                            JSONObject temp = jenisKolej.getJSONObject(i);
                            LookUp lookup = new LookUp(temp.getInt("id"), temp.getString("nama"));

                            kolejList.add(lookup);
                        }

                        JSONArray jenisFakulti = data.getJSONArray("jenisFakultiList");
                        for(int i=0 ; i<jenisFakulti.length() ; i++) {
                            JSONObject temp = jenisFakulti.getJSONObject(i);
                            LookUp lookup = new LookUp(temp.getInt("id"), temp.getString("nama"));

                            fakultiList.add(lookup);
                        }

                        for (LookUp temp : kenderaanList)
                            senaraiJenisKenderaan.add(temp.getName());
                        for (LookUp temp : kenderaanStatusList)
                            senaraiStatusKenderaan.add(temp.getName());
                        for (LookUp temp : kesalahanList)
                            senaraiJenisKesalahan.add(temp.getName());
                        for (LookUp temp : kursusList)
                            senaraiKursus.add(temp.getName());
                        for (LookUp temp : kolejList)
                            senaraiKolej.add(temp.getName());
                        for (LookUp temp : fakultiList)
                            senaraiFakulti.add(temp.getName());

                        kenderaanAdapter.notifyDataSetChanged();
                        statusKenderaanAdapter.notifyDataSetChanged();
                        kesalahanAdapter.notifyDataSetChanged();
                        kursusAdapter.notifyDataSetChanged();
                        kolejAdapter.notifyDataSetChanged();
                        fakultiAdapter.notifyDataSetChanged();
                    } else { Toast.makeText(LaporanBaru.this, "Terdapat masalah", Toast.LENGTH_SHORT).show(); }
                } catch (JSONException e) { e.printStackTrace(); }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LaporanBaru.this, "Terdapat masalah", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(maklumRequest);
    }

    private void hantarLaporan() {
        String url = getResources().getString(R.string.url_hantar_laporan);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest hantarLaporan = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(LaporanBaru.this, "Laporan telah dihantar", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LaporanBaru.this, "Terdapat masalah", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("polis_id", polis_id);
                params.put("polis_imej", polis_imej);
                params.put("polis_penerangan", etPenerangan.getText().toString().trim());
                params.put("laporan_tempat", etTempat.getText().toString().trim());
                params.put("kenderaan_no_siri_pelekat", etNoSiriPelekat.getText().toString().trim());
                params.put("kenderaan_no", etNoKenderaan.getText().toString().trim());
                params.put("kenderaan_jenis", "" + kenderaanList.get(spnJenisKenderaan.getSelectedItemPosition()).getId());
                params.put("kenderaan_status", "" + kenderaanStatusList.get(spnStatusKenderaan.getSelectedItemPosition()).getId());
                params.put("pelajar_no", etNoPelajar.getText().toString().trim());
                params.put("pelajar_nama", etNamaPelajar.getText().toString().trim());
                params.put("pelajar_kursus", "" + kursusList.get(spnKursus.getSelectedItemPosition()).getId());
                params.put("pelajar_kolej", "" + kolejList.get(spnKolej.getSelectedItemPosition()).getId());
                params.put("pelajar_fakulti", "" + fakultiList.get(spnFakulti.getSelectedItemPosition()).getId());
                params.put("kesalahan_list", new JSONArray(kesalahanIndexList).toString());

                return params;
            }
        };

        requestQueue.add(hantarLaporan);
    }
}
