package com.example.tester

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // GitHub 링크 클릭 리스너
        val githubLink = findViewById<TextView>(R.id.github_link)
        githubLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/GrayOM"))
            startActivity(intent)
        }

        // 블로그 링크 클릭 리스너
        val blogLink = findViewById<TextView>(R.id.blog_link)
        blogLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://grayom.tistory.com/"))
            startActivity(intent)
        }

        // 시스템 바 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
