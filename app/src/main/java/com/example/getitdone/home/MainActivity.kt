package com.example.getitdone.home

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.getitdone.R
import com.example.getitdone.add_update_task.AddUpdateTaskFragment
import com.example.getitdone.all_tasks.AllTasksFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
var completeAdding = ""
class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener  {

   lateinit var bottomNavigationView:BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_nav_bar)
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.selectedItemId = R.id.home_btn


    }

    var firstFragment: Fragment = TaskFragment()
    var secondFragment: Fragment = AllTasksFragment()


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home_btn -> {
                val fragmentName = intent.getIntExtra("fragment",0)
                val locationName = intent.getStringExtra("locationName")
                val addressLine = intent.getStringExtra("locationAddress")
                val taskDesc = intent.getStringExtra("taskDesc")
                val taskTitle = intent.getStringExtra("taskTitle")
                if (fragmentName == 1){
                    val args = Bundle()
                    args.putSerializable("location name",locationName)
                    args.putSerializable("location address",addressLine)
                    args.putSerializable("taskDesc",taskDesc)
                    args.putSerializable("taskTitle",taskTitle)
                    completeAdding = "complete"
                    val addTaskFragment = AddUpdateTaskFragment()
                    addTaskFragment.arguments = args
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, addTaskFragment).commit()
                } else{
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, firstFragment).commit()
                }
                return true
            }
            R.id.tasks_btn -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, secondFragment)
                    .commit()
                return true
            }
        }
        return false
    }


}