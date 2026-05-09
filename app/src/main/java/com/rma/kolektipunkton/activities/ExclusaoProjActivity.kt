package com.rma.kolektipunkton.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.adapter.ListaExclProjAdaptador
import com.rma.kolektipunkton.helper.BdDAO
import com.rma.kolektipunkton.helper.Constantes
import com.rma.kolektipunkton.helper.RecyclerItemClickListener

class ExclusaoProjActivity : AppCompatActivity() {
    private val listaProjetosExcl = ArrayList<String>()
    private lateinit var recyclerExclProjeto: RecyclerView
    private lateinit var adaptadorExclProj: ListaExclProjAdaptador
    private var listaDAOExcl = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exclusao_proj)

        //getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true) //botão voltar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        recyclerExclProjeto = findViewById<RecyclerView>(R.id.recyclerExclusaoProj)
        adaptadorExclProj = ListaExclProjAdaptador(listaProjetosExcl)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerExclProjeto.setLayoutManager(layoutManager)
        recyclerExclProjeto.setHasFixedSize(true)
        recyclerExclProjeto.setAdapter(adaptadorExclProj)

        recyclerExclProjeto.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                recyclerExclProjeto, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        /*a seleção de uma linha (string) atualiza o arquivo de preferência e a
                         string com o nome do arquivo em ConstantesVarGlobais.nomeProjeto;
                        o projeto a ser aberto pode ser qualquer um existente, exceto o que será excluído*/
                        Constantes.nomeUsuario_Exclusao_Selecionado =
                            listaProjetosExcl.get(position) //nome de usuário do projeto escolhido para exclusão
                        Constantes.nomeProjetoSistema_Excluir_Opcao_Nao =
                            Constantes.nomeProjeto //nome de sistema do projeto atual
                        for (i in listaProjetosExcl.indices) {
                            if (i != position) {
                                val bdDAO = BdDAO(applicationContext)
                                Constantes.nomeProjetoSistema_Excluir_Opcao_Sim =
                                    bdDAO.obterNomeSistema(listaProjetosExcl.get(i)) //nome de sistema do projeto que será aberto após a exclusão do atual
                                break
                            }
                        }
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
        this.carregarListaExclProj()
    }

    fun carregarListaExclProj() {
        listaProjetosExcl.clear()
        val bdDAO = BdDAO(applicationContext, getString(R.string.bd_tabela_principal))
        listaDAOExcl = bdDAO.listarTodosProjetos()
        var i = 0
        while (i < listaDAOExcl.size) {
            listaProjetosExcl.add(listaDAOExcl.get(i))
            i = i + 1
        }
        adaptadorExclProj.notifyDataSetChanged()
    }
}