package com.example.plantmanager.plantdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem // 뒤로가기 버튼 처리를 위해 추가
import android.widget.Toast
import com.example.plantmanager.R
import com.example.plantmanager.databinding.ActivityPlantDetailBinding

class PlantDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlantDetailBinding

    // MainActivity에서 전달한 데이터 키 (상수로 정의하는 것이 좋음)
    companion object{
        const val Extra_Plant_ID="plant_id"
        const val Extra_Plant_Name="plant_name"
        const val Extra_Plant_Image="plant_image"
    }

    // 데이터 변수 선언 (MainActivity에서 전달받을 데이터)
    private var plantId: String?=null
    private var plantName: String?=null
    private var plantImageResId: Int = R.drawable.default_plant_placeholder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding 초기화 및 화면 설정
        binding = ActivityPlantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 데이터 추출 함수 호출
        getIntentData()

        // 툴바 설정 함수 호출
        setupToolbar()

        // 추출된 데이터로 UI 업데이트 함수 호출
        updateUi()

        // 클릭 리스너 등 설정 함수 호출 (필요하다면)
        setupClickListeners()

        Log.d("PlantDetailActivity", "onCreate: Plant ID = $plantId, Name = $plantName")

    }
        // Intent에서 데이터 추출하는 함수
    private fun getIntentData() {
        plantId = intent.getStringExtra(Extra_Plant_ID)
        plantName = intent.getStringExtra(Extra_Plant_Name)
        // 이미지 리소스 ID 받기 (기본값 설정)
        plantImageResId = intent.getIntExtra(Extra_Plant_Image, R.drawable.default_plant_placeholder)

        // 만약 필수 데이터(ID 또는 이름)가 없다면 오류 처리 또는 Activity 종료
        if (plantId == null || plantName == null) {
            Log.e("PlantDetailActivity", "필수 데이터(ID 또는 이름)가 전달되지 않았습니다.")
            Toast.makeText(this, "식물 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish() // 현재 Activity 종료
        }
    }

        // 툴바 설정 함수
    private fun setupToolbar() {
            setSupportActionBar(binding.toolbar) // 생성한 툴바를 액션바로 설정
            supportActionBar?.apply {
                // 툴바에 제목 설정 (전달받은 식물 이름 사용, null이면 기본 제목)
                title = plantName ?: "식물 상세 정보"
                // 뒤로가기 버튼 (Up Button) 표시
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
    }


        // 전달받은 데이터로 UI 요소 업데이트 함수
    private fun updateUi() {
        // ViewBinding을 사용하여 각 View에 데이터 설정
        binding.tvPlantDetailNickname.text = plantName // 닉네임 표시 (여기서는 이름으로 대체)
        // 실제 식물 종류는 plantId를 이용해 DB 등에서 가져와야 함
        binding.tvPlantDetailName.text = "($plantId) 식물 종류 정보 필요"

        // 이미지 설정
        if (plantImageResId != 0 && plantImageResId != R.drawable.default_plant_placeholder) { // 유효한 리소스 ID인지 확인 (기본값 제외)
            binding.ivPlantDetailImage.setImageResource(plantImageResId)
        } else {
            // 유효하지 않거나 기본값이면 기본 이미지 설정
            binding.ivPlantDetailImage.setImageResource(R.drawable.default_plant_placeholder)
            Log.w("PlantDetailActivity", "유효한 식물 이미지가 없어 기본 이미지를 표시합니다.")
        }

        // TODO: plantId를 사용하여 데이터베이스나 다른 저장소에서
        //       식물의 상세 정보(실제 종류, 습도, 물탱크 상태 등)를 비동기적으로 가져와서
        //       해당하는 TextView(tv_soil_percentage, tv_water_percentage 등),
        //       SeekBar(seekBar_light_temp, seekBar_brightness) 등에 표시하는 로직 추가
        //       (예: LiveData, ViewModel, Coroutines 사용)
    }


        // 클릭 리스너 설정 함수 (예: 좋아요 버튼)
    private fun setupClickListeners() {
        binding.ivFavorite.setOnClickListener {
            // TODO: 좋아요 상태 토글 및 DB/서버에 상태 저장 로직 구현
            // TODO: 좋아요 상태에 따라 아이콘 변경 (ic_favorite vs ic_favorite_border)
            Toast.makeText(this, "좋아요 버튼 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }

        // TODO: 정보 카드 클릭 시 동작 추가 (예: 상세 통계 화면 이동)
        binding.layoutSoilMoisture.setOnClickListener {
            Toast.makeText(this, "토양 습도 정보 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }
        binding.layoutWaterTank.setOnClickListener {
            Toast.makeText(this, "물탱크 정보 클릭됨 (구현 필요)", Toast.LENGTH_SHORT).show()
        }

        // TODO: SeekBar 변경 리스너 추가
        // binding.seekBarLightTemp.setOnSeekBarChangeListener(...)
        // binding.seekBarBrightness.setOnSeekBarChangeListener(...)
    }

    // 옵션 메뉴 아이템 선택 처리 함수 (툴바의 뒤로가기 버튼 클릭 처리)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // android.R.id.home은 툴바의 뒤로가기(Up) 버튼의 ID입니다.
        if (item.itemId == android.R.id.home) {
            // 현재 Activity를 종료하고 이전 화면(MainActivity)으로 돌아갑니다.
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
