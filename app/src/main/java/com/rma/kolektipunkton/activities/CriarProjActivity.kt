package com.rma.kolektipunkton.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.helper.BdDAO
import com.rma.kolektipunkton.helper.Constantes
import com.rma.kolektipunkton.helper.DbHelper
import com.rma.kolektipunkton.helper.InputBox
import com.rma.kolektipunkton.helper.InputBox.InputBoxListener
import com.rma.kolektipunkton.helper.ToastPersonalizado
import java.io.File

class CriarProjActivity : AppCompatActivity() {
    private var nomeProjetoUsuario: String? = null
    private var nomeProjeto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inputBox = InputBox(
            this@CriarProjActivity,
            getString(R.string.np_novo_projeto_titulo),
            getString(R.string.np_branco),
            getString(R.string.cancelar),
            getString(R.string.np_nome),
            object : InputBoxListener {
                override fun onBotao1(textoDigitado: String?) {
                    //abrir arquivo em branco
                    nomeProjetoUsuario = textoDigitado
                    if (nomeJaExiste(textoDigitado)) {
                        ToastPersonalizado(
                            applicationContext,
                            getString(R.string.np_nome_existente),
                            Toast.LENGTH_SHORT
                        ).show()
                        //Toast.makeText(CriarProjActivity.this, getString(R.string.np_nome_existente), Toast.LENGTH_SHORT).show();
                        nomeProjetoUsuario = ""
                    }
                    if (textoDigitado != null && textoDigitado != "") {
                        if (nomeProjetoUsuario != null && nomeProjetoUsuario != "") {
                            if (criarProjetoBranco(nomeProjetoUsuario)) {
                                salvarPreferencia()
                                recuperarDadosReinicializacao()
                            }
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            //nomeProjetoEmUso = Constantes.nomeProjeto;
                            ToastPersonalizado(
                                applicationContext, getString(R.string.np_input_vazio) + " " +
                                        getString(R.string.np_preencher_tudo), Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(getApplicationContext(), MainActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        //nomeProjetoEmUso = Constantes.nomeProjeto;
                        ToastPersonalizado(
                            applicationContext, getString(R.string.np_input_vazio) + " " +
                                    getString(R.string.np_preencher_tudo), Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(getApplicationContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onBotao2(textoDigitado: String?) {
                    finish()
                }
            })
        inputBox
            .setCancelable(false)
            .show()
    }


    private fun nomeJaExiste(textoDigitado: String?): Boolean {
        val bdDAO = BdDAO(getApplicationContext(), getString(R.string.bd_tabela_principal))
        val todosProjetosUsuario = bdDAO.listarTodosProjetos()
        return todosProjetosUsuario.contains(textoDigitado)
    }

    fun criarProjetoBranco(nomeArquivoBranco: String?): Boolean {
        /* Identificar a primeira linha fora de uso na tabela_principal.
         * Gravar dados nessa linha da tabela_principal.
         * Criar tabela para o novo projeto. */
        val bdDAO = BdDAO(getApplicationContext(), getString(R.string.bd_tabela_principal))
        val linha_Tabela_Principal = bdDAO.numeroLinhas() + 1
        if (!bdDAO.criarTabela(linha_Tabela_Principal, nomeArquivoBranco)) {
            ToastPersonalizado(
                getApplicationContext(),
                getString(R.string.tabela_erro_criar),
                Toast.LENGTH_LONG
            ).show()
            //Toast.makeText(CriarProjActivity.this, getString(R.string.tabela_erro_criar), Toast.LENGTH_LONG).show();
            return false
        } else {
            nomeProjeto = DbHelper.tabela + linha_Tabela_Principal.toString()
        }
        return true
    }

    fun salvarPreferencia() {
        //salvar nas preferências
        val prefs = getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0)
        Constantes.nomeProjetoPref = prefs
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(getString(R.string.nomeprojetokey), nomeProjeto)
        editor.apply()
    }


    private fun recuperarDadosReinicializacao() {
        //Abrir último banco de dados
        //recuperar na string Constantes.nomeProjeto o nome em preferencias; ver onStop
        val prefs = getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0) //modo privado
        Constantes.nomeProjetoPref = prefs
        if (prefs.contains(getString(R.string.nomeprojetokey))) {
            Constantes.nomeProjeto =
                prefs.getString(getString(R.string.nomeprojetokey), "")
            val bdDAO = BdDAO(applicationContext)
            Constantes.nomeUsuario = bdDAO.obterNomeUsuario(Constantes.nomeProjeto)
        } else {
            ToastPersonalizado(
                getApplicationContext(),
                getString(R.string.db_semdados),
                Toast.LENGTH_LONG
            ).show()
            //Toast.makeText(CriarProjActivity.this, getString(R.string.db_semdados), Toast.LENGTH_LONG).show();
        }
    }

    private fun converterDpEmPx(densidade: Float, dp: Float): Float {
        //converterDpEmPx: converter o valor de dp passado para pixel
        return dp * densidade
    }
}

