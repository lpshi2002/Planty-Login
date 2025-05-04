package com.example.plantmanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.plantmanager.data.Plant
import com.example.plantmanager.databinding.ItemPlantCardBinding

class PlantAdapter(private var plantList: MutableList<Plant>, // 표시할 식물 데이터 목록 (MutableList로 받아야 수정 가능)
                   private val onItemClick: (Plant) -> Unit // 아이템 클릭 시 호출될 함수 (람다 표현식)
) : RecyclerView.Adapter<PlantAdapter.ViewHolder>(){
    inner class ViewHolder(val binding: ItemPlantCardBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(plantList[adapterPosition])
                }
            }
        }
    }

    // 3. onCreateViewHolder: ViewHolder 객체를 생성하고 반환합니다.
    // 아이템 레이아웃(item_plant_card.xml)을 inflate하여 ViewHolder를 만듭니다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlantCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // 4. onBindViewHolder: ViewHolder에 데이터를 바인딩(연결)합니다.
    // position에 해당하는 데이터를 가져와 ViewHolder의 뷰들에 설정합니다.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plant = plantList[position]
        holder.binding.tvPlantName.text = plant.name
        holder.binding.tvPlantDescription.text = plant.description
        holder.binding.ivPlantImage.setImageResource(plant.imageResId)
    }

    // 5. getItemCount: 데이터 목록의 전체 아이템 개수를 반환합니다.
    override fun getItemCount(): Int {
        return plantList.size
    }

    // 6. (추가) 식물 추가 함수
    fun addPlant(plant: Plant) {
        plantList.add(plant)
        notifyItemInserted(plantList.size - 1)
    }

    fun removePlant(position: Int) {
        if(position >=0 && position<plantList.size) {
            plantList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
