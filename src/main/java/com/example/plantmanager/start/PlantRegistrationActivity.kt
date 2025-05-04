package com.example.plantmanager.start // 실제 패키지 경로 확인

import android.app.DatePickerDialog
import android.content.Context // Context import 추가 (SharedPreferences 용)
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope // Coroutine Scope import
import com.example.plantmanager.R
import com.example.plantmanager.data.PlantRegistrationRequest // 생성한 데이터 클래스 import
import com.example.plantmanager.databinding.ActivityPlantRegistrationBinding
import com.example.plantmanager.login.Login
import com.example.plantmanager.network.RetrofitClient // RetrofitClient import
import kotlinx.coroutines.launch // Coroutine launch import
import java.io.IOException // 예외 처리 import
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlantRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantRegistrationBinding
    private var userNickname: String? = null
    private var userId: String? = null // 이전 화면에서 받은 사용자 ID (토큰이 아님을 가정)
    private var selectedDateInMillis: Long = 0L

    companion object {
        const val EXTRA_USER_NICKNAME = "user_nickname"
        const val EXTRA_USER_ID_TOKEN = "user_id_token" // 여기로 실제 User ID가 전달된다고 가정
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userNickname = intent.getStringExtra(EXTRA_USER_NICKNAME)
        userId = intent.getStringExtra(EXTRA_USER_ID_TOKEN) // ID 받기

        if (userNickname == null || userId == null) { // ID null 체크 추가
            Log.e("PlantRegActivity", "사용자 정보(닉네임 또는 ID)가 전달되지 않았습니다.")
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvWelcomeUser.text = getString(R.string.welcome_plant_registration, userNickname)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.etPlantDate.setOnClickListener { showDatePickerDialog() }
        binding.btnRegisterPlant.setOnClickListener { registerPlant() }
    }

    private fun showDatePickerDialog() {
        // ... (DatePickerDialog 관련 코드는 이전과 동일) ...
        val calendar = Calendar.getInstance()
        if (selectedDateInMillis != 0L) { calendar.timeInMillis = selectedDateInMillis }
        val year = calendar.get(Calendar.YEAR); val month = calendar.get(Calendar.MONTH); val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog( this, { _, y, m, d ->
            val selectedCalendar = Calendar.getInstance(); selectedCalendar.set(y, m, d)
            selectedDateInMillis = selectedCalendar.timeInMillis
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.etPlantDate.setText(dateFormat.format(selectedCalendar.time))
            binding.tilPlantDate.error = null
        }, year, month, day )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    // --- 식물 등록 로직 수정 ---
    private fun registerPlant() {
        val plantType = binding.etPlantType.text.toString().trim()
        val plantNickname = binding.etPlantNickname.text.toString().trim()
        val startDate = binding.etPlantDate.text.toString().trim() // "yyyy-MM-dd"

        // 유효성 검사 (userId는 이미 onCreate에서 확인했으므로 생략 가능, 또는 여기서 다시 확인)
        var isValid = true
        // ... (plantType, plantNickname, startDate 빈 값 검사 로직 - 이전과 동일) ...
        if (plantType.isEmpty()) { binding.tilPlantType.error = getString(R.string.error_empty_field); isValid = false } else { binding.tilPlantType.error = null }
        if (plantNickname.isEmpty()) { binding.tilPlantNickname.error = getString(R.string.error_empty_field); isValid = false } else { binding.tilPlantNickname.error = null }
        if (startDate.isEmpty()) { binding.tilPlantDate.error = getString(R.string.error_empty_field); isValid = false }
        // userId null 체크 (안전을 위해 한번 더)
        if (userId == null) {
            Toast.makeText(this, "사용자 정보 오류. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            Log.e("PlantRegActivity", "userId is null during registration attempt.")
            isValid = false
        }

        if (!isValid) { return }

        // 서버로 보낼 요청 데이터 생성 (userId 사용 확인!)
        // *** PlantRegistrationRequest 필드 이름(userId, plantType 등)은 서버와 일치해야 함! ***
        val plantRequest = PlantRegistrationRequest(
            userId = userId!!, // 위에서 null 체크 했으므로 !! 사용 (또는 안전 호출 ?.let 사용)
            plantType = plantType,
            nickname = plantNickname,
            startDate = startDate
        )

        // 코루틴으로 API 호출
        lifecycleScope.launch {
            try {
                // API 호출
                val response = RetrofitClient.instance.registerPlant(plantRequest)

                // 응답 처리
                if (response.isSuccessful && response.body() != null) {
                    val registrationResponse = response.body()!!
                    if (registrationResponse.success) {
                        // 서버 로직 상 등록 성공
                        Toast.makeText(this@PlantRegistrationActivity, getString(R.string.plant_registration_success), Toast.LENGTH_SHORT).show()

                        // 식물 등록 완료 상태 저장 (SharedPreferences 업데이트)
                        updatePlantRegistrationStatus(false)

                        // 등록 완료 화면으로 이동 (식물 별명 전달)
                        val intent = Intent(this@PlantRegistrationActivity, RegistrationCompleteActivity::class.java)
                        intent.putExtra(RegistrationCompleteActivity.EXTRA_PLANT_NICKNAME, plantNickname)
                        startActivity(intent)
                        finish() // 현재 화면 종료
                    } else {
                        // 서버 로직 상 등록 실패
                        Toast.makeText(this@PlantRegistrationActivity, "식물 등록 실패: ${registrationResponse.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // HTTP 요청 실패
                    val errorMsg = "식물 등록 오류 (코드: ${response.code()})"
                    Toast.makeText(this@PlantRegistrationActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    Log.e("PlantRegActivity", "API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: IOException) {
                // 네트워크 오류
                Toast.makeText(this@PlantRegistrationActivity, "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
                Log.e("PlantRegActivity", "Network IOException: ${e.message}", e)
            } catch (e: Exception) {
                // 기타 오류
                Toast.makeText(this@PlantRegistrationActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("PlantRegActivity", "Exception: ${e.message}", e)
            }
        } // 코루틴 종료
    } // registerPlant 종료

    // SharedPreferences에 식물 등록 필요 상태 업데이트 함수 (이전 답변 내용과 동일)
    private fun updatePlantRegistrationStatus(requiresRegistration: Boolean) {
        val sharedPreferences = getSharedPreferences(Login.PREFS_FILENAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // LoginActivity에서 사용하는 키와 동일한 키 사용!
        editor.putBoolean(Login.KEY_REQUIRES_PLANT_REGISTRATION, requiresRegistration)
        editor.apply()
        Log.d("PlantRegActivity", "식물 등록 상태 업데이트: $requiresRegistration")
    }

} // PlantRegistrationActivity 클래스 종료