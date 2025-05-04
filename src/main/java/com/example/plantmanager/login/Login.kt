package com.example.plantmanager.login // 실제 패키지 경로 확인

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.plantmanager.MainActivity
import com.example.plantmanager.R
import com.example.plantmanager.data.LoginRequest
import com.example.plantmanager.data.User // User 클래스 사용 시 import
import com.example.plantmanager.databinding.ActivityLoginBinding // 여기서는 LoginActivityBinding 이어야 함
import com.example.plantmanager.login.SignUp
import com.example.plantmanager.network.RetrofitClient
import com.example.plantmanager.start.PlantRegistrationActivity
import kotlinx.coroutines.launch
import java.io.IOException

// Login 클래스 이름 대신 LoginActivity 사용 권장 (이전 논의 기반)
class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding // 파일명에 맞는 바인딩 클래스
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val PREFS_FILENAME = "UserPrefs"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_AUTH_TOKEN = "authToken"
        const val KEY_USER_NICKNAME = "userNickname" // 닉네임 저장용 키
        const val KEY_USER_ID = "userId"           // 사용자 ID 저장용 키 (또는 토큰만 사용)
        const val KEY_REQUIRES_PLANT_REGISTRATION = "requiresPlantRegistration" // 상태 저장용 키
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

        // --- 자동 로그인 로직 수정 ---
        if (isUserLoggedIn()) {
            // 자동 로그인 시, 저장된 상태를 보고 적절한 화면으로 바로 이동
            navigateToAppropriateScreen()
            return // LoginActivity UI를 보여줄 필요 없으므로 여기서 종료
        }
        // --- 자동 로그인 대상이 아니면 UI 설정 ---

        binding = ActivityLoginBinding.inflate(layoutInflater) // LoginActivityBinding 사용
        setContentView(binding.root)
        setupClickListeners()
    }

    // 기본적인 로그인 여부 확인 (토큰 또는 플래그 기준)
    private fun isUserLoggedIn(): Boolean {
        // 토큰 유무로 판단하는 것이 더 일반적
        //return sharedPreferences.getString(KEY_AUTH_TOKEN, null) != null
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // SharedPreferences에서 식물 등록 필요 상태 읽기
    private fun checkRequiresPlantRegistration(): Boolean {
        // 기본값은 true (정보 없으면 등록 필요하다고 가정)
        return sharedPreferences.getBoolean(KEY_REQUIRES_PLANT_REGISTRATION, true)
    }

    // SharedPreferences에서 필요한 사용자 정보 읽기 (닉네임, ID 등)
    private fun getStoredUserNickname(): String? {
        return sharedPreferences.getString(KEY_USER_NICKNAME, null)
    }
    private fun getStoredUserIdOrToken(): String? {
        // ID를 저장했으면 ID 반환, 아니면 토큰 반환 (API 호출 방식에 따라 선택)
        //return sharedPreferences.getString(KEY_AUTH_TOKEN, null) // 예시: 토큰 반환
        return sharedPreferences.getString(KEY_USER_ID, null)
    }


    // 상태에 따라 적절한 화면으로 이동시키는 함수
    private fun navigateToAppropriateScreen() {
        val requiresPlantReg = checkRequiresPlantRegistration()

        if (requiresPlantReg) {
            // 식물 등록 필요 -> PlantRegistrationActivity 로 이동
            val nickname = getStoredUserNickname() ?: "사용자" // 저장된 닉네임 (없으면 기본값)
            val userIdOrToken = getStoredUserIdOrToken()

            if (userIdOrToken == null) {
                Log.e("LoginActivity", "자동 로그인 실패: 사용자 ID/토큰 정보 없음")
                // 오류 처리: 로그인 상태 강제 해제 등
                saveLoginState(false, null, null, true) // 로그아웃 상태로 만듦
                return
            }

            val intent = Intent(this, PlantRegistrationActivity::class.java)
            intent.putExtra(PlantRegistrationActivity.EXTRA_USER_NICKNAME, nickname)
            intent.putExtra(PlantRegistrationActivity.EXTRA_USER_ID_TOKEN, userIdOrToken)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 새 시작점으로
            startActivity(intent)
            finish() // 로그인 화면 종료
        } else {
            // 식물 등록 불필요 (이미 완료) -> MainActivity 로 이동
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 새 시작점으로
            startActivity(intent)
            finish() // 로그인 화면 종료
        }
    }

    // 로그인 상태 및 사용자 정보, 식물 등록 필요 여부 저장 함수
    private fun saveLoginState(isLoggedIn: Boolean, token: String?, userData: User?, requiresPlantReg: Boolean?) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn) // 기본 로그인 상태 플래그 (선택적)

        if (isLoggedIn && token != null && userData != null && requiresPlantReg != null) {
            // 로그인 성공 시 정보 저장
            editor.putString(KEY_AUTH_TOKEN, token)
            editor.putString(KEY_USER_ID, userData.userId) // User 데이터 클래스에 userId 필드 필요
            editor.putString(KEY_USER_NICKNAME, userData.nickname)
            editor.putBoolean(KEY_REQUIRES_PLANT_REGISTRATION, requiresPlantReg)
        } else {
            // 로그아웃 또는 실패 시 정보 제거
            editor.remove(KEY_AUTH_TOKEN)
            editor.remove(KEY_USER_ID)
            editor.remove(KEY_USER_NICKNAME)
            editor.remove(KEY_REQUIRES_PLANT_REGISTRATION)
        }
        editor.apply()
    }

    // 클릭 리스너 설정 함수
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            var isValid = true // isValid 변수 선언 및 초기화
            // --- ↑↑↑ 여기까지 확인 ↑↑↑ ---

            // --- 그 다음에 유효성 검사 로직 ---
            if (id.isEmpty()) {
                binding.tilId.error = "아이디를 입력해주세요."
                isValid = false // 여기서 isValid 사용
            } else {
                binding.tilId.error = null
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "비밀번호를 입력해주세요."
                isValid = false // 여기서 isValid 사용
            } else {
                binding.tilPassword.error = null
            }
            if (!isValid) return@setOnClickListener

            val loginRequest = LoginRequest(userId = id, userPw = password)

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.loginUser(loginRequest)

                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!
                        if (loginResponse.success) {
                            // 서버 로직 상 로그인 성공
                            Toast.makeText(this@Login, "로그인 성공!", Toast.LENGTH_SHORT).show()

                            // *** 상태 저장 ***
                            // 서버 응답에서 식물 등록 필요 여부(requiresPlantRegistration) 확인
                            // 서버가 null을 보낼 경우 기본값(true) 처리 등 필요할 수 있음
                            val requiresReg = loginResponse.requiresPlantRegistration ?: true
                            saveLoginState(true, loginResponse.token, loginResponse.userData, requiresReg)

                            // *** 적절한 화면으로 이동 ***
                            navigateToAppropriateScreen()

                        } else {
                            // 서버 로직 상 로그인 실패
                            Toast.makeText(this@Login, loginResponse.message, Toast.LENGTH_LONG).show()
                            saveLoginState(false, null, null, true) // 로그인 상태 초기화
                        }
                    } else {
                        // HTTP 요청 실패
                        Toast.makeText(this@Login, "로그인 실패 (코드: ${response.code()})", Toast.LENGTH_SHORT).show()
                        saveLoginState(false, null, null, true) // 로그인 상태 초기화
                    }
                } catch (e: Exception) { // IOException 포함 모든 예외 처리
                    // 네트워크 또는 기타 오류
                    Toast.makeText(this@Login, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Login Exception", e)
                    saveLoginState(false, null, null, true) // 로그인 상태 초기화
                }
            } // 코루틴 종료
        } // 로그인 버튼 리스너 종료

        // 회원가입 버튼 리스너
        binding.btnSignup.setOnClickListener {
            // SignupActivity 또는 SignUp 클래스로 변경
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        // 비밀번호 찾기 리스너
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, getString(R.string.forgot_password_clicked), Toast.LENGTH_SHORT).show()
        }
        // 소셜 로그인 리스너...
    }
}