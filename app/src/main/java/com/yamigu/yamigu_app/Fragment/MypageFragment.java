package com.yamigu.yamigu_app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yamigu.yamigu_app.Activity.CertificationUActivity;
import com.yamigu.yamigu_app.Activity.CertificationWActivity;
import com.yamigu.yamigu_app.Activity.GlobalApplication;
import com.yamigu.yamigu_app.Activity.MainActivity;
import com.yamigu.yamigu_app.Activity.NotificationActivity;
import com.yamigu.yamigu_app.Activity.TicketOnboardingActivity;
import com.yamigu.yamigu_app.CustomLayout.CircularImageView;
import com.yamigu.yamigu_app.CustomLayout.InviteFriends;
import com.yamigu.yamigu_app.CustomLayout.ProfileCard;
import com.yamigu.yamigu_app.Etc.ImageFilePath;
import com.yamigu.yamigu_app.Etc.ImageUtils;
import com.yamigu.yamigu_app.Network.RequestHttpURLConnection;
import com.yamigu.yamigu_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import static android.content.Intent.ACTION_PICK;

public class MypageFragment extends Fragment {
    private Toolbar tb;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ProfileCard profileCard;
    private InviteFriends inviteFriends;
    private ImageButton btn_chat_manager, btn_notification, btn_ticket;
    private TextView tv_nickname, tv_age, tv_belong, tv_department, tv_available_nickname, tv_num_of_ticket, tv_num_of_noti;
    private EditText et_nickname;
    private ImageButton btn_edit_nickname;
    public static CircularImageView profile_img;
    private RelativeLayout label_certificated;
    private FrameLayout fl_meeting_card;
    private Button btn_certificating;
    private String auth_token;
    private boolean validated_from_server;
    private boolean nickname_validated;
    private String nickname;
    private String profile_url;
    private Menu globalMenu;
    private int user_certified;
    private final int REQ_CODE_SELECT_IMAGE = 100;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;

    String upLoadServerUri = "http://106.10.39.154:9999/api/user/change/avata/";

    /**********  File Path *************/
    String uploadFilePath = "";
    String uploadFileName = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
//        tb = (Toolbar) view.findViewById(R.id.toolbar_m) ;
//        ((AppCompatActivity)getActivity()).setSupportActionBar(tb) ;
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();

        profileCard = view.findViewById(R.id.profile_card);
        inviteFriends = view.findViewById(R.id.invite_friend);
        btn_chat_manager = view.findViewById(R.id.btn_kakao_chat);
        tv_nickname = profileCard.findViewById(R.id.name);
        tv_age = profileCard.findViewById(R.id.age);
        tv_belong = profileCard.findViewById(R.id.company);
        tv_department = profileCard.findViewById(R.id.department);
        tv_available_nickname = profileCard.findViewById(R.id.tv_available_nickname);
        et_nickname = profileCard.findViewById(R.id.et_nickname);
        btn_edit_nickname = profileCard.findViewById(R.id.btn_edit_nickname);
        profile_img = profileCard.findViewById(R.id.iv_profile);
        label_certificated = profileCard.findViewById(R.id.label_certificated);
        btn_certificating = profileCard.findViewById(R.id.btn_certify);
        fl_meeting_card = profileCard.findViewById(R.id.fl_meeting_card);
        btn_notification = profileCard.findViewById(R.id.btn_notification);
        btn_ticket = profileCard.findViewById(R.id.btn_ticket);
        tv_num_of_noti = profileCard.findViewById(R.id.num_of_notifi);
        tv_num_of_ticket = profileCard.findViewById(R.id.num_of_ticket);
        label_certificated.setVisibility(View.INVISIBLE);
        btn_certificating.setVisibility(View.INVISIBLE);
        profile_url = preferences.getString("profile", "");

