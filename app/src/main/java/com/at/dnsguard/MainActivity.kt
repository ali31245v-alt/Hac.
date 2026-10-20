package com.at.dnsguard

import android.accessibilityservice.AccessibilityService
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AdminReceiver : DeviceAdminReceiver() {
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return "إلغاء هذه الصلاحية يعطّل الحماية ويسمح بحذف التطبيق."
    }
}

class DnsLockService : AccessibilityService() {
    private val forbiddenKeywords = listOf(
        "private dns", "dns الخاص", "dns", "encrypted dns",
        "أسماء مضيفين", "provider hostname", "مزيد من خيارات الاتصال",
        "more connection settings", "connection settings",
        "device administrators", "مسؤولو الجهاز", "إدارة الجهاز",
        "إلغاء التثبيت", "uninstall", "تطبيقات مشرف الجهاز"
    )

    private val settingsPackages = listOf(
        "com.android.settings", "com.miui.securitycenter",
        "com.samsung.android.settings", "com.google.android.settings"
    )

    private var lastHomeActionTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val pkgName = event.packageName?.toString()?.lowercase() ?: return
        val isSettingsApp = settingsPackages.any { pkgName.contains(it) } || pkgName.contains("settings")
        if (!isSettingsApp) return

        val now = System.currentTimeMillis()
        if (now - lastHomeActionTime < 1000) return

        val eventText = event.text.joinToString(" ").lowercase()
        val className = event.className?.toString()?.lowercase() ?: ""
        val rootNode = rootInActiveWindow
        val screenContent = StringBuilder()
        collectText(rootNode, screenContent)
        val fullContent = screenContent.toString().lowercase()

        val isHit = forbiddenKeywords.any { kw ->
            eventText.contains(kw) || className.contains(kw) || fullContent.contains(kw)
        }

        if (isHit) {
            lastHomeActionTime = now
            performGlobalAction(GLOBAL_ACTION_HOME)
        }
    }

    private fun collectText(node: AccessibilityNodeInfo?, sb: StringBuilder) {
        if (node == null) return
        node.text?.let { sb.append(it).append(" ") }
        node.contentDescription?.let { sb.append(it).append(" ") }
        for (i in 0 until node.childCount) collectText(node.getChild(i), sb)
    }

    override fun onInterrupt() {}
}

class MainActivity : ComponentActivity() {
    private val isA11yEnabled = mutableStateOf(false)
    private val isAdminEnabled = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { MainScreen() } }
    }

    override fun onResume() {
        super.onResume()
        checkStatus()
    }

    private fun checkStatus() {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""
        isA11yEnabled.value = enabledServices.contains("$packageName/")

        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(this, AdminReceiver::class.java)
        isAdminEnabled.value = dpm.isAdminActive(adminComponent)
    }

    private fun openAccessibilitySettings() {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    private fun requestDeviceAdmin() {
        val adminComponent = ComponentName(this, AdminReceiver::class.java)
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "تفعيل الحماية للمساعدة في منع تعديل الإعدادات")
        }
        startActivity(intent)
    }

    private fun openAppDetails() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }

    @Composable
    private fun MainScreen() {
        val a11yOn by remember { isA11yEnabled }
        val adminOn by remember { isAdminEnabled }
        val isProtected = a11yOn && adminOn

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isProtected) Color(0xFF1E88E5) else Color(0xFFD32F2F)
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = if (isProtected) "🛡️" else "⚠️", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isProtected) "الحماية مفعّلة" else "الحماية غير مكتملة",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isProtected)
                            "تم تفعيل خدمتي الوصول ومسؤول الجهاز. قد تختلف فعالية القفل حسب إصدار النظام."
                        else
                            "يرجى تفعيل الصلاحيات أدناه. هذه الصلاحيات لا تضمن منع حذف التطبيق على كل الأجهزة.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "خطوات التفعيل المطلوبة",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(12.dp))

            StatusCard(
                title = "1. قفل إعدادات الـ DNS",
                description = "تحاول خدمة الوصول الخروج من صفحات الإعدادات التي تطابق الكلمات المحددة.",
                isDone = a11yOn,
                buttonText = if (a11yOn) "مفعّل ✓" else "تفعيل خدمة الوصول",
                onClick = { openAccessibilitySettings() }
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatusCard(
                title = "2. صلاحية مسؤول الجهاز",
                description = "تضيف صلاحية مسؤول الجهاز؛ هذه الصلاحية وحدها لا تمنع إلغاء التثبيت بشكل مضمون.",
                isDone = adminOn,
                buttonText = if (adminOn) "مفعّل ✓" else "تفعيل مسؤول الجهاز",
                onClick = { requestDeviceAdmin() }
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (!a11yOn) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFFFF8E1))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "💡 هل خيار خدمة الوصول رمادي ولا يعمل؟",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFF57F17)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "افتح معلومات التطبيق، ثم من القائمة (⋮) اختر «السماح بالإعدادات المقيدة» إن ظهر هذا الخيار.",
                            fontSize = 12.sp,
                            color = Color(0xFF5D4037)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { openAppDetails() }) {
                            Text("فتح معلومات التطبيق ←", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun StatusCard(
        title: String,
        description: String,
        isDone: Boolean,
        buttonText: String,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isDone) "🟢" else "🔴", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = description, fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isDone,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDone) Color(0xFF4CAF50) else Color(0xFF1976D2)
                    )
                ) {
                    Text(text = buttonText, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
