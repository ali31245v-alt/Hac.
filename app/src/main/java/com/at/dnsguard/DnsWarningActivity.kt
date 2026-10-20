package com.at.dnsguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A no-countdown reminder shown when the accessibility service detects the
 * Private DNS settings screen. System navigation is intentionally left intact.
 */
class DnsWarningActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navy = Color(0xFF0A1728)
            val cyan = Color(0xFF38D9C5)
            MaterialTheme(colorScheme = darkColorScheme(primary = cyan, background = navy, surface = Color(0xFF14263D))) {
                Surface(modifier = Modifier.fillMaxSize(), color = navy) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🛡️", fontSize = 64.sp)
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "تمهّل قبل تغيير DNS",
                            color = cyan,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "أنت في صفحة إعدادات DNS الخاص. تغيير هذا الإعداد قد يعطّل الحماية التي اخترتها في DNS Guard.",
                            color = Color.White,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 27.sp
                        )
                        Spacer(Modifier.height(14.dp))
                        Text(
                            "لا يوجد عدّ تنازلي أو زر رجوع داخل هذه الشاشة. أزرار تنقّل أندرويد تبقى متاحة.",
                            color = Color(0xFFB9C8D9),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
