package com.example.yourtree;

import static okhttp3.internal.Util.UTF_8;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link userFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class userFragment extends Fragment {

    //user adapter
    private ListView userListView;
    private UserListAdapter UserListAdapter;
    private List<User> userList;
    private String userID;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public userFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment userlistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static userFragment newInstance(String param1, String param2) {
        userFragment fragment = new userFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // user adapter ??????
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        userID = MainActivity.userID;
        userListView = (ListView) v.findViewById(R.id.userListView);
        userList = new ArrayList<User>(); // ?????????
        // ?????? ?????? ?????????????????? ????????? ?????? ??????
        new BackgroundTask().execute();
        UserListAdapter = new UserListAdapter(getActivity().getApplicationContext(), userList); // ???????????? ??????
        userListView.setAdapter(UserListAdapter); // ???????????? ???????????? ????????? ?????? ?????? ????????? ?????????

        final ImageButton add_friend = (ImageButton) v.findViewById(R.id.add_friend);
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), searchActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    // ????????? ?????? ?????? ??????
    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;
        @Override
        protected void onPreExecute() {
            target = "https://thddbap.cafe24.com/FriendList.php";// ?????? ????????? URL??? ??????
        }
        String TAG = "JsonParseTest";
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String selectData = "userID=" + userID;
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                // ???????????? ????????? ??????
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(selectData.getBytes(UTF_8));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                //. ???????????? ????????? ????????? ??????
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                } // ?????? ?????? ??????

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();
                Log.d(TAG, sb.toString().trim());

                return sb.toString().trim();        // ????????? JSON ??? ????????? ??????
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return null;
            }
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        // ?????? ????????? ??????
        public void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);// ?????? ?????? ??????
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String userID, userPassword, userName, userBirth;
                // ?????? ?????? ????????????
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    userID = object.getString("userID");
                    userPassword = object.getString("userPassword");
                    userName = object.getString("userName");
                    userBirth = object.getString("userBirth");

                    // ????????? ?????? ???????????? ?????? ?????? ??????
                    User user = new User(userID, userPassword, userName, userBirth);
                    userList.add(user); // ?????? ????????? userList??? ??????
                    UserListAdapter.notifyDataSetChanged();
                    count++;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}