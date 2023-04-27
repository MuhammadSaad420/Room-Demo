package com.example.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.example.roomdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding?.root)
        val employeeDao: EmployeeDao? = (application as EmployeeApp).db?.employeeDao()
        binding?.btnAdd?.setOnClickListener {
            employeeDao?.let {
                add(it);
            }
        }
        lifecycleScope.launch {
            employeeDao?.fetchAllEmployees()?.collect {
                setUpListOfDataInRecyclerView(ArrayList(it), employeeDao);
            }
        }
    }

    fun add(employeeDao: EmployeeDao) {
        val email : String = binding?.etEmailId?.text.toString();
        val name : String = binding?.etName?.text.toString();
            if(name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    employeeDao.insert(EmployeeEntity(name = name, email = email))
                    Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()
                    binding?.etName?.text?.clear()
                    binding?.etEmailId?.text?.clear()
                }

            } else {
                Toast.makeText(applicationContext,"Please Enter valid data",Toast.LENGTH_SHORT);
            }
    }

    fun setUpListOfDataInRecyclerView(list: ArrayList<EmployeeEntity>, employeeDao: EmployeeDao) {
        if(list.isNotEmpty()) {
            var itemAdapter: ItemAdapter = ItemAdapter(list);
            var layoutManager: LayoutManager = LinearLayoutManager(this);
            binding?.rvItemsList?.layoutManager = layoutManager;
            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE;
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
        } else {
            binding?.rvItemsList?.visibility = View.GONE;
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }

    }
}