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

import static com.example.jsontest.ApiProxy.RECORD_INFO;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";

    TextView degree;
    EditText weight;
    TextView result;
    Button save, getInfo;
    Switch bleeding;

    MyGridView gridViewColor;
    private ColorAdapter cAdapter;
    private String[] colors; //顏色
    private RecordColor recordColor;

    //Api
    private Record record;
    private ApiProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        record = new Record();
        proxy = ApiProxy.getInstance();

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
        getInfo = findViewById(R.id.btnGet);

        gridViewColor = findViewById(R.id.gvColor);

        save.setOnClickListener(this);
        getInfo.setOnClickListener(this);
        bleeding.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                updateToApi();
                break;
            case R.id.btnGet:
                getDataFromApi();
                break;
        }
    }

    private void getDataFromApi() {

        JSONObject json = new JSONObject();
        try {
            json.put("type", "3");
            json.put("userId", "H5E3q5MjA=");
            json.put("testDate","2021-01-12");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        proxy.buildPOST(RECORD_INFO, json.toString(), requestListeren);
    }

    private ApiProxy.OnApiListener requestListeren = new ApiProxy.OnApiListener() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onSuccess(JSONObject result) {
            Log.d(TAG, "成功後回覆1: " + result);
            runOnUiThread(new Runnable() { //需要另外開線程
                @Override
                public void run() {
                    parser(result); //解析
                }
            });

        }

        @Override
        public void onFailure(String message) {

        }

        @Override
        public void onPostExecute() {

        }
    };

    //解析
    private void parser(JSONObject result) {
        record = Record.newInstance(result.toString());
        Log.d(TAG, "成功後回覆2: " + record.toJSONString());

        String bleed = record.getStatus().getBleeding();
        if (bleed.equals("Y")){
            bleeding.setChecked(true);
        }

        setColor();
    }

    private void setColor() {
        String secretionsColor = record.getSecretions().getColor();
        recordColor = RecordColor.getColor(secretionsColor);
        int pos_color = recordColor.getIndex();

        colors = new String[]{ getString(R.string.normal), getString(R.string.white), getString(R.string.yellow),
                getString(R.string.milky), getString(R.string.brown), getString(R.string.greenish_yellow)};

        cAdapter = new ColorAdapter(this);
        cAdapter.setData(colors, pos_color);
        gridViewColor.setAdapter(cAdapter);
        gridViewColor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                cAdapter.setSelection(position);   //傳直更新
                cAdapter.notifyDataSetChanged();
                recordColor = RecordColor.getEnName(position);
                String enName = recordColor.getName();
                Log.d(TAG, "Touch OnClick: " + enName);
                record.getSecretions().setColor(enName);
            }
        });
    }

    //更新資訊
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