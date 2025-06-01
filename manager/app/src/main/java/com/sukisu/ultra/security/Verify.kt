package com.sukisu.ultra.security

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

class BiometricHelper(private val context: Context) {

    private val biometricManager = BiometricManager.from(context)

    /**
     * 检查设备生物识别能力
     * @return BiometricCapability 枚举，表示设备的生物识别能力状态
     */
    fun checkBiometricCapability(): BiometricCapability {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricCapability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricCapability.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricCapability.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricCapability.NONE_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                BiometricCapability.SECURITY_UPDATE_REQUIRED
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                BiometricCapability.UNSUPPORTED
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                BiometricCapability.STATUS_UNKNOWN
            else -> BiometricCapability.UNKNOWN_ERROR
        }
    }

    /**
     * 检查强生物识别能力
     */
    fun checkStrongBiometricCapability(): BiometricCapability {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricCapability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricCapability.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricCapability.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricCapability.NONE_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                BiometricCapability.SECURITY_UPDATE_REQUIRED
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                BiometricCapability.UNSUPPORTED
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                BiometricCapability.STATUS_UNKNOWN
            else -> BiometricCapability.UNKNOWN_ERROR
        }
    }

    /**
     * 显示生物识别能力检查结果的Toast提示
     */
    fun showBiometricCapabilityToast() {
        val capability = checkBiometricCapability()
        val message = when (capability) {
            BiometricCapability.AVAILABLE ->
                "✅ 生物识别可用"
            BiometricCapability.NO_HARDWARE ->
                "❌ 设备不支持生物识别硬件"
            BiometricCapability.HARDWARE_UNAVAILABLE ->
                "⚠️ 生物识别硬件当前不可用"
            BiometricCapability.NONE_ENROLLED ->
                "⚠️ 未注册生物识别信息，请先在系统设置中添加"
            BiometricCapability.SECURITY_UPDATE_REQUIRED ->
                "⚠️ 需要安全更新才能使用生物识别"
            BiometricCapability.UNSUPPORTED ->
                "❌ 当前Android版本不支持生物识别"
            BiometricCapability.STATUS_UNKNOWN ->
                "❓ 生物识别状态未知"
            BiometricCapability.UNKNOWN_ERROR ->
                "❌ 检查生物识别时发生未知错误"
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * 检查是否可以使用生物识别
     */
    fun isBiometricAvailable(): Boolean {
        return checkBiometricCapability() == BiometricCapability.AVAILABLE
    }

    /**
     * 检查是否可以使用强生物识别
     */
    fun isStrongBiometricAvailable(): Boolean {
        return checkStrongBiometricCapability() == BiometricCapability.AVAILABLE
    }

    /**
     * 获取生物识别能力的详细描述
     */
    fun getBiometricCapabilityDescription(): String {
        val capability = checkBiometricCapability()
        return when (capability) {
            BiometricCapability.AVAILABLE ->
                "设备支持生物识别，用户已注册生物识别信息，可以正常使用。"
            BiometricCapability.NO_HARDWARE ->
                "设备没有生物识别硬件（如指纹传感器、摄像头等）。"
            BiometricCapability.HARDWARE_UNAVAILABLE ->
                "生物识别硬件存在但当前不可用，可能是暂时性问题。"
            BiometricCapability.NONE_ENROLLED ->
                "设备支持生物识别但用户尚未注册任何生物识别信息。建议引导用户到系统设置中添加指纹或面部识别。"
            BiometricCapability.SECURITY_UPDATE_REQUIRED ->
                "设备需要安全更新才能使用生物识别功能。"
            BiometricCapability.UNSUPPORTED ->
                "当前Android版本或设备配置不支持生物识别API。"
            BiometricCapability.STATUS_UNKNOWN ->
                "无法确定生物识别状态，可能是系统问题。"
            BiometricCapability.UNKNOWN_ERROR ->
                "检查生物识别能力时遇到未知错误。"
        }
    }

    /**
     * 创建生物识别认证提示框
     * @param activity FragmentActivity实例
     * @param title 认证标题
     * @param subtitle 认证副标题
     * @param description 认证描述
     * @param negativeButtonText 取消按钮文本
     * @param callback 认证回调
     */
    fun createBiometricPrompt(
        activity: FragmentActivity,
        title: String = "生物识别认证",
        subtitle: String = "使用您的生物识别信息进行验证",
        description: String = "请将手指放在指纹传感器上或看向摄像头",
        negativeButtonText: String = "取消",
        callback: BiometricAuthCallback
    ): BiometricPrompt {
        val executor: Executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callback.onAuthenticationError(errorCode, errString.toString())
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    callback.onAuthenticationSucceeded()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback.onAuthenticationFailed()
                }
            })

        return biometricPrompt
    }

    /**
     * 创建认证信息
     */
    fun createPromptInfo(
        title: String = "生物识别认证",
        subtitle: String = "使用您的生物识别信息进行验证",
        description: String = "请将手指放在指纹传感器上或看向摄像头",
        negativeButtonText: String = "取消"
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .build()
    }
}

/**
 * 生物识别能力枚举
 */
enum class BiometricCapability {
    AVAILABLE,                    // 可用
    NO_HARDWARE,                  // 无硬件
    HARDWARE_UNAVAILABLE,         // 硬件不可用
    NONE_ENROLLED,               // 未注册
    SECURITY_UPDATE_REQUIRED,     // 需要安全更新
    UNSUPPORTED,                 // 不支持
    STATUS_UNKNOWN,              // 状态未知
    UNKNOWN_ERROR                // 未知错误
}

/**
 * 生物识别认证回调接口
 */
interface BiometricAuthCallback {
    fun onAuthenticationSucceeded()
    fun onAuthenticationError(errorCode: Int, errString: String)
    fun onAuthenticationFailed()
}