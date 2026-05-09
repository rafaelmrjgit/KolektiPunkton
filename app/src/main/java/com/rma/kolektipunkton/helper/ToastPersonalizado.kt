package com.rma.kolektipunkton.helper

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.rma.kolektipunkton.R

class ToastPersonalizado(context: Context, mensagem: String?, duracao: Int) : Toast(context) {
    init {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_personalizado, null)
        val text = layout.findViewById<TextView>(R.id.textToast)
        text.text = mensagem
        
        duration = duracao
        @Suppress("DEPRECATION")
        view = layout
    }
}
