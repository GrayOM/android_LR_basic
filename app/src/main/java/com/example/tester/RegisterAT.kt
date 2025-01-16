package com.example.tester

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class RegisterAT : AppCompatActivity() {
    private lateinit var om_id: EditText
    private lateinit var om_pw: EditText
    private lateinit var om_name: EditText
    private lateinit var om_age: EditText
    private lateinit var om_register: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_at)

        // EditText 초기화
        om_id = findViewById(R.id.om_id)
        om_pw = findViewById(R.id.om_pw)
        om_name = findViewById(R.id.om_name)
        om_age = findViewById(R.id.om_age)

        // Button 초기화
        om_register = findViewById(R.id.om_register)

        // Button 클릭 리스너 설정
        om_register.setOnClickListener {
            val id = om_id.text.toString()
            val pw = om_pw.text.toString()
            val name = om_name.text.toString()
            val ageText = om_age.text.toString()

            if (id.isEmpty() || pw.isEmpty() || name.isEmpty() || ageText.isEmpty()) {
                Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val age = try {
                    ageText.toInt()
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "나이는 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 네트워크 요청 전송
                sendRegisterRequest(id, pw, name, age)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun sendRegisterRequest(id: String, pw: String, name: String, age: Int) {
        val url = "http://grayom.dothome.co.kr/Register.php"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getBoolean("success")
                    if (success) {
                        Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "회원가입 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "응답 처리 중 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                val errorMessage = when {
                    error.networkResponse?.statusCode != null ->
                        "서버 에러: ${error.networkResponse.statusCode}"

                    error.message != null ->
                        "에러 메시지: ${error.message}"

                    else ->
                        "알 수 없는 네트워크 에러가 발생했습니다"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userID"] = id           // 수정: userid -> userID
                params["userPassword"] = pw
                params["userName"] = name
                params["userAge"] = age.toString()
                return params
            }
        }

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}