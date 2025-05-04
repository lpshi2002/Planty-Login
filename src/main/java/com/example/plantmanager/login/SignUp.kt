package com.example.plantmanager.login

import android.content.Intent
import android.os.Bundle
import com.example.plantmanager.R
import android.util.Patterns // 이메일 검증용 import
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plantmanager.databinding.ActivitySignupBinding // ViewBinding 클래스 import
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Log
import com.example.plantmanager.data.SignupRequest // 생성한 클래스 import 확인
import com.example.plantmanager.network.RetrofitClient // 생성한 클래스 import 확인
import java.io.IOException // 예외 처리용 import

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSignupAction.setOnClickListener {
            performSignup()
        }
    }

    private fun performSignup() {
        // 1. 입력 필드에서 값 가져오기
        val nickname = binding.etNickname.text.toString().trim()
        val id = binding.etIdSignup.text.toString().trim()
        val password = binding.etPasswordSignup.text.toString().trim()
        val passwordConfirm = binding.etPasswordConfirm.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        // 2. 앱 내부 기본 유효성 검사 (빈칸, 비번 일치, 이메일 형식 등)
        if (!validateInput(nickname, id, password, passwordConfirm, email)) {
            return // 유효하지 않으면 여기서 함수 종료
        }

        // 3. 서버로 보낼 요청 데이터 생성 (사용자가 입력한 비밀번호 그대로 전달)
        val signupRequest = SignupRequest(nickname, id, password, email)

        // 4. 코루틴을 사용하여 서버에 회원가입 요청 보내기 (백그라운드 실행)
        lifecycleScope.launch {
            try {
                // RetrofitClient를 통해 ApiService의 signupUser 함수 호출
                val response = RetrofitClient.instance.signupUser(signupRequest)

                // 5. 서버로부터 받은 응답 처리
                if (response.isSuccessful && response.body() != null) {
                    // 5-1. HTTP 요청 성공 (상태 코드 2xx) 및 응답 데이터 수신 완료
                    val signupResponse = response.body()!!
                    if (signupResponse.success) {
                        // 5-1-1. 서버에서 회원가입 처리 성공 응답 (success: true)
                        Toast.makeText(this@SignUp, "회원가입 성공!", Toast.LENGTH_SHORT).show()

                        // *** 회원가입 성공 후에는 앱에 아이디/비번 등을 저장하지 않습니다! ***
                        // 다음 화면으로 이동하거나 로그인 화면으로 돌아가도록 처리합니다.
                        // TODO: 다음 화면(예: 식물 등록)으로 이동하는 코드 구현
                        val intent = Intent(this@SignUp, Login::class.java)
                        startActivity(intent)
                        finish() // 회원가입 화면 종료

                    } else {
                        // 5-1-2. 서버에서 회원가입 처리 실패 응답 (success: false)
                        // 실패 사유(errorCode)에 따라 사용자에게 피드백 제공
                        when (signupResponse.errorCode) {
                            "DUPLICATE_ID" -> {
                                binding.tilIdSignup.error = signupResponse.message // 예: "이미 사용 중인 아이디입니다."
                            }
                            "DUPLICATE_EMAIL" -> {
                                binding.tilEmail.error = signupResponse.message // 예: "이미 등록된 이메일입니다."
                            }
                            // TODO: 서버에서 정의한 다른 에러 코드 처리 추가
                            else -> {
                                // 기타 서버 로직 오류
                                Toast.makeText(this@SignUp, "회원가입 실패: ${signupResponse.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    // 5-2. HTTP 요청 실패 (상태 코드 4xx, 5xx 등)
                    val errorBody = response.errorBody()?.string() // 서버가 보낸 에러 내용 확인 시도
                    Log.e("SignupActivity", "API Error: Code=${response.code()}, Message=${response.message()}, Body=$errorBody")
                    Toast.makeText(this@SignUp, "회원가입 중 오류 발생 (코드: ${response.code()})", Toast.LENGTH_SHORT).show()
                }

            } catch (e: IOException) {
                // 5-3. 네트워크 연결 오류 (인터넷 끊김, 서버 응답 없음 등)
                Log.e("SignupActivity", "Network IOException: ${e.message}", e)
                Toast.makeText(this@SignUp, "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // 5-4. 기타 예상치 못한 오류 (데이터 파싱 실패 등)
                Log.e("SignupActivity", "Exception: ${e.message}", e)
                Toast.makeText(this@SignUp, "알 수 없는 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        } // 코루틴 종료
    } // performSignup 함수 종료

    // 입력값 유효성 검사 함수
    private fun validateInput(nickname: String, id: String, pass: String, passConfirm: String, email: String): Boolean {
        // 모든 필드 초기화 (이전 에러 메시지 제거)
        binding.tilNickname.error = null
        binding.tilIdSignup.error = null
        binding.tilPasswordSignup.error = null
        binding.tilPasswordConfirm.error = null
        binding.tilEmail.error = null

        var isValid = true

        if (nickname.isEmpty()) {
            binding.tilNickname.error = getString(R.string.error_empty_field)
            isValid = false
        }
        if (id.isEmpty()) {
            binding.tilIdSignup.error = getString(R.string.error_empty_field)
            isValid = false
        }
        if (pass.isEmpty()) {
            binding.tilPasswordSignup.error = getString(R.string.error_empty_field)
            isValid = false
        }
        if (passConfirm.isEmpty()) {
            binding.tilPasswordConfirm.error = getString(R.string.error_empty_field)
            isValid = false
        } else if (pass != passConfirm) {
            binding.tilPasswordConfirm.error = getString(R.string.error_password_mismatch)
            binding.tilPasswordSignup.error = " " // 두 필드 모두에 에러 표시 (선택 사항)
            isValid = false
        }
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_empty_field)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // 이메일 형식 검사
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        return isValid
    }
}