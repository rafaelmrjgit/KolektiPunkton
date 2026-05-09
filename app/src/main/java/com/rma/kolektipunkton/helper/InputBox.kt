package com.rma.kolektipunkton.helper

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.rma.kolektipunkton.R

class InputBox(
    private val context: Context,
    private val titulo: String?,
    private val textoBotao1: String?,
    private val textoBotao2: String?,
    private val hintCampo1: String?,
    private val listener: InputBoxListener
) : MaterialAlertDialogBuilder(context, R.style.AlertDialogCustom) {

    private lateinit var textoCampoapp: TextInputEditText

    interface InputBoxListener {
        fun onBotao1(texto1: String?)
        fun onBotao2(texto1: String?)
    }

    override fun create(): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.inputbox, null)
        textoCampoapp = view.findViewById(R.id.textInputBoxapp1)
        //val textTitulo = view.findViewById<TextView>(R.id.textinputBoxTituloapp)

        //textTitulo.text = titulo
        //textoCampoapp.hint = hintCampo1

        setView(view)
        setPositiveButton(textoBotao1) { _, _ ->
            listener.onBotao1(textoCampoapp.text.toString())
        }
        setNegativeButton(textoBotao2) { _, _ ->
            listener.onBotao2(textoCampoapp.text.toString())
        }

        return super.create()
    }
}
