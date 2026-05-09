package com.rma.kolektipunkton.helper

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permissao {
    fun validarPermissoes(permissoes: Array<String>, activity: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            val listaPermissoes = ArrayList<String>()
            for (permissao in permissoes) {
                val temPermissao = ContextCompat.checkSelfPermission(
                    activity,
                    permissao
                ) == PackageManager.PERMISSION_GRANTED
                if (!temPermissao) listaPermissoes.add(permissao) //adicionando permissão a ser solicitada, que ainda não foi concedida
            }
            if (listaPermissoes.isEmpty()) return
            val novasPermissoes = listaPermissoes.toTypedArray()
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode)
        }
    }
}
