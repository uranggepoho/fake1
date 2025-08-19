package com.example.fakegps

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    FakeGPSScreen(
                        onStartService = {
                            startForegroundService(Intent(this, MockLocationService::class.java))
                        },
                        onStopService = {
                            stopService(Intent(this, MockLocationService::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FakeGPSScreen(onStartService: () -> Unit, onStopService: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = remember { CoroutineScope(Dispatchers.Main) }
    val mocker = remember { LocationMocker(context) }

    var lat by remember { mutableStateOf(TextFieldValue("-6.9175")) }
    var lon by remember { mutableStateOf(TextFieldValue("107.6191")) }
    var accuracy by remember { mutableStateOf(TextFieldValue("5")) }
    var isMocking by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Fake GPS – Android 15")
        OutlinedTextField(value = lat, onValueChange = { lat = it }, label = { Text("Latitude") })
        OutlinedTextField(value = lon, onValueChange = { lon = it }, label = { Text("Longitude") })
        OutlinedTextField(value = accuracy, onValueChange = { accuracy = it }, label = { Text("Akurasi (m)") })

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                scope.launch {
                    runCatching {
                        mocker.startMock(
                            lat.text.toDouble(),
                            lon.text.toDouble(),
                            accuracy.text.toFloat()
                        )
                    }.onSuccess {
                        isMocking = true
                    }
                }
            }) { Text("Start Mock") }

            Button(onClick = {
                scope.launch {
                    runCatching { mocker.stopMock() }.onSuccess { isMocking = false }
                }
            }, enabled = isMocking) { Text("Stop Mock") }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onStartService) { Text("Start Service (opsional)") }
            OutlinedButton(onClick = onStopService) { Text("Stop Service") }
        }

        Text(
            text = "Langkah penting: Buka Developer Options → Select mock location app → pilih ‘Fake GPS’.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
