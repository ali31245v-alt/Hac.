package com.at.dnsguard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF0A1728)
private val Panel = Color(0xFF14263D)
private val Cyan = Color(0xFF38D9C5)
private val SoftText = Color(0xFFB9C8D9)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Cyan,
                    onPrimary = Navy,
                    background = Navy,
                    surface = Panel,
                    onSurface = Color.White
                )
            ) {
                val selectedProvider = remember { mutableStateOf("security.cloudflare-dns.com") }
                val message = remember { mutableStateOf("اختر مزوّد الحماية ثم افتح إعدادات DNS الخاصة") }
                val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current

                Surface(modifier = Modifier.fillMaxSize(), color = Navy) {
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(18.dp))
                        Text("🛡️", fontSize = 58.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("DNS Guard", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Cyan)
                        Text("حماية DNS من إعدادات أندرويد", fontSize = 15.sp, color = SoftText)
                        Spacer(Modifier.height(24.dp))

                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                                Text("حالة الحماية", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(Modifier.height(8.dp))
                                Text("تُفعَّل الحماية بعد ضبط مزوّد DNS الخاص في إعدادات الهاتف. لا يستطيع التطبيق تغيير هذا الإعداد بصمت.", color = SoftText, fontSize = 14.sp)
                                Spacer(Modifier.height(14.dp))
                                Text("اختر إعداد DNS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(Modifier.height(8.dp))
                                ProviderOption(
                                    title = "حجب البرمجيات الضارة",
                                    subtitle = "Cloudflare Security",
                                    host = "security.cloudflare-dns.com",
                                    selected = selectedProvider.value == "security.cloudflare-dns.com",
                                    onClick = { selectedProvider.value = "security.cloudflare-dns.com" }
                                )
                                Spacer(Modifier.height(8.dp))
                                ProviderOption(
                                    title = "حجب البرمجيات الضارة ومحتوى البالغين",
                                    subtitle = "Cloudflare Family",
                                    host = "family.cloudflare-dns.com",
                                    selected = selectedProvider.value == "family.cloudflare-dns.com",
                                    onClick = { selectedProvider.value = "family.cloudflare-dns.com" }
                                )
                                Spacer(Modifier.height(12.dp))
                                Text("اسم المزوّد الذي ستدخله:", color = SoftText, fontSize = 13.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(selectedProvider.value, color = Cyan, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        clipboard.setText(androidx.compose.ui.text.AnnotatedString(selectedProvider.value))
                                        message.value = "تم نسخ اسم المزوّد. الصقه في خانة اسم مضيف DNS الخاص."
                                    },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) { Text("نسخ اسم المزوّد", fontWeight = FontWeight.Bold) }
                                Spacer(Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        try {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                                startActivity(Intent(Settings.ACTION_SETTINGS))
                                            } else {
                                                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                                            }
                                            message.value = "في الإعدادات اختر DNS الخاص، ثم اسم مضيف مزوّد DNS الخاص، والصق الاسم المنسوخ واحفظ."
                                        } catch (_: Exception) {
                                            startActivity(Intent(Settings.ACTION_SETTINGS))
                                            message.value = "افتح الشبكة والإنترنت ثم DNS الخاص، وأدخل اسم المزوّد."
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) { Text("فتح إعدادات DNS الخاصة", fontWeight = FontWeight.Bold) }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF102F36)), shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                Text("الخطوات المطلوبة", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("١. اضغط «نسخ اسم المزوّد».\n٢. افتح إعدادات DNS الخاصة.\n٣. اختر «اسم مضيف مزوّد DNS الخاص».\n٤. الصق الاسم واحفظ.\n٥. ارجع إلى التطبيق؛ إعداد DNS يُدار من أندرويد.", color = Color.White, fontSize = 14.sp, lineHeight = 23.sp)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(message.value, color = SoftText, fontSize = 13.sp)
                        Spacer(Modifier.height(18.dp))
                        Text("ملاحظة: يلزم اتصال بالإنترنت ودعم DNS الخاص من الهاتف والشبكة. قد لا تعمل بعض المواقع أو الشبكات مع مزوّد معيّن.", color = SoftText, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderOption(
    title: String,
    subtitle: String,
    host: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) Color(0xFF16464B) else Color(0xFF1A3049),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) Cyan else Color(0xFF30465E)
        )
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = Cyan))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = SoftText, fontSize = 12.sp)
                Text(host, color = Cyan, fontSize = 11.sp)
            }
        }
    }
}
