package com.unica.bxhbaihatapi.ui.base

import androidx.fragment.app.Fragment
import com.unica.bxhbaihatapi.ui.base.BaseActivity

open class BaseFragment:Fragment(){

    open fun onBackPress(){
        if (activity != null){
            (activity as BaseActivity).backRoot()
        }
    }
}