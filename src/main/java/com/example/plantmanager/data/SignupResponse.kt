package com.example.plantmanager.data

data class SignupResponse(
    val success: Boolean,      // 회원가입 처리 성공 여부 (서버 로직 결과)
    val message: String,       // 서버 응답 메시지 (예: "회원가입 성공", "이미 사용 중인 아이디입니다.")
    val errorCode: String? = null // 에러 발생 시 구체적인 원인 코드 (예: "DUPLICATE_ID"), 없을 수도 있음 (Nullable)
    // 필요에 따라 서버가 성공 시 추가 데이터를 보낼 수도 있습니다. (예: 생성된 사용자 ID 등)
)