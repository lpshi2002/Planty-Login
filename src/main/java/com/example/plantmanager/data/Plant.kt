package com.example.plantmanager.data

data class Plant(val id: String, // 각 식물을 구분할 고유 ID (예: "plant_1", UUID 등)
                 val name: String, // 식물 이름 (예: "방울토마토")
                 val description: String, // 식물 설명 (예: "열매가 발견되었어요")
                 val imageResId: Int // 식물 이미지 리소스 ID (예: R.drawable.tomato_image)
                // 필요에 따라 다른 정보 추가 (예: 습도, 온도 등)
)
