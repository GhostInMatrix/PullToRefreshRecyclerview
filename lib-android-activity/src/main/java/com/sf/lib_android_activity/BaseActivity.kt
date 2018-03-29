package com.sf.lib_android_activity

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


/**
 * Created by ghostinmatrix on 2018/3/2.
 */
abstract class BaseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = window.decorView as ViewGroup
        group.removeAllViews()
        LayoutInflater.from(this).inflate(getContentLayoutId(), group, true)
        StatusBarHelper.translucent(this)
        StatusBarHelper.setStatusBarLightMode(this)
        initView()
    }
    
    abstract fun getContentLayoutId(): Int
    abstract fun initView()
}