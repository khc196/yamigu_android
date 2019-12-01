package com.yamigu.yamigu_app.Activity;


import android.app.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yamigu.yamigu_app.Etc.ImageFilePath;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import static android.content.Intent.ACTION_PICK;

public class CertificationWActivity extends AppCompatActivity {
    private Toolbar tb;
    private ImageButton btn_attach_file;

    private Button btn_go_home;
    private TextView btn_skip;
    private EditText et_job, et_company;
    private String belong, department, nickname, friend_code;
    private String auth_token;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private final int REQ_CODE_SELECT_IMAGE = 100;
    ProgressDialog dialog = null;
    int serverResponseCode = 0;
    String uploadFilePath = "";
    String uploadFileName = "";

    Bitmap image_bitmap;
    String real_name, phonenumber, gender_string, birthdate;
    int age, gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification_w);
        findViewById(R.id.overall_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch(NullPointerException e) {

                }
                return true;
            }
        });
        findViewById(R.id.inner_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch(NullPointerException e) {

                }
                return true;
            }
        });
        tb = (Toolbar) findViewById(R.id.toolbar) ;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        setSupportActionBar(tb) ;
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb.setNavigationIcon(R.drawable.arrow_back_ios);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
//        nickname = intent.getExtras().getString("nickname");
//        friend_code = intent.getExtras().getString("friend_code");
//        real_name = intent.getExtras().getString("realname");
//        phonenumber = intent.getExtras().getString("phonenumber");
//        gender_string = intent.getExtras().getString("gender");
//        birthdate = intent.getExtras().getString("birthdate");
        nickname = preferences.getString("nickname", "");
        friend_code = preferences.getString("friend_code", "");
        real_name = preferences.getString("real_name", "");
        phonenumber = preferences.getString("phonenumber", "");
        gender_string = preferences.getString("gender_string", "");
        birthdate = preferences.getString("birthdate", "");
        if(birthdate.isEmpty()) {
            age = preferences.getInt("age", 0);
        }
        else {
            age = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(birthdate.substring(0, 4)) + 1;
        }
        if(gender_string.isEmpty()) {
            gender = preferences.getInt("gender", 0);
        }
        else {
            gender = Integer.parseInt(gender_string);

        }
        auth_token = preferences.getString("auth_token", "");
        btn_attach_file = (ImageButton) findViewById(R.id.btn_attach_file);
        btn_go_home = (Button) findViewById(R.id.btn_gohome);
        btn_skip = (TextView) findViewById(R.id.btn_skip);
        et_company = (EditText) findViewById(R.id.et_company);
        et_job = (EditText) findViewById(R.id.et_job);
        btn_go_home.setBackgroundResource(R.drawable.state_pressed_gray);
        et_company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().isEmpty() || et_job.getText().toString().isEmpty()) {
                    btn_go_home.setBackgroundResource(R.drawable.state_pressed_gray);
                }
                else {
                    btn_go_home.setBackgroundResource(R.drawable.state_pressed_orange);
                }
            }
        });
        et_job.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().isEmpty() || et_company.getText().toString().isEmpty()) {
                    btn_go_home.setBackgroundResource(R.drawable.state_pressed_gray);
                }
                else {
                    btn_go_home.setBackgroundResource(R.drawable.state_pressed_orange);
                }
            }
        });
        btn_attach_file.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){  // 클릭하면 ACTION_PICK 연결로 기본 갤러리를 불러옵니다.
                Intent intent = new Intent(ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });
        btn_go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_job.getText().toString().isEmpty() || et_company.getText().toString().isEmpty()) return;
                department = et_job.getText().toString();
                belong = et_company.getText().toString();
                String url = "http://106.10.39.154:9999/api/auth/signup/";
                ContentValues values = new ContentValues();
                values.put("real_name", real_name);
                values.put("age", age);
                values.put("phone", phonenumber);
                values.put("gender", gender);
                values.put("nickname", nickname);
                values.put("is_student", false);
                values.put("belong", belong);
                values.put("department", department);
                dialog = new ProgressDialog(CertificationWActivity.this);
                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();
            }
        });
        btn_skip.setPaintFlags(btn_skip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://106.10.39.154:9999/api/auth/signup/";
                ContentValues values = new ContentValues();
                values.put("real_name", real_name);
                values.put("age", age);
                values.put("phone", phonenumber);
                values.put("gender", gender);
                values.put("nickname", nickname);
                values.put("is_student", false);
                dialog = new ProgressDialog(CertificationWActivity.this);
                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();
            }
        });

        ((GlobalApplication)getApplicationContext()).setCurrentActivity(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode==Activity.RESULT_OK)
            {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());
                    //이미지 데이터를 비트맵으로 받아온다.
                    final String filePath = ImageFilePath.getPath(this, data.getData());
                    dialog = ProgressDialog.show(this, "", "Uploading file...", true);
                    new Thread(new Runnable() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                }
                            });
                            uploadFile(filePath);
                        }
                    }).start();
                    image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView)findViewById(R.id.selected_img);
                    image.setVisibility(View.VISIBLE);
                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);
                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                String upLoadServerUri = "http://106.10.39.154:9999/api/user/certificate/";
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("Authorization", "Token " + auth_token);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                            editor.putInt("user_certified", 1);
                            editor.apply();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }
    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = ((GlobalApplication)getApplicationContext()).getCurrentActivity();
        if (this.equals(currActivity))
            ((GlobalApplication)getApplicationContext()).setCurrentActivity(null);

    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_slide_out_right);
    }
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("회원가입 진행중입니다...");
        }
        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(getApplicationContext(), url, values, "POST", auth_token); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            editor.putInt("age", age);
            editor.putString("belong", belong);
            editor.putString("department", department);
            editor.putBoolean("is_student", false);
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
        }
    }
}
