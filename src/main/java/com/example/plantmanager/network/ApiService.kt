package com.example.plantmanager.network

import com.example.plantmanager.data.SignupRequest // 생성한 데이터 클래스 import
import com.example.plantmanager.data.SignupResponse
import com.example.plantmanager.data.LoginRequest
import com.example.plantmanager.data.LoginResponse
import com.example.plantmanager.data.PlantRegistrationRequest
import com.example.plantmanager.data.PlantRegistrationResponse
// 필요한 다른 Request/Response 클래스들도 import
import retrofit2.Response // Retrofit 응답 클래스 import
import retrofit2.http.Body
import retrofit2.http.POST

// 서버 API 엔드포인트 명세를 정의하는 인터페이스
interface ApiService {

    // 회원가입 API 함수 정의
    @POST("signup") // 1. HTTP POST 메소드 사용, 2. 서버의 회원가입 엔드포인트 경로 지정 (예: "/user/register")
    // *** 이 경로는 실제 서버 API 경로와 일치해야 합니다! (Base URL 제외) ***
    suspend fun signupUser( // 코루틴 비동기 처리를 위해 suspend 키워드 사용
        @Body request: SignupRequest // @Body: request 객체를 JSON으로 변환하여 HTTP 요청 본문에 담아 전송
    ): Response<SignupResponse> // 반환 타입: 서버로부터 SignupResponse 형식의 데이터를 포함하는 HTTP 응답 전체를 받음

    @POST("login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("plants")
    suspend fun registerPlant(
        @Body request: PlantRegistrationRequest // 요청 본문에 식물 정보 전달
    ): Response<PlantRegistrationResponse> // PlantRegistrationResponse 형태로 응답 받음
    // TODO: 로그인, 식물 등록 등 필요한 다른 API 함수들을 여기에 추가 정의
    // 예:
    // @POST("login")
    // suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
}