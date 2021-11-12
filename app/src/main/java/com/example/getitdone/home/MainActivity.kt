package com.example.getitdone.home

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.getitdone.R
import com.example.getitdone.all_tasks.AllTasksFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


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
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, firstFragment)
                    .commit()
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