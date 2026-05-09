package com.rma.kolektipunkton.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.helper.BdDAO
import com.rma.kolektipunkton.helper.Constantes
import com.rma.kolektipunkton.helper.ToastPersonalizado
import java.io.File

class ConfirmaExclusaoProjetoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onResume() {
        super.onResume()

        //abrir alert dialog para confirmar exclusão
        val alert = MaterialAlertDialogBuilder(
            this@ConfirmaExclusaoProjetoActivity,
            R.style.AlertDialogCustom
        )
        alert.setTitle(getString(R.string.excp_exclusaoproj_titulo) + " " + Constantes.nomeUsuario_Exclusao_Selecionado + "?")
        alert.setPositiveButton(getString(R.string.sim), object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val bdDAO = BdDAO(applicationContext)
                val nomeProjSistema =
                    bdDAO.excluirProjeto(Constantes.nomeUsuario_Exclusao_Selecionado)
                if (nomeProjSistema != null && nomeProjSistema != "") {
                    val prefs = getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0)
                    Constantes.nomeProjetoPref = prefs
                    val editor: SharedPreferences.Editor = prefs.edit()
                    editor.putString(
                        getString(R.string.nomeprojetokey),
                        Constantes.nomeProjetoSistema_Excluir_Opcao_Sim
                    ) //nome de sistema do projeto que será aberto
                    editor.apply()
                    ToastPersonalizado(
                        this@ConfirmaExclusaoProjetoActivity,
                        getString(R.string.excp_projeto_excluido) + " (" + Constantes.nomeUsuario_Exclusao_Selecionado + ")",
                        Toast.LENGTH_SHORT
                    ).show()

                    //Toast.makeText(ConfirmaExclusaoProjetoActivity.this, getString(R.string.excp_projeto_excluido) + " (" + Constantes.nomeUsuario_Exclusao_Selecionado + ")", Toast.LENGTH_SHORT).show();

                    //excluir arquivo jpg
                    val path = File(getFilesDir(), "kpfolderoriginal")
                    val arquivoImg = File(path, nomeProjSistema + ".jpeg")
                    if (arquivoImg.exists()) {
                        if (!arquivoImg.delete()) {
                            ToastPersonalizado(this@ConfirmaExclusaoProjetoActivity,
                                getString(R.string.lp_erro_excluir_img),
                                Toast.LENGTH_LONG
                            ).show()
                            //Toast.makeText(ConfirmaExclusaoProjetoActivity.this, getString(R.string.lp_erro_excluir_img), Toast.LENGTH_LONG).show();
                        }
                    }

                    Constantes.nomeProjeto = Constantes.nomeProjetoSistema_Excluir_Opcao_Sim
                    Constantes.nomeProjetoSistema_Excluir_Opcao_Sim = ""
                    Constantes.nomeProjetoSistema_Excluir_Opcao_Nao = ""
                    Constantes.nomeUsuario_Exclusao_Selecionado = ""

                    val intent = Intent(this@ConfirmaExclusaoProjetoActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    ToastPersonalizado(
                        this@ConfirmaExclusaoProjetoActivity,
                        getString(R.string.excp_exclusao_erro),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        alert.setNegativeButton(getString(R.string.nao), object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val prefs = getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0)
                Constantes.nomeProjetoPref = prefs
                val editor: SharedPreferences.Editor = prefs.edit()
                editor.putString(
                    getString(R.string.nomeprojetokey),
                    Constantes.nomeProjetoSistema_Excluir_Opcao_Nao
                )
                editor.apply()
                Constantes.nomeProjeto = Constantes.nomeProjetoSistema_Excluir_Opcao_Nao
                Constantes.nomeProjetoSistema_Excluir_Opcao_Sim = ""
                Constantes.nomeProjetoSistema_Excluir_Opcao_Nao = ""
                Constantes.nomeUsuario_Exclusao_Selecionado = ""
                finish()
            }
        })
        alert.setCancelable(false)
        alert.create()
        alert.show()
    }
}
