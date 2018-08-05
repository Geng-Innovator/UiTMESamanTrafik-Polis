package com.example.zaimfared.uitmereport_polis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.List;

public class LaporanBaru extends AppCompatActivity implements View.OnClickListener {

    ImageView ivGambarLaporan;
    EditText etTempat, etNoKenderaan, etNoSiriPelekat, etNoPelajar, etNamaPelajar, etPenerangan;
    Spinner spnJenisKenderaan, spnKursus, spnKolej, spnFakulti;
    RecyclerView rvSenaraiKesalahan;

    KesalahanAdapter kesalahanAdapter;

    List<LookUp> kesalahanList, kenderaanList, kursusList, kolejList, fakultiList;
    ArrayList<String> senaraiJenisKesalahan, senaraiJenisKenderaan, senaraiKursus, senaraiKolej, senaraiFakulti;
    ArrayAdapter<String> kenderaanAdapter, kursusAdapter, kolejAdapter, fakultiAdapter;

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
                break;
            case R.id.rvSenaraiKesalahan:
                break;
            case R.id.btnLapor:
                hantarLaporan();
                break;
            case R.id.btnKembali:
                finish();
                break;
        }
    }

    private void initialize() {
        senaraiJenisKesalahan = new ArrayList<>();
        senaraiJenisKenderaan = new ArrayList<>();
        senaraiKursus = new ArrayList<>();
        senaraiKolej = new ArrayList<>();
        senaraiFakulti = new ArrayList<>();

        ivGambarLaporan = findViewById(R.id.ivGambarLaporan);
        etTempat = findViewById(R.id.etTempat);
        etNoKenderaan = findViewById(R.id.etNoKenderaan);
        etNoSiriPelekat = findViewById(R.id.etNoSiriPelekat);
        etNoPelajar = findViewById(R.id.etNoPelajar);
        etNamaPelajar = findViewById(R.id.etNamaPelajar);
        etPenerangan = findViewById(R.id.etPenerangan);
        spnJenisKenderaan = findViewById(R.id.spnJenisKenderaan);
        spnKursus = findViewById(R.id.spnKursus);
        spnKolej = findViewById(R.id.spnKolej);
        spnFakulti = findViewById(R.id.spnFakulti);
        rvSenaraiKesalahan = findViewById(R.id.rvSenaraiKesalahan);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvSenaraiKesalahan.setLayoutManager(layoutManager);
        rvSenaraiKesalahan.setItemAnimator(new DefaultItemAnimator());

        kesalahanAdapter = new KesalahanAdapter(senaraiJenisKesalahan);
        kenderaanAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, senaraiJenisKenderaan);
        kursusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, senaraiKursus);
        kolejAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, senaraiKolej);
        fakultiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, senaraiFakulti);

        prepareForm();

        spnJenisKenderaan.setAdapter(kenderaanAdapter);
        rvSenaraiKesalahan.setAdapter(kesalahanAdapter);
        spnKursus.setAdapter(kursusAdapter);
        spnKolej.setAdapter(kolejAdapter);
        spnFakulti.setAdapter(fakultiAdapter);
    }

    private void prepareForm (){
        kenderaanList = new ArrayList<>();
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
                        for (LookUp temp : kesalahanList)
                            senaraiJenisKesalahan.add(temp.getName());
                        for (LookUp temp : kursusList)
                            senaraiKursus.add(temp.getName());
                        for (LookUp temp : kolejList)
                            senaraiKolej.add(temp.getName());
                        for (LookUp temp : fakultiList)
                            senaraiFakulti.add(temp.getName());

                        kenderaanAdapter.notifyDataSetChanged();
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

    }
}
