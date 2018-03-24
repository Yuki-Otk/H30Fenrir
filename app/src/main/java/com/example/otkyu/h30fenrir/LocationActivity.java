package com.example.otkyu.h30fenrir;

/**
 * Created by YukiOtake on 2018/01/24 024.
 * src:https://akira-watson.com/android/gps.html
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.otkyu.h30fenrir.asynchronous.api.GnaviAPI;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviRequestEntity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Date;

public class LocationActivity extends AppCompatActivity {

    // Fused Location Provider API.
    private FusedLocationProviderClient fusedLocationClient;

    // Location Settings APIs.
    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location location;

    private String lastUpdateTime;
    private Boolean requestingLocationUpdates;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private int priority = 0;
    //    private TextView textView;
    private double[] gps = new double[2];
    private GnaviAPI gnaviAPI;
    GnaviRequestEntity gnaviRequestEntity;
    TextView pageTextView;
    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8, checkBox9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);
        priority = 0;
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        //Sprinner(プルダウン)
        Spinner spinner = (Spinner) findViewById(R.id.genre_spinner);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
        checkBox6 = (CheckBox) findViewById(R.id.checkBox6);
        checkBox7 = (CheckBox) findViewById(R.id.checkBox7);
        checkBox8 = (CheckBox) findViewById(R.id.checkBox8);
        checkBox9 = (CheckBox) findViewById(R.id.checkBox9);
        doSelectSprinner(spinner);
        //スニークバー
        SeekBar seekBar = (SeekBar) findViewById(R.id.page_seekBar);//pageスニーク
        pageTextView = (TextView) findViewById(R.id.page_textView);//page表示
        String num = String.valueOf(seekBar.getProgress());
        pageTextView.setText(num);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {//動かしたとき
                if (position == 0) {
                    seekBar.setProgress(1);
                    position = 1;
                }
                String num = String.valueOf(position);
                pageTextView.setText(num);
                pageTextView.setTypeface(Typeface.DEFAULT_BOLD);
                pageTextView.setTextColor(Color.RED);
                pageTextView.setTextSize(20);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // 検索
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gnaviAPI = new GnaviAPI();//検索ボタン押した段階で初期化しないと何回も呼べない
                boolean flag = gnaviRequest();
                if (flag) {
                    jump();
                } else {
                    String str = "検索結果はありませんでした。";
                    Toast.makeText(LocationActivity.this, str, Toast.LENGTH_LONG).show();
                    System.out.println(str);
                }
            }
        });
        startLocationUpdates();//強制開始
    }

    private void doSelectSprinner(Spinner spinner) {//seekBarをselectした際の挙動
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String select = (String) adapterView.getSelectedItem();
                Toast.makeText(LocationActivity.this, select, Toast.LENGTH_SHORT).show();
                Log.d("sprinner", select);
                changeCheckBox(select);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void changeCheckBox(String str) {//seekBarに変更があればcheckboxを変換
        unCheck();//変更があればすべてのチェックを外す
        switch (str) {
            case "料理・ジャンル":
                doCheckBox(0);
                break;
            case "和食":
                doCheckBox(9);
                checkBox1.setText("焼き鳥");
                checkBox2.setText("寿司");
                checkBox3.setText("そば");
                checkBox4.setText("とんかつ");
                checkBox5.setText("焼肉");
                checkBox6.setText("お好み焼き");
                checkBox7.setText("うどん");
                checkBox8.setText("たこ焼き");
                checkBox9.setText("うなぎ");
                break;
            case "イタリアン":
                doCheckBox(2);
                checkBox1.setText("ピザ");
                checkBox2.setText("パスタ");
                break;
            case "中華":
                doCheckBox(3);
                checkBox1.setText("ラーメン");
                checkBox2.setText("麻婆豆腐");
                checkBox3.setText("餃子");
                break;
            case "もっと絞り込み":

                break;
        }
    }

    private void doCheckBox(int num) {//checkboxの表示を変更
        String str;
        int huga=0;
        for(int i=0;i<num;i++){
            huga=huga*10;
            huga++;
        }
        for(int i=0;i<9-num;i++){
            huga=huga*10;
        }
        str= String.valueOf(huga);
        if (huga == 0) {
            str="000000000";
        }
        if (str.length() == 9) {
            for (int i = 1; i <= 9; i++) {
                String hoge = String.valueOf(str.charAt(i - 1));
                if (i == 1 && hoge.equals("1")) {
                    checkBox1.setVisibility(View.VISIBLE);
                } else if (i == 1) {
                    checkBox1.setVisibility(View.INVISIBLE);
                }
                if (i == 2 && hoge.equals("1")) {
                    checkBox2.setVisibility(View.VISIBLE);
                } else if (i == 2) {
                    checkBox2.setVisibility(View.INVISIBLE);
                }
                if (i == 3 && hoge.equals("1")) {
                    checkBox3.setVisibility(View.VISIBLE);
                } else if (i == 3) {
                    checkBox3.setVisibility(View.INVISIBLE);
                }
                if (i == 4 && hoge.equals("1")) {
                    checkBox4.setVisibility(View.VISIBLE);
                } else if (i == 4) {
                    checkBox4.setVisibility(View.INVISIBLE);
                }
                if (i == 5 && hoge.equals("1")) {
                    checkBox5.setVisibility(View.VISIBLE);
                } else if (i == 5) {
                    checkBox5.setVisibility(View.INVISIBLE);
                }
                if (i == 6 && hoge.equals("1")) {
                    checkBox6.setVisibility(View.VISIBLE);
                } else if (i == 6) {
                    checkBox6.setVisibility(View.INVISIBLE);
                }
                if (i == 7 && hoge.equals("1")) {
                    checkBox7.setVisibility(View.VISIBLE);
                } else if (i == 7) {
                    checkBox7.setVisibility(View.INVISIBLE);
                }
                if (i == 8 && hoge.equals("1")) {
                    checkBox8.setVisibility(View.VISIBLE);
                } else if (i == 8) {
                    checkBox8.setVisibility(View.INVISIBLE);
                }
                if (i == 9 && hoge.equals("1")) {
                    checkBox9.setVisibility(View.VISIBLE);
                } else if (i == 9) {
                    checkBox9.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
    private void unCheck(){//すべてのチェックを外す
        checkBox1.setChecked(false);
        checkBox2.setChecked(false);
        checkBox3.setChecked(false);
        checkBox4.setChecked(false);
        checkBox5.setChecked(false);
        checkBox6.setChecked(false);
        checkBox7.setChecked(false);
        checkBox8.setChecked(false);
        checkBox9.setChecked(false);
    }

    private boolean gnaviRequest() {
        gnaviRequestEntity = new GnaviRequestEntity();
        gnaviRequestEntity.setGps(gps);//gps情報をセット
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);// ラジオグループのオブジェクトを取得
        int id = radioGroup.getCheckedRadioButtonId();// チェックされているラジオボタンの ID を取得
        RadioButton radioButton = (RadioButton) findViewById(id);// チェックされているラジオボタンオブジェクトを取得
        String checkStr = radioButton.getText().toString();
        gnaviRequestEntity.setRange(checkStr);//範囲をセット
        String page = (String) pageTextView.getText();
        if (page.equals("")) {
            page = "20";
        }
        System.out.println("page=" + page);
        try {
            int hoge = Integer.parseInt(page);
        } catch (Exception e) {
            System.out.println("error=" + e);
            Toast.makeText(LocationActivity.this, "ページ数に誤りがあります", Toast.LENGTH_LONG).show();
            return false;
        }
        if (Integer.parseInt(page) > 100) {
            Toast.makeText(LocationActivity.this, "100件以上一度に表示することはできません", Toast.LENGTH_LONG).show();
            return false;
        }
//        int page= Integer.parseInt(temp);
        EditText keywordEditText = (EditText) findViewById(R.id.keyword_editText);
        String freeword = keywordEditText.getText().toString();//keyWordをセット
        freeword=addKeyWord(freeword);
        gnaviRequestEntity.setFreeword(freeword);//フリーワード検索をセット
        gnaviRequestEntity.setPage(page);
        gnaviAPI.setGnaviRequestEntity(gnaviRequestEntity);
        boolean flag = false;
        gnaviAPI.execute();
        while (true) {//api結果取得するまでweit
            if (GnaviAPI.isResultFlag()) {
                if (GnaviAPI.isFinishFlag()) {
                    return true;
                }
            } else if (GnaviAPI.isFinishFlag()) {
                return false;
            }

        }
    }
    private String addKeyWord(String str){
        String add="";
        if(checkBox1.isChecked()){
            add=add+"%20"+checkBox1.getText();
        }
        Log.d("api",str+"%20"+add);
        return str+"%20"+add;
    }

    private void jump() {
        startActivity(ShowListActivity.createIntent(gnaviRequestEntity, getApplication()));
    }

    // locationのコールバックを受け取る
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                location = locationResult.getLastLocation();
                lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };
    }

    private void updateLocationUI() {
        // getLastLocation()からの情報がある場合のみ
        if (location != null) {

            String fusedName[] = {
                    "Latitude", "Longitude", "Accuracy",
                    "Altitude", "Speed", "Bearing"
            };

            double fusedData[] = {
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getAccuracy(),
                    location.getAltitude(),
                    location.getSpeed(),
                    location.getBearing()
            };

            StringBuilder stringBuilder = new StringBuilder("---------- UpdateLocation ---------- \n");

            for (int i = 0; i < fusedName.length; i++) {
                stringBuilder.append(fusedName[i]);
                stringBuilder.append(" = ");
                stringBuilder.append(String.valueOf(fusedData[i]));
                stringBuilder.append("\n");
            }
            System.out.println(fusedData[0] + ":" + fusedData[1]);
            setGps(fusedData[0], fusedData[1]);
            stringBuilder.append("Time");
            stringBuilder.append(" = ");
            stringBuilder.append(lastUpdateTime);
            stringBuilder.append("\n");
        }

    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();

        if (priority == 0) {
            // 高い精度の位置情報を取得したい場合
            // インターバルを例えば5000msecに設定すれば
            // マップアプリのようなリアルタイム測位となる
            // 主に精度重視のためGPSが優先的に使われる
            locationRequest.setPriority(
                    LocationRequest.PRIORITY_HIGH_ACCURACY);

        } else if (priority == 1) {
            // バッテリー消費を抑えたい場合、精度は100mと悪くなる
            // 主にwifi,電話網での位置情報が主となる
            // この設定の例としては　setInterval(1時間)、setFastestInterval(1分)
            locationRequest.setPriority(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        } else if (priority == 2) {
            // バッテリー消費を抑えたい場合、精度は10kmと悪くなる
            locationRequest.setPriority(
                    LocationRequest.PRIORITY_LOW_POWER);

        } else {
            // 受け身的な位置情報取得でアプリが自ら測位せず、
            // 他のアプリで得られた位置情報は入手できる
            locationRequest.setPriority(
                    LocationRequest.PRIORITY_NO_POWER);
        }

        // アップデートのインターバル期間設定
        // このインターバルは測位データがない場合はアップデートしません
        // また状況によってはこの時間よりも長くなることもあり
        // 必ずしも正確な時間ではありません
        // 他に同様のアプリが短いインターバルでアップデートしていると
        // それに影響されインターバルが短くなることがあります。
        // 単位：msec
        locationRequest.setInterval(60000);
        // このインターバル時間は正確です。これより早いアップデートはしません。
        // 単位：msec
        locationRequest.setFastestInterval(5000);

    }

    // 端末で測位できる状態か確認する。wifi, GPSなどがOffになっているとエラー情報のダイアログが出る
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("debug", "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("debug", "User chose not to make required location settings changes.");
                        requestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    // FusedLocationApiによるlocation updatesをリクエスト
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this,
                        new OnSuccessListener<LocationSettingsResponse>() {
                            @Override
                            public void onSuccess(
                                    LocationSettingsResponse locationSettingsResponse) {
                                Log.i("debug", "All location settings are satisfied.");

                                // パーミッションの確認
                                if (ActivityCompat.checkSelfPermission(
                                        LocationActivity.this,
                                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(
                                        LocationActivity.this,
                                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {

                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                fusedLocationClient.requestLocationUpdates(
                                        locationRequest, locationCallback, Looper.myLooper());

                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("debug", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(
                                            LocationActivity.this,
                                            REQUEST_CHECK_SETTINGS);

                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("debug", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                                String errorMessage = "Location settings are inadequate, and cannot be " +"fixed here. Fix in Settings.";
                                String errorMessage = "GPSを取得できない設定になっています。\n設定を変更してください。";
                                Log.e("debug", errorMessage);
                                Toast.makeText(LocationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                requestingLocationUpdates = false;
                        }
                    }
                });
        requestingLocationUpdates = true;
    }

    private void stopLocationUpdates() {
        if (!requestingLocationUpdates) {
            Log.d("debug", "stopLocationUpdates: " +
                    "updates never requested, no-op.");
            return;
        }

        fusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                requestingLocationUpdates = false;
                            }
                        });
    }

    //端末の戻るボタンを押下したとき
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //homeに戻る
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // バッテリー消費を鑑みLocation requestを止める
        stopLocationUpdates();
    }

    public void setGps(double lat, double lon) {
        gps[0] = lat;
        gps[1] = lon;
        this.gps = gps;
    }

    public double[] getGps() {
        return gps;
    }
}
