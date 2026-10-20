package com.at.dnsguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DnsGuardScreen()
            }
        }
    }
}

@Composable
private fun DnsGuardScreen() {
    var protectionEnabled by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("DNS Guard", style = MaterialTheme.typography.headlineLarge)
            Text(
                text = if (protectionEnabled) "الحالة: مفعّل" else "الحالة: غير مفعّل",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                onClick = { protectionEnabled = !protectionEnabled },
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(if (protectionEnabled) "إيقاف الحماية" else "تفعيل الحماية")
            }
            Text(
                "واجهة أولية للتجربة؛ زر التفعيل لا يغيّر إعدادات DNS فعلياً بعد.",
                modifier = Modifier.padding(top = 20.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
