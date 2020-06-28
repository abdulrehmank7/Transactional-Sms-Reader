package com.arkapp.payoassignment.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.arkapp.payoassignment.R
import com.arkapp.payoassignment.utils.getAllSms
import com.arkapp.payoassignment.utils.hide
import com.arkapp.payoassignment.utils.show
import com.arkapp.payoassignment.utils.toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var shownDenied = false

    private val askSmsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getAllSms(lifecycleScope)
                loadSplash()
            }
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getAllSms(lifecycleScope)
            loadSplash()
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && !shownDenied) {
            permissionDenied()
        }
    }

    private fun loadSplash() {
        Handler().postDelayed({
            findNavController(R.id.fragment).navigate(R.id.action_splashFragment_to_homeFragment)
        }, 1500)
    }

    private fun permissionDenied() {
        toast("SMS permission is required to use this app! Please give sms permission.")
        askSmsPermission.launch(Manifest.permission.READ_SMS)
        shownDenied = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askSmsPermission.launch(Manifest.permission.READ_SMS)

        val navController = findNavController(R.id.fragment)

        val appBarConfiguration = AppBarConfiguration
            .Builder(R.id.homeFragment, R.id.historyFragment, R.id.tagFragment)
            .build()

        setupWithNavController(bottomNavigation, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        this.findNavController(R.id.fragment)
            .addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
                if (destination.id == R.id.splashFragment) {
                    supportActionBar!!.hide()
                    bottomNavigation.hide()
                } else {
                    supportActionBar!!.show()
                    bottomNavigation.show()
                }
            }
    }
}