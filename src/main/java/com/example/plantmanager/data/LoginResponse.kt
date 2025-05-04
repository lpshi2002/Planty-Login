package com.example.plantmanager.data

data class LoginResponse(
    // *** 변수 이름(success, message, token, userData)은 실제 서버 API 응답의 JSON 키 이름과 일치해야 합니다! ***
    val success: Boolean,      // 로그인 성공 여부 (서버 로직 결과)
    val message: String,       // 서버가 보내는 메시지 (예: "로그인 성공", "비밀번호 불일치")
    val token: String? = null, // 로그인 성공 시 받을 수 있는 인증 토큰 (JWT 등), 없을 수도 있으므로 Nullable(?)
    val userData: User? = null, // 로그인 성공 시 받을 수 있는 사용자 정보 (선택 사항), 없을 수도 있으므로 Nullable(?)
    val requiresPlantRegistration: Boolean? = null // 로그인을 한적이 있는지
)

