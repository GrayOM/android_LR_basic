package com.example.tester;


import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class register_request extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동)
    final static private String URL = "http://grayom.dothome.co.kr/Register.php";
    private Map<String, String> map;

    public register_request(String userID, String userPassword,String userName , int userAge , Response.Listener<String> listener)
    {
        super(Method.POST,URL, listener, null);

        map = new HashMap<>();
        map.put("userID",userID);
        map.put("userPassword",userPassword);
        map.put("userName",userName);
        map.put("userAge",userAge + "");
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
