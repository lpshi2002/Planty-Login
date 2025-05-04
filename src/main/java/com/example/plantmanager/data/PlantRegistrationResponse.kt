package com.example.plantmanager.data // 실제 패키지 경로 확인

// 식물 등록 요청 후 서버로부터 받을 응답 데이터 형식
data class PlantRegistrationResponse(
    // *** 서버 API 응답의 실제 키 이름과 일치해야 합니다! ***
    val success: Boolean,      // 등록 성공 여부
    val message: String,       // 서버 메시지
    val plantId: String? = null // 성공 시 새로 생성된 식물의 ID (선택 사항)
    // 필요한 다른 응답 데이터 필드 추가 가능
)