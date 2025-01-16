package com.example.tester

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import org.json.JSONObject
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.security.MessageDigest
import android.app.ProgressDialog
import android.content.Context

class LoginAT : AppCompatActivity() {
    private lateinit var om_id: EditText
    private lateinit var om_pw: EditText
    private lateinit var om_login: Button
    private lateinit var om_register: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_at)

        // EditText 초기화
        om_id = findViewById(R.id.om_id)
        om_pw = findViewById(R.id.om_pw)

        // Button 초기화
        om_login = findViewById(R.id.om_login)
        om_register = findViewById(R.id.om_register)

        // 로그인 버튼 클릭 리스너 설정
        om_login.setOnClickListener {
            val id = om_id.text.toString()
            val pw = om_pw.text.toString()

            if (id.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 로그인 처리 로직 추가
                loginUser(id, pw)
            }
        }

        // 회원가입 버튼 클릭 리스너 설정
        om_register.setOnClickListener {
            // 회원가입 화면으로 이동
            val intent = Intent(this, RegisterAT::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loginUser(id: String, pw: String) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("로그인 중...")
        progressDialog.show()

        val url = "http://grayom.dothome.co.kr/Login.php"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                progressDialog.dismiss()
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getBoolean("success")
                    if (success) {
                        // userID로 변경 (userId -> userID)
                        saveUserSession(jsonObject.getString("userID"))
                        Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        val message = jsonObject.getString("message")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "응답 처리 중 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                progressDialog.dismiss()
                val message = when (error) {
                    is TimeoutError -> "서버 응답 시간 초과"
                    is NoConnectionError -> "서버에 연결할 수 없습니다"
                    else -> "로그인 처리 중 오류: ${error.message}"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "userID" to id,
                    "userPassword" to pw  // 해시화하지 않고 raw 비밀번호 전송
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun hashPassword(password: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
            .fold("") { str, byte -> str + "%02x".format(byte) }
    }

    private fun saveUserSession(userID: String) {
        getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("user_id", userID)
            .putBoolean("is_logged_in", true)
            .apply()
    }
}



