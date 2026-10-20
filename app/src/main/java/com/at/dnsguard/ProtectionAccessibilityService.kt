package com.at.dnsguard

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/** Diagnostic-only service; does not interfere with system settings or uninstall flows. */
class ProtectionAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "SERVICE_CONNECTED")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        Log.d(TAG, "type=${event.eventType}, package=${event.packageName}, class=${event.className}, text=${event.text}")
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            val root = rootInActiveWindow
            try {
                Log.d(TAG, "activeRoot=${root?.className}, rootPackage=${root?.packageName}")
            } finally {
                root?.recycle()
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "SERVICE_INTERRUPTED")
    }

    companion object { private const val TAG = "ProtectionService" }
}
