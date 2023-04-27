package com.example.roomdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdemo.databinding.ItemsRowBinding

class ItemAdapter(private val items:List<EmployeeEntity>, private val onItemDelete: (id: Int) -> Unit, private val onItemEdit: (id: Int) -> Unit): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemsRowBinding): RecyclerView.ViewHolder(binding.root) {
        val llm = binding.llMain;
        val tvName = binding.tvName;
        val tvEmail = binding.tvEmail;
        val ivDelete = binding.ivDelete;
        val ivEdit = binding.ivEdit;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemsRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return  items.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context;
        val currentItem = items[position];
        holder.tvName.text = currentItem.name;
        holder.tvEmail.text = currentItem.email;
        if(position % 2 == 0) {
            holder.llm.setBackgroundColor(ContextCompat.getColor(context,R.color.purple_200))
        } else {
            holder.llm.setBackgroundColor(ContextCompat.getColor(context,R.color.teal_700))
        }
        holder.ivDelete.setOnClickListener {
            onItemDelete.invoke(currentItem.id)
        }
        holder.ivEdit.setOnClickListener {
            onItemEdit.invoke(currentItem.id)
        }
    }
}