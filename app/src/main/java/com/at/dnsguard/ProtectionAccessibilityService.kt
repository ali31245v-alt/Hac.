package com.at.dnsguard

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.Locale

/**
 * Watches visible Settings text for the Private DNS screen and shows one reminder
 * per Settings visit. Android system navigation remains available; this is not
 * an enforcement mechanism and cannot guarantee that DNS changes are blocked.
 */
class ProtectionAccessibilityService : AccessibilityService() {
    private var warningShownThisVisit = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "SERVICE_CONNECTED")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val packageName = event.packageName?.toString().orEmpty()
        if (packageName.isEmpty()) return

        // Keep the visit flag while our own warning Activity is on screen.
        if (packageName != SETTINGS_PACKAGE && packageName != applicationContext.packageName) {
            warningShownThisVisit = false
        }

        if (packageName != SETTINGS_PACKAGE || warningShownThisVisit) return

        val text = buildString {
            append(event.text.joinToString(" "))
            append(' ')
            append(event.contentDescription ?: "")
            append(' ')
            append(readVisibleText(rootInActiveWindow))
        }.lowercase(Locale.ROOT)

        if (DNS_TERMS.any { text.contains(it) }) {
            warningShownThisVisit = true
            Log.i(TAG, "Private DNS screen detected; showing reminder")
            try {
                startActivity(Intent(this, DnsWarningActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                })
            } catch (e: Exception) {
                Log.w(TAG, "Could not show DNS reminder", e)
            }
        }
    }

    private fun readVisibleText(node: AccessibilityNodeInfo?, depth: Int = 0): String {
        if (node == null || depth > 12) return ""
        val result = StringBuilder()
        try {
            node.text?.let { result.append(it).append(' ') }
            node.contentDescription?.let { result.append(it).append(' ') }
            for (i in 0 until node.childCount.coerceAtMost(80)) {
                result.append(readVisibleText(node.getChild(i), depth + 1))
            }
        } catch (_: Exception) {
            // Settings can refresh the accessibility tree while it is being read.
        }
        return result.toString()
    }

    override fun onInterrupt() {
        Log.w(TAG, "SERVICE_INTERRUPTED")
    }

    companion object {
        private const val TAG = "ProtectionService"
        private const val SETTINGS_PACKAGE = "com.android.settings"
        private val DNS_TERMS = listOf(
            "private dns",
            "private dns provider hostname",
            "dns الخاص",
            "اسم مضيف مزوّد dns الخاص",
            "اسم مضيف مزود dns الخاص"
        )
    }
}
