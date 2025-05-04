package com.example.plantmanager.data

data class SignupRequest(
    val nickname: String,   // 서버 API 명세에 맞는 닉네임 필드 이름 사용
    val userId: String,     // 서버 API 명세에 맞는 아이디 필드 이름 사용 (예: id, username)
    val userPw: String,     // 서버 API 명세에 맞는 비밀번호 필드 이름 사용 (예: password, pw)
    // *** 앱은 사용자가 입력한 비밀번호를 그대로 보냅니다. ***
    // *** 비밀번호 암호화(해싱)는 서버에서 처리합니다! ***
    val email: String       // 서버 API 명세에 맞는 이메일 필드 이름 사용
)