        user_certified = preferences.getInt("user_certified", 0);
        int num_of_ticket = preferences.getInt("num_of_ticket", 0);
        tv_num_of_ticket.setText(Integer.toString(num_of_ticket));
        tv_num_of_noti.setText(GlobalApplication.unread_noti_count+"");
        if(user_certified == 0) {
            btn_certificating.setVisibility(View.VISIBLE);
            label_certificated.setVisibility(View.INVISIBLE);
            btn_certificating.setText("소속 인증하기");
            fl_meeting_card.setAlpha(0.2f);
            btn_certificating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean is_student = preferences.getBoolean("is_student", false);
                    Intent intent;
                    if(is_student) {
                        intent = new Intent(getContext(), CertificationUActivity.class);
                    }
                    else {
                        intent = new Intent(getContext(), CertificationWActivity.class);
                    }
                    intent.putExtra("nickname", preferences.getString("nickname", ""));
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
                }
            });

        }
        else if(user_certified == 1) {
            btn_certificating.setVisibility(View.VISIBLE);
            label_certificated.setVisibility(View.INVISIBLE);
            btn_certificating.setText("인증 진행중입니다");
            fl_meeting_card.setAlpha(0.2f);
        }
        else {
            btn_certificating.setVisibility(View.INVISIBLE);
            label_certificated.setVisibility(View.VISIBLE);
            btn_edit_nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    et_nickname.setHint(preferences.getString("nickname", ""));
                    tv_nickname.setVisibility(View.GONE);
                    et_nickname.setVisibility(View.VISIBLE);
                    btn_edit_nickname.setVisibility(View.GONE);
                    globalMenu.findItem(R.id.menu_cancel).setVisible(true);
                    globalMenu.findItem(R.id.menu_complete).setVisible(true);
                }
            });
            profile_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ACTION_PICK);
                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                }
            });
            et_nickname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!editable.toString().equals("")) {
                        String url = "http://106.10.39.154:9999/api/user/validation/nickname/"+editable.toString();
                        ContentValues values = new ContentValues();
                        NetworkTask networkTask = new NetworkTask(url, values);
                        networkTask.execute();
                    }
                }
            });
            btn_notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), NotificationActivity.class);
                    startActivity(intent);
                    ((MainActivity)getContext()).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
                }
            });
            btn_ticket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), TicketOnboardingActivity.class);
                    startActivity(intent);
                    ((MainActivity)getContext()).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_fadeout_short);
                }
            });
        }
//        String base64AvataUser = preferences.getString("avata", "default");
//        Log.d("avata", base64AvataUser);
//        Bitmap bitmapAvata;
//        if (!base64AvataUser.equals("default")) {
//            byte[] decodedString = Base64.decode(base64AvataUser, Base64.DEFAULT);
//            bitmapAvata = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        } else {
//            bitmapAvata = null;
//        }
//        if(bitmapAvata != null) {
//            while(bitmapAvata.getWidth() < profile_img.getWidth()) {
//                bitmapAvata = Bitmap.createScaledBitmap(bitmapAvata, bitmapAvata.getWidth() * 2, bitmapAvata.getHeight() * 2, false);
//            }
//            while(bitmapAvata.getHeight() < profile_img.getHeight()) {
//                bitmapAvata = Bitmap.createScaledBitmap(bitmapAvata, bitmapAvata.getWidth() * 2, bitmapAvata.getHeight() * 2, false);
//            }
//            profile_img.setImageBitmap(bitmapAvata);
//        }
//
        if(!profile_url.isEmpty()) {
//            profile_img.setImageBitmap(GlobalApplication.bitmap_map.get(profile_url));
//            ContentValues values = new ContentValues();
//            NetworkTask3 networkTask3 = new NetworkTask3(profile_url, values, profile_img);
//            networkTask3.execute();
        }
        tv_nickname.setText(preferences.getString("nickname", ""));
        tv_age.setText(" (" + preferences.getInt("age", 0) + ")");
        tv_belong.setText(preferences.getString("belong", ""));
        tv_department.setText(preferences.getString("department", ""));
        auth_token = preferences.getString("auth_token", "");

        if(MainActivity.dialog.isShowing()) {
            MainActivity.dialog.dismiss();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tv_num_of_ticket.setText(Integer.toString(HomeFragment.ticket_count));
        tv_num_of_noti.setText(Integer.toString(GlobalApplication.unread_noti_count));
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_edit_nickname, menu);
        globalMenu = menu;
        menu.findItem(R.id.menu_complete).setVisible(false);
        menu.findItem(R.id.menu_cancel).setVisible(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_complete:
                if(nickname_validated) {
                    String url = "http://106.10.39.154:9999/api/user/change/nickname/";
                    ContentValues values = new ContentValues();
                    values.put("nickname", et_nickname.getText().toString());
                    NetworkTask2 networkTask2 = new NetworkTask2(url, values);
                    networkTask2.execute();
                    tv_nickname.setVisibility(View.VISIBLE);
                    et_nickname.setVisibility(View.GONE);
                    btn_edit_nickname.setVisibility(View.VISIBLE);
                    tv_available_nickname.setVisibility(View.GONE);
                    globalMenu.findItem(R.id.menu_cancel).setVisible(false);
                    globalMenu.findItem(R.id.menu_complete).setVisible(false);
                }
                return true;
            case R.id.menu_cancel:
                tv_nickname.setVisibility(View.VISIBLE);
                et_nickname.setVisibility(View.GONE);
                btn_edit_nickname.setVisibility(View.VISIBLE);
                tv_available_nickname.setVisibility(View.GONE);
                globalMenu.findItem(R.id.menu_cancel).setVisible(false);
                globalMenu.findItem(R.id.menu_complete).setVisible(false);
                return true;
        }
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());
                    //이미지 데이터를 비트맵으로 받아온다.

                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    final String filePath = ImageFilePath.getPath(getContext(), data.getData());
                    Log.d("filePath", filePath);
