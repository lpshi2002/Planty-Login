package com.example.plantmanager.data

// 식물 등록 요청 시 서버로 보낼 데이터 형식
data class PlantRegistrationRequest(
    // *** 서버 API에서 사용하는 실제 키 이름과 일치해야 합니다! ***
    val userId: String,        // 식물 소유자의 ID (LoginActivity에서 전달받은 값 사용)
    val plantType: String,     // 식물 종류
    val nickname: String,      // 식물 별명
    val startDate: String      // 키우기 시작한 날짜 (예: "yyyy-MM-dd" 형식 문자열)
    // 서버가 다른 형식(Long timestamp 등)을 원하면 맞춰서 보내야 함
)