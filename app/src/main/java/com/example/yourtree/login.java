package com.example.yourtree;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class login extends AppCompatActivity {

    private Button btn_login;
    private EditText et_login_id;
    private String login_id;

    private String userId;
    private String userPw;
    private String username;
    private String userbirth;
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView btn_join = (TextView) findViewById(R.id.btn_join);
        btn_join.setOnClickListener((view) -> {
            Intent joinIntent  = new Intent(login.this, join.class);
        });

        final EditText et_login_id = (EditText) findViewById(R.id.et_login_id);
        final EditText et_login_pass = (EditText) findViewById(R.id.et_login_pass);
        final Button btn_login = (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = et_login_id.getText().toString();
                String userPassword = et_login_pass.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            //로그인 성공시
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                                dialog = builder.setMessage("로그인에 성공하였습니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                Intent intent = new Intent(login.this, MainActivity.class);
                                login.this.startActivity(intent);
                                finish(); // 액티비티 닫기
                            }
                            // 로그인 실패
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                                dialog = builder.setMessage("계정을 다시 확인하세요.").setNegativeButton("다시 시도", null).create();
                                dialog.show();
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userId, userPassword,responseListener);
                RequestQueue queue = Volley.newRequestQueue(login.this);
                queue.add(loginRequest);
            }
        });
    } // oncreate은 해당 액티비티가 처음 실행될 때 안에 구문 한 번 실행

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}