//
//                    String url = "http://106.10.39.154:9999/api/user/certificate/";
//                    ContentValues values = new ContentValues();
//                    final String imageBase64 = ImageUtils.encodeBase64(image_bitmap);
//                    values.put("file_name", filePath);
//                    values.put("image", imageBase64);
//                    NetworkTask3 networkTask = new NetworkTask3(url, values);
//                    networkTask.execute();
                    dialog = ProgressDialog.show(getContext(), "", "Uploading file...", true);

                    new Thread(new Runnable() {
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                }
                            });
                            uploadFile(filePath);

                        }
                    }).start();
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
            else {
                Log.d("OnAcitivityResult", "Fail");
            }
        }
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
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(getContext(), url, values, "GET", auth_token); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            validated_from_server = false;

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                validated_from_server = jsonObject.getBoolean("is_available");
            } catch(JSONException e) {
                validated_from_server = false;
            } catch(NullPointerException e) {
                validated_from_server = false;
            }

            String pattern = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]*$";
            Editable editable = et_nickname.getText();
            try {
                boolean is_validate = editable.toString().getBytes("euc-kr").length <= 12 && Pattern.matches(pattern, editable.toString());
                if (!editable.toString().isEmpty() || !editable.toString().equals(tv_nickname.getText().toString())) {
                    tv_available_nickname.setVisibility(View.VISIBLE);
                    if (!is_validate || !validated_from_server) {
                        tv_available_nickname.setTextColor(getResources().getColor(R.color.colorRed));
                        tv_available_nickname.setText("사용 불가능합니다.");
                        nickname_validated = false;
                        nickname = "";
                    } else {
                        tv_available_nickname.setTextColor(getResources().getColor(R.color.colorBlue));
                        tv_available_nickname.setText("사용 가능합니다.");
                        nickname_validated = true;
                        nickname = editable.toString();
                    }
                } else {
                    tv_available_nickname.setVisibility(View.GONE);
                }
            } catch(UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
    public class NetworkTask2 extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;

        public NetworkTask2(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            requestHttpURLConnection = new RequestHttpURLConnection();

            result = requestHttpURLConnection.request(getContext(), url, values, "POST", auth_token);
            return result;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            String after_nickname = "";
            try {
                jsonObject = new JSONObject(s);
                after_nickname = jsonObject.getString("data");
            } catch(JSONException e) {
                e.printStackTrace();
            }
            tv_nickname.setText(after_nickname);
            et_nickname.setHint(after_nickname);
            et_nickname.setText("");
            tv_available_nickname.setVisibility(View.GONE);
            editor.putString("nickname", after_nickname);
            editor.apply();

        }
    }
    public class NetworkTask3 extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ContentValues values;
        private RequestHttpURLConnection requestHttpURLConnection;
        private CircularImageView civ;
        public NetworkTask3(String url, ContentValues values,  CircularImageView civ) {
            this.url = url;
            this.values = values;
            this.civ = civ;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlO = new URL(url);
                URLConnection conn = urlO.openConnection();
                conn.connect();
                InputStream urlInputStream = conn.getInputStream();
                return BitmapFactory.decodeStream(urlInputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap bm) {
            editor.putString("profile", url);
            editor.commit();
            try {
                civ.setImageBitmap(bm);
            } catch(NullPointerException e) {
                e.printStackTrace();
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
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", sourceFile);
        Bitmap resized = ImageUtils.resize(getContext(), uri, 200);
        int radius =  resized.getWidth() <= resized.getHeight() ? resized.getWidth() : resized.getHeight();
        resized = ImageUtils.cropCenterBitmap(resized, radius, radius);
        try {
            ExifInterface exif = new ExifInterface(sourceFileUri);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = ImageUtils.exifOrientationToDegrees(exifOrientation);
            resized = ImageUtils.rotateBitmap(resized, exifDegree);
        }catch(IOException e) {
            e.printStackTrace();
        }
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
//                if(!resized.isRecycled())
//                    resized.recycle();
                InputStream fileInputStream = ImageUtils.convertBitmapToInputStream(resized);
                //FileInputStream fileInputStream = new FileInputStream(resizedInputStream);
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
                conn.setRequestProperty("Accept", "application/json");

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
                     BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                     // 출력물의 라인과 그 합에 대한 변수.
                     String line;
                     String page = "";

                     // 라인을 받아와 합친다.
                     while ((line = reader.readLine()) != null){
                        page += line;
                     }
                    try {
                        JSONObject jsonObject = new JSONObject(page);
                        String newImageUrl = jsonObject.getString("new_avata");
                        ContentValues values = new ContentValues();
                        if(!newImageUrl.isEmpty()) {
                            NetworkTask3 networkTask3 = new NetworkTask3(newImageUrl, values, profile_img);
                            networkTask3.execute();
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getContext(), "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
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

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getContext(), "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getContext(), "Got Exception : see logcat ",
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
}

