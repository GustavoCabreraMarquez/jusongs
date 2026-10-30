package com.swave.swave2000

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.swave.swave2000.theme.SWaveTheme
import com.swave.swave2000.ui.MainScreen
import com.swave.swave2000.ui.PermissionScreen
import com.swave.swave2000.ui.PlayerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SWaveTheme {
                PermissionGate(viewModel)
            }
        }
    }
}

private fun requiredPermissions(): Array<String> = buildList {
    add(if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE)
    if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
}.toTypedArray()

private fun allGranted(context: Context) = requiredPermissions().all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun PermissionGate(viewModel: PlayerViewModel) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(allGranted(context)) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        granted = allGranted(context)
    }

    LaunchedEffect(Unit) { if (!granted) launcher.launch(requiredPermissions()) }
    // Re-check when returning from the system settings page
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { granted = allGranted(context) }
    LaunchedEffect(granted) { if (granted) viewModel.startLibrary() }

    if (granted) {
        MainScreen(viewModel)
    } else {
        PermissionScreen(
            onGrant = { launcher.launch(requiredPermissions()) },
            onOpenSettings = {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                )
            }
        )
    }
}
