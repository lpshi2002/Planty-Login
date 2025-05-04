package com.example.plantmanager.start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plantmanager.MainActivity
import com.example.plantmanager.R
import com.example.plantmanager.databinding.ActivityRegistrationCompleteBinding // ViewBinding 클래스 import

class RegistrationCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationCompleteBinding
    private var plantNickname: String? = null

    companion object {
        // 이전 화면(PlantRegistrationActivity)에서 식물 별명을 받아오기 위한 키
        const val EXTRA_PLANT_NICKNAME = "plant_nickname"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 식물 별명 가져오기
        plantNickname = intent.getStringExtra(EXTRA_PLANT_NICKNAME)

        if (plantNickname == null) {
            Log.e("RegCompleteActivity", "식물 별명이 전달되지 않았습니다.")
            // 별명이 없으면 기본 메시지 표시 또는 오류 처리
            binding.tvCompletionMessage.text = "등록이 완료되었어요!\n확인해보러 갈까요?"
        } else {
            // 별명이 있으면 형식 문자열을 사용하여 메시지 설정
            binding.tvCompletionMessage.text =
                getString(R.string.registration_complete_message, plantNickname)
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // "보러가기" 버튼 클릭 리스너
        binding.btnViewPlant.setOnClickListener {
            // MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            // 등록 완료 화면 및 이전 등록 과정 화면들을 스택에서 제거하고 MainActivity를 새 시작점으로 만듦
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // 현재 화면 종료 (필수)
        }
    }
}