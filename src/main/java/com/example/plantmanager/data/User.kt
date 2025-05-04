package com.example.plantmanager.data

// 예시: 사용자 정보 데이터 클래스 (LoginResponse 안에 포함될 수 있음)
data class User(
    val userId: String,
    val nickname: String,
    val email: String
    // 필요한 다른 사용자 정보 필드들...
)