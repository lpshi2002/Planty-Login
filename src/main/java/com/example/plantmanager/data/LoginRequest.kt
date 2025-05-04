package com.example.plantmanager.data

data class LoginRequest(
    // *** 변수 이름(userId, userPw)은 실제 서버 API에서 받는 JSON 키 이름과 일치해야 합니다! ***
    // 예: 서버에서 'username', 'password' 키를 사용한다면 변수 이름도 그렇게 맞추거나,
    // @SerializedName("username") 어노테이션을 사용할 수 있습니다 (Gson 사용 시).
    val userId: String,
    val userPw: String
)