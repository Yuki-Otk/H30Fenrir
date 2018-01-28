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
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
//import android.widget.TextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.example.otkyu.h30fenrir.model.GnaviAPI;
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
//    private String textLog;
    private double[] gps = new double[2];
    private GnaviAPI gnaviAPI = new GnaviAPI();

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

//        textView = (TextView) findViewById(R.id.text_view);
//        textLog = "onCreate()\n";
//        textView.setText(textLog);

        // 測位開始
//        Button buttonStart = (Button) findViewById(R.id.button_start);
//        buttonStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startLocationUpdates();
//            }
//        });

        // 検索
        Button buttonSearch = (Button) findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                System.out.println("gps1="+gps[0]);
//                stopLocationUpdates();//画面移動した際に強制的に終了されるため書かなくてもよい?
                boolean flag = gnaviRequest();
                ;
                if (flag) {
//                    List<GnaviResultEntity> list = GnaviAPI.getList();
//                    System.out.println("list is=" + list.size());
//                    for(int i=0;i<list.size();i++){
//                        System.out.println("list name "+list.get(i).getName()+"index is "+i);
//                    }
                    jump();
                } else {
                    System.out.println("検索することができませんでした。");
                }
            }
        });

        startLocationUpdates();//強制開始

    }

    private boolean gnaviRequest() {
        // ラジオグループのオブジェクトを取得
        RadioGroup rg = (RadioGroup)findViewById(R.id.radiogroup);
        // チェックされているラジオボタンの ID を取得
        int id = rg.getCheckedRadioButtonId();
        // チェックされているラジオボタンオブジェクトを取得
        RadioButton radioButton = (RadioButton)findViewById(id);
        System.out.println("radio="+radioButton.getText().toString());
        EditText keywordEditText = (EditText) findViewById(R.id.keyword_editText);
        System.out.println("keyword="+keywordEditText.getText().toString());
        boolean flag = false;
        gnaviAPI.setGps(gps);
        gnaviAPI.execute();
        while (true) {
            if (GnaviAPI.isResultFlag()) {
                if (GnaviAPI.isFinishFlag()) {
                    return true;
                }
            } else if (GnaviAPI.isFinishFlag()) {
                return false;
            }
        }
    }

    private void jump() {
        Intent intent = new Intent(getApplication(), ShowListActivity.class);
//        GnaviRequestEntity gnaviRequestEntity = new GnaviRequestEntity();
//        gnaviRequestEntity.setGps(gps);
//        intent.putExtra("entity", gnaviRequestEntity);
//        intent.setAction(Intent.ACTION_VIEW);

//        String str="hogehoge";
//        intent.putExtra("MESSAGE1", str);
        startActivity(intent);//listをglobal変数にしたため何も持って行かない
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

            StringBuilder strBuf =
                    new StringBuilder("---------- UpdateLocation ---------- \n");

            for (int i = 0; i < fusedName.length; i++) {
                strBuf.append(fusedName[i]);
                strBuf.append(" = ");
                strBuf.append(String.valueOf(fusedData[i]));
                strBuf.append("\n");
            }
            System.out.println(fusedData[0] + ":" + fusedData[1]);
            setGps(fusedData[0], fusedData[1]);
            strBuf.append("Time");
            strBuf.append(" = ");
            strBuf.append(lastUpdateTime);
            strBuf.append("\n");

//            textLog += strBuf;
//            textView.setText(textLog);
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
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("debug", errorMessage);
                                Toast.makeText(LocationActivity.this,
                                        errorMessage, Toast.LENGTH_LONG).show();

                                requestingLocationUpdates = false;
                        }

                    }
                });

        requestingLocationUpdates = true;
    }

    private void stopLocationUpdates() {
//        textLog += "onStop()\n";
//        textView.setText(textLog);

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

    @Override
    protected void onPause() {
        super.onPause();
        // バッテリー消費を鑑みLocation requestを止める
        stopLocationUpdates();
    }

    public void setGps(double a, double b) {
        gps[0] = a;
        gps[1] = b;
        this.gps = gps;
    }

    public double[] getGps() {
        return gps;
    }
}
