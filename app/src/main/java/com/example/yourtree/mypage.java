package com.example.yourtree;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class mypage extends AppCompatActivity {

    String userID = MainActivity.userID;
    Uri uri; //uri 이미지
    ImageView IMG;


    //프로필 사진 요청코드
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        IMG = findViewById(R.id.IMG);

        // 사진 등록
        final Button btn_upload= (Button) findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);
                Upload();
            }

        });

        // 사진 보기
        final Button btn_download= (Button) findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Download();
            }
        });
    }
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Intent intent = result.getData();
                        Uri uri = intent.getData();
//                      IMG.setImageURI(uri);
                        Glide.with(mypage.this)
                                .load(uri)
                                .into(IMG);

                        try {
                            ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), uri);
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            bitmap = resize(bitmap);
                            String image = bitmapToByteArray(bitmap);
                            changeProfileImageToDB(image); //변경된 프로필 이미지 서버로 보내기

                        } catch (Exception e) {

                        }

                    }
                }
            });

    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE) {
            if(requestCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    Glide.with(getApplicationContext()).load(uri).into(IMG); // 다이얼로그 이미지 사진에 넣기
                } catch (Exception e) {}
            } else if (resultCode == RESULT_CANCELED) {} // 취소시 호출할 행동 쓰기
        }
    }
     */

    private void Upload() { //이미지 업데이트 하기
        Response.Listener<String> responseListener = new Response.Listener<String>() { //여기서 여기서 Quest1에서 썼던 데이터를 다가져온다.

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        Toast.makeText(mypage.this, "데이터베이스 이미지 바꾸기 성공", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Upload upload = new Upload(userID, uri, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(upload);
    }

    private void Download() { //서버에서 이미지 가져오기
        Response.Listener<String> responseListener = new Response.Listener<String>() { //여기서 여기서 Quest1에서 썼던 데이터를 다가져온다.

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        String image = jsonObject.getString("IMG");
                        //json데이터로 받은 image를 이미지뷰에 넣거나,
                        //글라이드 라이브러리를 통해 사용하면 된다.
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Download download = new Download(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(download);
    }

    //이미지 사이즈 변경
    private Bitmap resize(Bitmap bm){
        Configuration config=getResources().getConfiguration();
        if(config.smallestScreenWidthDp>=800)
            bm = Bitmap.createScaledBitmap(bm, 400, 240, true);
        else if(config.smallestScreenWidthDp>=600)
            bm = Bitmap.createScaledBitmap(bm, 300, 180, true);
        else if(config.smallestScreenWidthDp>=400)
            bm = Bitmap.createScaledBitmap(bm, 200, 120, true);
        else if(config.smallestScreenWidthDp>=360)
            bm = Bitmap.createScaledBitmap(bm, 180, 108, true);
        else
            bm = Bitmap.createScaledBitmap(bm, 160, 96, true);
        return bm;
    }

    /**비트맵을 바이너리 바이트배열로 바꾸어주는 메서드 */
    public String bitmapToByteArray(Bitmap bitmap) {
        String image = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        image = "&image=" + byteArrayToBinaryString(byteArray);
        return image;
    }

    /**바이너리 바이트 배열을 스트링으로 바꾸어주는 메서드 */
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    /**바이너리 바이트를 스트링으로 바꾸어주는 메서드 */
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    private void changeProfileImageToDB(String image) {


        Response.Listener<String> responseListener = new Response.Listener<String>() { //여기서 여기서 Quest1에서 썼던 데이터를 다가져온다.

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Profile_Img_Check profile_img_check = new Profile_Img_Check(id, image, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(profile_img_check);

        Log.d("비트맵", String.valueOf(uri));
    }
}