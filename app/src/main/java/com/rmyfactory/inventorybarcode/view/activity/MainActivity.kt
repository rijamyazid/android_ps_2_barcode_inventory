package com.rmyfactory.inventorybarcode.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.rmyfactory.inventorybarcode.R
import com.rmyfactory.inventorybarcode.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bnvMain.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.orderFragment -> hideBottomNav()
                R.id.logFragment -> hideBottomNav()
                else -> showBottomNav()
            }
        }
    }

    fun showBottomNav() {
        binding.bnvMain.visibility = View.VISIBLE
    }

    fun hideBottomNav() {
        binding.bnvMain.visibility = View.GONE
    }
}