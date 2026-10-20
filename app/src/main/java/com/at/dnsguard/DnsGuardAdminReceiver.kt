package com.at.dnsguard

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Registers DNS Guard in Android's device-admin list.
 * This legacy admin receiver does not prevent uninstall or lock Private DNS settings.
 */
class DnsGuardAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        Toast.makeText(context, "تم تفعيل مسؤول الجهاز لـ DNS Guard", Toast.LENGTH_SHORT).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Toast.makeText(context, "تم إيقاف مسؤول الجهاز لـ DNS Guard", Toast.LENGTH_SHORT).show()
    }
}
