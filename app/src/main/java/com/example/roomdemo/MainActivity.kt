package com.example.roomdemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.example.roomdemo.databinding.ActivityMainBinding
import com.example.roomdemo.databinding.DialogUpdateBinding
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
            var itemAdapter: ItemAdapter = ItemAdapter(list, {deleteId ->
                lifecycleScope.launch {
                    employeeDao.fetchAllEmployees(deleteId).collect {
                        if (it != null) {
                            deleteRecordAlertDialog(deleteId, employeeDao, it)
                        }
                    }
                }
            } ,{updateId ->
                updateRecordDialog(updateId,employeeDao)
            });
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
    fun updateRecordDialog(id:Int,employeeDao: EmployeeDao)  {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchAllEmployees(id).collect {
                if (it != null) {
                    binding.etUpdateName.setText(it.name)
                    binding.etUpdateEmailId.setText(it.email)
                }
            }
        }
        binding.tvUpdate.setOnClickListener {

            val name = binding.etUpdateName.text.toString()
            val email = binding.etUpdateEmailId.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id, name, email))
                    Toast.makeText(applicationContext, "Record Updated.", Toast.LENGTH_LONG)
                        .show()
                    updateDialog.dismiss() // Dialog will be dismissed
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or Email cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.tvCancel.setOnClickListener{
            updateDialog.dismiss()
        }
        updateDialog.show()
    }

    fun deleteRecordAlertDialog(id:Int,employeeDao: EmployeeDao,employee: EmployeeEntity) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you wants to delete ${employee.name}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(
                    applicationContext,
                    "Record deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()

                dialogInterface.dismiss()
            }

        }


        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}