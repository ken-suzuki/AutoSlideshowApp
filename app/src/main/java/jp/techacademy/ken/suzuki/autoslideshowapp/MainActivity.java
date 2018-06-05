package jp.techacademy.ken.suzuki.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;
    double mTimerSec = 0.0;

    Handler mHandler = new Handler();

    Button mStartButton;
    Button mPrevButton;
    Button mNextButton;

    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrevButton = (Button) findViewById(R.id.prev_button);
        mStartButton = (Button) findViewById(R.id.start_button);
        mNextButton = (Button) findViewById(R.id.next_button);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                // getContentsInfoメソッドを読み込む
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
                // getContentsInfoメソッドを読み込まないしてください
            }
            // Android 5系以下の場合
        } else {
            // getContentsInfoメソッドを読み込む
            getContentsInfo();
        }

        // 再生ボタンの処理
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // cursorの中身（DBのレコード）が0の場合エラーを起こすので、中身を判定する
                if ((cursor != null) && cursor.getCount() > 0) {


                    if (mTimer == null) {

                        // 進む戻るボタンのタップ不可
                        mPrevButton.setEnabled(false);
                        mNextButton.setEnabled(false);

                        // ボタンの表示を停止に変更
                        mStartButton.setText(String.format("停止"));

                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mTimerSec += 0.1;

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        // 自動的に次の画像に切り替えるので moveToFirst
                                        if (cursor.moveToNext()) {
                                            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                            Long id = cursor.getLong(fieldIndex);
                                            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                            ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                            imageView.setImageURI(imageUri);
                                        } else {
                                            // 最初の画面を取得する
                                            cursor.moveToFirst();
                                            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                            Long id = cursor.getLong(fieldIndex);
                                            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                            ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                            imageView.setImageURI(imageUri);
                                        }
                                    }
                                });
                            }
                        }, 2000, 2000);
                    } else {
                        // 自動送りを停止する処理
                        mTimer.cancel();
                        mTimer = null;

                        // 進む戻るボタンをタップ可に変更
                        mPrevButton.setEnabled(true);
                        mNextButton.setEnabled(true);

                        // ボタンの表示を再生に変更
                        mStartButton.setText(String.format("再生"));
                    }
                } else {
                    // ボタンをタップ不可に変更
                    mPrevButton.setEnabled(false);
                    mNextButton.setEnabled(false);
                    mStartButton.setEnabled(false);

                    // Toastを追加
                    Toast toast = Toast.makeText(MainActivity.this, "表示できません", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        // 戻るボタンの処理
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // cursorの中身（DBのレコード）が0の場合エラーを起こすので、中身を判定する
                if ((cursor != null) && cursor.getCount() > 0) {


                    if (cursor.moveToPrevious()) {
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageURI(imageUri);
                    } else {
                        // 最後の画面を取得する
                        cursor.moveToLast();
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageURI(imageUri);
                    }
                } else {
                    // ボタンをタップ不可に変更
                    mPrevButton.setEnabled(false);
                    mNextButton.setEnabled(false);
                    mStartButton.setEnabled(false);

                    // Toastを追加
                    Toast toast = Toast.makeText(MainActivity.this, "表示できません", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        // 進むボタンの処理
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // cursorの中身（DBのレコード）が0の場合エラーを起こすので、中身を判定する
                if (( cursor != null ) && cursor.getCount() > 0 ) {

                    if (cursor.moveToNext()) {

                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageURI(imageUri);
                    } else {
                        // 最初の画面を取得する
                        cursor.moveToFirst();
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageURI(imageUri);
                    }
                } else {
                    // ボタンをタップ不可に変更
                    mPrevButton.setEnabled(false);
                    mNextButton.setEnabled(false);
                    mStartButton.setEnabled(false);

                    // Toastを追加
                    Toast toast = Toast.makeText(MainActivity.this, "表示できません", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    // ユーザの許可、不許可の選択結果を受け取って処理する
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("ANDROID", "許可された");
                    // ここでgetContentsInfoメソッドを読み込むのがポイント
                    getContentsInfo();

                    // Toastを追加
                    Toast toast = Toast.makeText(MainActivity.this, "許可されました", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Log.d("ANDROID", "許可されなかった");

                    //ユーザーから許可されるまでダイアログを表示する処理
                    // Android 6.0以降の場合
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // パーミッションの許可状態を確認する
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            // 許可されている
                            // getContentsInfoメソッドを読み込む
                            getContentsInfo();
                        } else {
                            // 許可されていないので許可ダイアログを表示する
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
                            // getContentsInfoメソッドを読み込まない
                        }
                        // Android 5系以下の場合
                    } else {
                        // getContentsInfoメソッドを読み込む
                        getContentsInfo();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
        }
    }

    // cursor.close(); をライフサイクルのonDestory内処理する
    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (cursor != null) {
            cursor.close();
        }
    }
}