package com.rma.kolektipunkton.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.adapter.ListaProjetosAdaptador
import com.rma.kolektipunkton.helper.BdDAO
import com.rma.kolektipunkton.helper.Constantes
import com.rma.kolektipunkton.helper.RecyclerItemClickListener

class AbrirProjActivity : AppCompatActivity() {
    private val listaProjetos = ArrayList<String>()
    private lateinit var recyclerSelecaoProjeto: RecyclerView
    private lateinit var adaptadorListaProj: ListaProjetosAdaptador
    private var listaDAO = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abrir_proj)

        //getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true) //botão voltar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        recyclerSelecaoProjeto = findViewById<RecyclerView>(R.id.recyclerListaProjetos)
        adaptadorListaProj = ListaProjetosAdaptador(listaProjetos)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerSelecaoProjeto.setLayoutManager(layoutManager)
        recyclerSelecaoProjeto.setHasFixedSize(true)
        recyclerSelecaoProjeto.setAdapter(adaptadorListaProj)

        recyclerSelecaoProjeto.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                recyclerSelecaoProjeto, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        /*a seleção de uma linha (string) com o nome do projeto a ser aberto atualiza
                        o arquivo de preferência e ConstantesVarGlobais.nomeProjeto*/
                        val bdDAO = BdDAO(applicationContext)
                        val nomeSelecionado = bdDAO.obterNomeSistema(listaProjetos.get(position))
                        Constantes.nomeProjeto = nomeSelecionado
                        val prefs = getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0)
                        Constantes.nomeProjetoPref = prefs
                        val editor: SharedPreferences.Editor = prefs.edit()
                        editor.putString(getString(R.string.nomeprojetokey), nomeSelecionado)
                        editor.apply()
                        val intent = Intent(this@AbrirProjActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                    }

                    override fun onItemClick(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                    }
                })
        )

        this.carregarListaProjeto()
    }


    fun carregarListaProjeto() {
        listaProjetos.clear()
        val bdDAO = BdDAO(applicationContext, getString(R.string.bd_tabela_principal))
        listaDAO = bdDAO.listarTodosProjetos()
        for (i in listaDAO.indices) {
            listaProjetos.add(listaDAO.get(i))
        }
        adaptadorListaProj.notifyDataSetChanged()
    }
}