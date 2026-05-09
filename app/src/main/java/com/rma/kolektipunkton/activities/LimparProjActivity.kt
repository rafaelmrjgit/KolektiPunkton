package com.rma.kolektipunkton.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.helper.BdDAO
import com.rma.kolektipunkton.helper.Constantes
import com.rma.kolektipunkton.helper.ToastPersonalizado
import java.io.File

class LimparProjActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MaterialAlertDialogBuilder(this, R.style.AlertDialogCustom)
            .setTitle("${getString(R.string.lp_limpar_tudo)} ${Constantes.nomeUsuario}?")
            .setPositiveButton(getString(R.string.sim)) { _, _ ->
                val nomeProjeto = Constantes.nomeProjeto
                if (nomeProjeto != null) {
                    val bdDAO = BdDAO(applicationContext, nomeProjeto)
                    if (!bdDAO.limparTabela()) {
                        ToastPersonalizado(this, getString(R.string.lp_erro_limpar_tudo), Toast.LENGTH_LONG).show()
                    }
                    
                    val path = File(filesDir, "kpfolderoriginal")
                    val arquivoImg = File(path, "$nomeProjeto.jpeg")
                    if (arquivoImg.exists()) {
                        if (!arquivoImg.delete()) {
                            ToastPersonalizado(this, getString(R.string.lp_erro_excluir_img), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.nao)) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }
}
