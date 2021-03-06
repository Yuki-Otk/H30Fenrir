package com.example.otkyu.h30fenrir;

/**
 * Created by YukiOtake on 2018/01/24 024.
 * src:https://akira-watson.com/android/gps.html
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuItem;
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
    private Boolean requestingLocationUpdates, modeFlag;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private int priority = 0;
    private double[] gps = new double[2];
    private GnaviAPI gnaviAPI;
    private GnaviRequestEntity gnaviRequestEntity;
    private TextView pageTextView, rangeTextView;
    private final int CHECKBOX_NUM = 9;//checkBoxの使用する数を固定値にしておく
    private CheckBox[] checkBoxes = new CheckBox[CHECKBOX_NUM];
    private Spinner spinner;
    private SeekBar seekBar;
    private RadioGroup radioGroup;
    private Button searchButton;
    private String rangeString;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);
        init();//viewを読み込み&初期化
        doGpsGet();//gpsを取得する
        onRadioClick();//radioボタンが変更されたらイベント
        radioGroup.check(R.id.five_m_radioButton);//初期値をチェックする
        onSelectSprinner();//spinnerが変更されたらイベント
        onChengeSeekBar();//SeekBarを変更したらイベント
        onClickButton();//検索ボタンが押されたら
        startLocationUpdates();//GPS読み込み強制開始
    }

    private void init() {//view、変数の初期化
        checkBoxes[0] = findViewById(R.id.checkBox1);
        checkBoxes[1] = findViewById(R.id.checkBox2);
        checkBoxes[2] = findViewById(R.id.checkBox3);
        checkBoxes[3] = findViewById(R.id.checkBox4);
        checkBoxes[4] = findViewById(R.id.checkBox5);
        checkBoxes[5] = findViewById(R.id.checkBox6);
        checkBoxes[6] = findViewById(R.id.checkBox7);
        checkBoxes[7] = findViewById(R.id.checkBox8);
        checkBoxes[8] = findViewById(R.id.checkBox9);
        seekBar = findViewById(R.id.page_seekBar);//pageスニーク
        pageTextView = findViewById(R.id.page_textView);//page表示
        radioGroup = findViewById(R.id.radiogroup);// ラジオグループのオブジェクトを取得
        rangeTextView = findViewById(R.id.range_textView);//徒歩何分か表示
        rangeString = getString(R.string.rangeJa);//strings.xmlのrangeJaを取得
        searchButton = findViewById(R.id.search_button);// 検索
        spinner = findViewById(R.id.genre_spinner);
        modeFlag = false;//制限モードかのフラグ(true=制限モード)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//アクションバーにメニューを表示させる
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menuItem = menu.findItem(R.id.imgSwich_menu);//アクションバーのアイコン
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//アクションバーのメニューを選択した時のイベント
        switch (item.getItemId()) {
            case R.id.imgSwich_menu:
                doShowAlertDialog();//AlertDialogを表示
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doShowAlertDialog() {//AlertDialogを表示
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("通信量の削減モードにしますか?");//タイトル
        builder.setMessage("通信量の削減モードでは画像の読み込みを停止します。これにより最低限の通信のみとなります。\n停止しますか?");//内容
        builder.setPositiveButton("制限モード",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(LocationActivity.this, "制限モード", Toast.LENGTH_SHORT).show();
                        menuItem.setIcon(R.mipmap.ic_image_off);//アイコンを変更
                        modeFlag = true;//モードのフラグを立てる

                    }
                });
        builder.setNegativeButton("通常モード",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(LocationActivity.this, "通常モード", Toast.LENGTH_SHORT).show();
                        menuItem.setIcon(R.mipmap.ic_image_on);//アイコンを変更
                        modeFlag = false;//モードのフラグを下げる
                    }
                });
        builder.show();
    }

    private void onClickButton() {//ボタンが押されたら
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gnaviAPI = new GnaviAPI();//検索ボタン押した段階で初期化しないと何回も呼べない
                boolean flag = gnaviRequest();//実行し検索結果があるかどうか
                if (flag) {//検索結果があれば
                    jump();//一覧を表示
                } else {
                    String str = "検索結果はありませんでした。";
                    Toast.makeText(LocationActivity.this, str, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void onChengeSeekBar() {//SeekBarを変更したら
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {//動かしたとき
                if (position == 0) {//0には設定できない
                    seekBar.setProgress(1);//強制セット
                    position = 1;//positionを上書き
                }
                String num = String.valueOf(position);//文字に変換
                pageTextView.setText(num);//値を表示
                pageTextView.setTypeface(Typeface.DEFAULT_BOLD);//太く
                pageTextView.setTextColor(Color.RED);//色変更
                pageTextView.setTextSize(20);//大きさ変更
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void onRadioClick() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkdId) {
                doSetRangeText();
            }
        });
    }

    private void doSetRangeText() {//もじ変更
        String range = doChoiceRadioButton();
        rangeTextView.setText(rangeString + range + "圏内(" + doChangeRange(range) + ")");
    }

    private String doChangeRange(String str) {//範囲の分をmに変換する
        if (str.equals(getString(R.string.M300))) {//M300と同じなら
            return getString(R.string.m300);//m300を変換
        }
        if (str.equals(getString(R.string.M500))) {
            return getString(R.string.m500);
        }
        if (str.equals(getString(R.string.M1000))) {
            return getString(R.string.m1000);

        }
        if (str.equals(getString(R.string.M2000))) {
            return getString(R.string.m2000);

        }
        return getString(R.string.m3000);
    }

    private void doGpsGet() {//GPSを取得する
        priority = 0;
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    private void onSelectSprinner() {//sprinnerを変更したとき
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//プルダウンで変更されたとき
                String select = (String) adapterView.getSelectedItem();//選択された文字列
                Toast.makeText(LocationActivity.this, select, Toast.LENGTH_SHORT).show();
                changeCheckBox(select);//seekBarに変更があればcheckboxを変換
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {//選択されなかったとき

            }
        });
    }

    private void changeCheckBox(String str) {//seekBarに変更があればcheckboxを変換
        unCheck();//変更があればすべてのチェックを外す
        String[][] strings = {//dataSet
                {"居酒屋", "カフェ", "ラーメン", "和食", "鍋", "イタリアン", "中華", "フレンチ", "韓国料理"},//ノンジャンル
                {"焼き鳥", "寿司", "そば", "とんかつ", "焼肉", "お好み焼き", "うどん", "たこ焼き", "うなぎ"},//和食
                {"ピザ", "パスタ", "ステーキ", "ハンバーガー", "カレー"},//洋食
                {"ラーメン", "麻婆豆腐", "餃子"}//中華
        };
        int index = 0;//配列のindex
        String[] genre = getResources().getStringArray(R.array.mainArray);
        for (int i = 0; i < genre.length; i++) {
            if (genre[i].equals(str)) {//配列の中身と引数が同じなら
                index = i;//indexセット
                break;
            }
        }
        doCheckBox(strings[index].length);//Checkboxの表示を行う(大カテゴリの要素の中身の数)
        for (int i = 0; i < strings[index].length; i++) {
            checkBoxes[i].setText(strings[index][i]);//checkBoxに文字をセット
        }
    }

    private void doCheckBox(int num) {//Checkboxの表示を行う(大カテゴリの要素の中身の数)
        //CHECKBOX_NUM桁の2進数で各桁ごとに0はoff,1はonという仕様にする
        //TODO;桁数が増えたら4桁ごとに16進数に直せばstringの領域を超えても問題ない(そもそもstringの領域は超えない,キャストするわけでないので特に問題なし)
        String str = "";//on,offを入れる
        for (int i = 0; i < num; i++) {//onの桁増やし
            str = "1" + str;//桁増やし
        }
        for (int i = 0; i < CHECKBOX_NUM - num; i++) {//offの桁増やし
            str = str + "0";//
        }
        for (int i = 0; i < CHECKBOX_NUM; i++) {//1桁ずつon,offを確認する
            int index = Integer.parseInt(String.valueOf(str.charAt(i)));//i桁を取得する
            if (index == 1) {//1ならon
                checkBoxes[i].setVisibility(View.VISIBLE);
            } else {//0ならoff
                checkBoxes[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void unCheck() {//すべてのチェックを外す
        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i].setChecked(false);
        }
    }

    private String doChoiceRadioButton() {//RadioButtonで選択されているindexを取得する
        int id = radioGroup.getCheckedRadioButtonId();// チェックされているラジオボタンの ID を取得
        RadioButton radioButton = findViewById(id);// チェックされているラジオボタンオブジェクトを取得
        return radioButton.getText().toString();
    }

    private boolean gnaviRequest() {
        gnaviRequestEntity = new GnaviRequestEntity();
        gnaviRequestEntity.setGps(gps);//gps情報をセット
        String checkStr = doChoiceRadioButton();//RadioButtonで選択されているindexを取得する
        gnaviRequestEntity.setRange(doChangeRange(checkStr));//範囲をセット
        String page = (String) pageTextView.getText();//表示するページ数を取得
        gnaviRequestEntity.setPage(page);//表示するページ数をセット
        EditText keywordEditText = findViewById(R.id.keyword_editText);
        String freeword = keywordEditText.getText().toString();//keyWordをセット
        freeword = addKeyWord(freeword);
        gnaviRequestEntity.setFreeword(freeword);//フリーワード検索をセット
        gnaviAPI.setGnaviRequestEntity(gnaviRequestEntity);//検索パラメータを渡す
        gnaviAPI.setModeFlag(modeFlag);//検索モードを代入
        gnaviAPI.execute();//非同期実行
        while (true) {//api結果取得するまでweit
            if (gnaviAPI.isResultFlag()) {
                if (gnaviAPI.isFinishFlag()) {
                    return true;
                }
            } else if (gnaviAPI.isFinishFlag()) {
                return false;
            }
        }
    }

    private String addKeyWord(String str) {//キーワードを追加する
        String add = "";
        boolean flag = false;
        for (int i = 0; i < checkBoxes.length; i++) {
            if (checkBoxes[i].isChecked()) {
                add = add + "%20" + checkBoxes[i].getText();//%20はスペースと同意
                flag = true;
            }
        }
        if (!flag) {
            return str;
        }
        return str + "%20" + add;
    }

    private void jump() {
        startActivity(ShowListActivity.createIntent(gnaviRequestEntity, getApplication()));
    }

    private void createLocationCallback() {// locationのコールバックを受け取る
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
            StringBuilder builder = new StringBuilder("---------- UpdateLocation ---------- \n");
            for (int i = 0; i < fusedName.length; i++) {
                builder.append(fusedName[i]);
                builder.append(" = ");
                builder.append(String.valueOf(fusedData[i]));
                builder.append("\n");
            }
            setGps(fusedData[0], fusedData[1]);
            builder.append("Time");
            builder.append(" = ");
            builder.append(lastUpdateTime);
            builder.append("\n");
        }

    }

    @SuppressLint("RestrictedApi")
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

    private void buildLocationSettingsRequest() {// 端末で測位できる状態か確認する。wifi, GPSなどがOffになっているとエラー情報のダイアログが出る
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        switch (requestCode) {
            // CheckModel for the integer request code originally supplied to startResolutionForResult().
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
            Log.d("debug", "stopLocationUpdates: " + "updates never requested, no-op.");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {    //端末の戻るボタンを押下したとき
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
        gps[0] = lat;//gps(lat)のセット
        gps[1] = lon;//gps(lon)のセット
        this.gps = gps;
    }

    public double[] getGps() {
        return gps;
    }
}
