package com.example.plantmanager.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Retrofit 인스턴스를 싱글톤으로 관리하는 객체
object RetrofitClient {

    // *** 매우 중요: 실제 운영 서버 또는 개발 서버의 기본 URL 주소를 입력해야 합니다! ***
    // 서버 개발자에게 정확한 주소를 확인하세요.
    private const val BASE_URL = "http://10.0.2.2:8080/" // <= 반드시 수정!!!
    // 예시 (내 PC에서 서버 실행 + 안드로이드 에뮬레이터 사용 시): "http://10.0.2.2:8080/"
    // 예시 (실제 도메인 사용 시): "https://api.myplantapp.com/"

    // 네트워크 통신 로그를 확인하기 위한 인터셉터 설정 (개발 시 유용)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // 로그 레벨 설정 (BODY: 요청/응답 헤더와 본문 모두 출력)
        // 앱 출시(릴리스) 시에는 보안을 위해 level = HttpLoggingInterceptor.Level.NONE 으로 변경 권장
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient 상세 설정 (로그 인터셉터 추가, 타임아웃 설정 등)
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // 로그 인터셉터 추가
        .connectTimeout(30, TimeUnit.SECONDS) // 연결 시도 시간 (30초)
        .readTimeout(30, TimeUnit.SECONDS)    // 데이터 읽기 시간 (30초)
        .writeTimeout(30, TimeUnit.SECONDS)   // 데이터 쓰기 시간 (30초)
        .build()

    // Retrofit 인스턴스 생성 (지연 초기화: 처음 사용할 때 한번만 생성)
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)              // 기본 서버 URL 설정
            .client(okHttpClient)           // 위에서 설정한 OkHttpClient 사용
            .addConverterFactory(GsonConverterFactory.create()) // JSON <-> Kotlin 데이터 클래스 변환기 지정 (Gson)
            .build()                        // Retrofit 객체 생성 완료
            .create(ApiService::class.java) // ApiService 인터페이스의 구현체 생성
    }
}