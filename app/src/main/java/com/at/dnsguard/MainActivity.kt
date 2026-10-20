package com.at.dnsguard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FB)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🛡️", fontSize = 54.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("DNS Guard", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF173B70))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "اختصارات آمنة لإعدادات DNS على جهازك",
                        fontSize = 16.sp, textAlign = TextAlign.Center, color = Color(0xFF4B5563)
                    )
                    Spacer(Modifier.height(24.dp))
                    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(18.dp)) {
                            Text("إدارة DNS الخاص", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "افتح إعدادات النظام لتغيير DNS يدويًا. التطبيق لا يغيّر الإعدادات دون موافقتك ولا يمنعك من مغادرتها أو حذف التطبيق.",
                                color = Color(0xFF4B5563), fontSize = 14.sp
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    try {
                                        startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                                    } catch (_: Exception) {
                                        startActivity(Intent(Settings.ACTION_SETTINGS))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("فتح إعدادات الشبكة") }
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.parse("package:$packageName")
                                    })
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("معلومات التطبيق") }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(
                        "ملاحظة: تختلف أماكن إعدادات DNS حسب الشركة وإصدار أندرويد.",
                        fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
