package com.example.plantmanager

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantmanager.adapter.PlantAdapter
import com.example.plantmanager.data.Plant
import com.example.plantmanager.databinding.ActivityMainBinding
import com.example.plantmanager.plantdetail.PlantDetailActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var plantAdapter: PlantAdapter // PlantAdapter 변수 선언
    private val plantDataList = mutableListOf<Plant>() // 식물 데이터 목록 (초기에는 비어있음)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. View Binding 초기화 및 화면 설정
        // ActivityMainBinding.inflate(layoutInflater): XML 레이아웃 파일을 메모리에 로드하고(inflate)
        // 해당 레이아웃의 뷰들에 접근할 수 있는 바인딩 객체를 생성합니다
        binding = ActivityMainBinding.inflate(layoutInflater)

        // binding.root: 바인딩 객체가 참조하는 레이아웃의 최상위 뷰(여기서는 ConstraintLayout)를 의미합니다.
        // setContentView(): Activity가 화면에 보여줄 내용을 설정합니다. 여기서는 XML 레이아웃 전체를 설정합니다.
        setContentView(binding.root)

        // 1. 초기 식물 데이터 준비 (임시)
        prepareInitalPlantData()

        // 2. RecyclerView 및 Adapter 설정 함수 호출
        setupRecyclerView()

        // 3. 기타 클릭 리스너 설정 (기존 코드 활용)
        setupOtherClickListeners()

    }


    // 임시 초기 데이터 준비 함수
    private fun prepareInitalPlantData() {
        plantDataList.add(Plant("1", "토토", "열매가 발견되었어요", R.drawable.tlranf))

        plantDataList.add(Plant("plant_2", "몬스테라", "건강하게 자라고 있어요", R.drawable.tlranf))
    }

    // RecyclerView 설정 함수
    private fun setupRecyclerView() {
        plantAdapter = PlantAdapter(plantDataList) { clickedPlant ->
            // --- 여기가 수정/확인 필요한 부분 ---
            Toast.makeText(this, "${clickedPlant.name} 클릭됨", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", "Plant clicked: ${clickedPlant.id}")

            // PlantDetailActivity로 이동하는 Intent 생성
            val intent = Intent(this, PlantDetailActivity::class.java)

            // 상세 화면으로 전달할 데이터 추가 (Key 값은 PlantDetailActivity와 일치해야 함)
            intent.putExtra(PlantDetailActivity.Extra_Plant_ID, clickedPlant.id) // ID 전달
            intent.putExtra(PlantDetailActivity.Extra_Plant_Name, clickedPlant.name) // 이름 전달
            intent.putExtra(
                PlantDetailActivity.Extra_Plant_Image,
                clickedPlant.imageResId
            ) // 이미지 리소스 ID 전달!

            // 새로운 Activity 시작
            startActivity(intent)
        }
        // RecyclerView에 LayoutManager와 Adapter 설정
        binding.rvMyPlants.apply {
            // LayoutManager 설정 (XML에서도 설정했으므로 여기서 생략 가능하나, 명시적으로 두어도 좋음)
            adapter = plantAdapter // 생성한 어댑터를 RecyclerView에 연결
        }

        Log.d("MainActivity", "RecyclerView 설정 완료. 아이템 개수: ${plantAdapter.itemCount}")
    }

    // 식물카드 클릭 리스너
    private fun setupOtherClickListeners() {

        binding.ivSearch.setOnClickListener {
            Toast.makeText(this, "검색 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }
        binding.ivLock.setOnClickListener {
            Toast.makeText(this, "잠금 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }
        binding.tvMyPlantsSeeMore.setOnClickListener {
            Toast.makeText(this, "'내 식물 더보기' 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }
        binding.tvRecommendedSeeMore.setOnClickListener {
            Toast.makeText(this, "'추천 용품 더보기' 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> { /*...*/ true
                }

                R.id.navigation_community -> { /*...*/ true
                }

                R.id.navigation_stores -> { /*...*/ true
                }

                R.id.navigation_profile -> { /*...*/ true
                }

                else -> false
            }
        }
        // (추가) 식물 추가 버튼 리스너 (예시)
        // binding.fabAddPlant.setOnClickListener { addNewPlant() }
    }
}
