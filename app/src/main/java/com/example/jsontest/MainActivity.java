package com.example.jsontest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";

    TextView degree;
    EditText weight;
    TextView result;
    Button save;
    Switch bleeding;

    MyGridView gridViewColor;
    private ColorAdapter cAdapter;
    private String[] colors; //顏色

    //Api
    private Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        record = new Record();

        initView();

        getDataFromApi();
    }

    private void initView() {
        degree = findViewById(R.id.tvDegree);
        weight = findViewById(R.id.edtWeight);
        weight.setText("55");
        result = findViewById(R.id.tvResult);
        bleeding = findViewById(R.id.swBleeding);       //出血
        save =  findViewById(R.id.btnSave);
        //顏色
        gridViewColor = findViewById(R.id.gvColor);
        setColorData();

        save.setOnClickListener(this);
        bleeding.setOnCheckedChangeListener(this);
    }

    private void setColorData() {
        colors = new String[]{ getString(R.string.normal), getString(R.string.white), getString(R.string.yellow),
                getString(R.string.milky), getString(R.string.brown), getString(R.string.greenish_yellow)};

        cAdapter = new ColorAdapter(this);

        cAdapter.setData(colors, 0);     //導入資料並指定default position
        gridViewColor.setAdapter(cAdapter);
        gridViewColor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                cAdapter.setSelection(position);   //傳直更新
                cAdapter.notifyDataSetChanged();
                Log.d(TAG, "onItemClick: " + position);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                updateToApi();
                break;
        }
    }

    private void getDataFromApi() {
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("type", "3");
            json.put("userId", "H5E3q5MjA=");
            json.put("testDate","2021-01-11");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 建立OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));

        // 建立Request，設置連線資訊
        Request request = new Request.Builder()
                .url("http://192.168.1.108:8080/allAiniita/aplus/RecordInfo")
                .addHeader("Authorization","xxx")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 連線失敗
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 連線成功，自response取得連線結果
                String result = response.body().string();  //字串
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parserJson(result); //解析後台資料
                    }
                });
            }
        });
    }

    //解析後台資料
    private void parserJson(String JsonResult) {
        record = Record.newInstance(JsonResult);

        Log.d(TAG, "後台回來的資料: " + record.toJSONString());

        String day = record.getTestDate(); //日期
        String temperature = String.valueOf(record.getMeasure().getTemperature());  //體溫

//
        String bleed = record.getStatus().getBleeding();
        if (bleed.equals("Y")){
            bleeding.setChecked(true);
        }

//        //顏色
        String secretionsColor = record.getSecretions().getColor();
        RecordColor recordColor = RecordColor.getColor(secretionsColor);
        String name = recordColor.getName();
        int pos_color = recordColor.getIndex();
        cAdapter.setData(colors, pos_color);
        Log.d(TAG, "後台回來的資料 color position : " + pos_color + " color name: " + name);
    }

    private void updateToApi() {
        double userDegree = Double.parseDouble(degree.getText().toString());
        double userWeight = Double.parseDouble(weight.getText().toString());

        record.setType("3");
        record.setUserId("H5E3q5MjA=");
        record.setTestDate("2021-01-11");
        record.getMeasure().setWeight(userWeight);
        record.getMeasure().setTemperature(userDegree);

        Log.d(TAG, "updateToApi: " + record.toJSONString());

//        //建立OkHttpClient
//        OkHttpClient okHttpClient = new OkHttpClient();
//
//        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
//        RequestBody requestBody = RequestBody.create(JSON, record.toJSONString());
//
//        // 建立Request，設置連線資訊 update Api name:Record
//        Request request = new Request.Builder()
//                .url("http://192.168.1.108:8080/allAiniita/aplus/Record")
//                .addHeader("Authorization","xxx")
//                .post(requestBody)
//                .build();
//
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // 連線失敗
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                // 連線成功，自response取得連線結果
//                String JsonResult = response.body().string();  //字串
////                Log.d(TAG, "onResponse: " + JsonResult);
//                result.setText("後台回覆: "+JsonResult);
//            }
//        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
        switch (compoundButton.getId()){
            case R.id.swBleeding:
                if(isCheck){
                    record.getStatus().setBleeding("Y");
                }else {
                    record.getStatus().setBleeding("N");
                }
                break;
        }
    }
}