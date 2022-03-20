package com.maricoolsapps.e_commerce.ui.user_authentication_ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.CheckMessage
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.source.FirebaseAuthSource
import com.maricoolsapps.e_commerce.data.source.FirebaseFirestoreSource
import com.maricoolsapps.e_commerce.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.maricoolsapps.e_commerce.utils.Constants.descriptionText
import com.maricoolsapps.e_commerce.utils.Constants.name
import com.maricoolsapps.e_commerce.utils.Status
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var bottomBar: BottomNavigationView
    lateinit var progressBar: SpinKitView
    lateinit var toolbar: Toolbar

    @Inject
    lateinit var source: CloudQueries

    @Inject
    lateinit var auth: ProfileChanges

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.second_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomBar = findViewById(R.id.bottom_nav)
        progressBar = findViewById(R.id.progress_bar)
        toolbar = findViewById(R.id.toolbar)
        bottomBar.visibility = View.GONE
        toolbarInit()
        NavigationUI.setupWithNavController(bottomBar, navController)
        //setupSmoothBottomMenu()
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.firstFragment -> {
                    bottomBar.toggleVisibility(false)
                    toolbar.toggleVisibility(false)
                }
                R.id.loginFragment -> {
                    bottomBar.toggleVisibility(false)
                    toolbar.toggleVisibility(false)
                }
                R.id.registerFragment -> {
                    bottomBar.toggleVisibility(false)
                    toolbar.toggleVisibility(false)
                }
                R.id.mainFragment -> {
                    auth.getAuthState().observeForever {
                        if (it != null) {
                            source.checkIfUserHasNewMessages(it.uid).observeForever {  res ->
                                //   Log.d("testTag", res.toString())
                                if (res != null && res){
                                    bottomBar.getOrCreateBadge(R.id.chatListFragment)
                                } else {
                                    bottomBar.removeBadge(R.id.chatListFragment)
                                }
                            }
                        }
                    }
                    bottomBar.toggleVisibility(true)
                    toolbar.toggleVisibility(false)
                }
                R.id.favoriteFragment -> {
                    bottomBar.toggleVisibility(true)
                    toolbar.toggleVisibility(true)
                }
                R.id.chatFragment -> {
                    bottomBar.toggleVisibility(false)
                    toolbar.toggleVisibility(false)
                }
                R.id.chatListFragment -> {
                    bottomBar.toggleVisibility(true)
                    toolbar.toggleVisibility(true)
                }
                R.id.sellFragment -> {
                    bottomBar.toggleVisibility(true)
                    toolbar.toggleVisibility(true)
                }
                R.id.profileFragment -> {
                    bottomBar.toggleVisibility(true)
                    toolbar.toggleVisibility(true)
                }
                R.id.followersFragment -> {
                    bottomBar.toggleVisibility(false)
                    toolbar.toggleVisibility(false)
                }
                R.id.pictureFragment -> {
                    toolbar.toggleVisibility(false)
                    progressBar.toggleVisibility(false)
                }
                else -> {
                    bottomBar.toggleVisibility(false)
                    toolbar.toggleVisibility(true)
                }
            }
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun toolbarInit() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.getAuthState().removeObservers(this)
    }

}