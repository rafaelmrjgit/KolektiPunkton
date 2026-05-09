package com.rma.kolektipunkton.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.adapter.ListaProjetosAdaptador
import com.rma.kolektipunkton.helper.BdDAO
import com.rma.kolektipunkton.helper.PontoFloat
import com.rma.kolektipunkton.helper.RecyclerItemClickListener
import com.rma.kolektipunkton.helper.ToastPersonalizado
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CompartilharFragment : Fragment() {
    private val listaProjetos = ArrayList<String>()
    private var listaDAO1 = ArrayList<PontoFloat>()
    private lateinit var recyclerCompartilhar: RecyclerView
    private lateinit var adaptadorListaProj: ListaProjetosAdaptador
    private var listaDAO = ArrayList<String>()
    private var nomeProjetoUser: String? = null
    private lateinit var botaoCompartilhar: ImageButton
    private lateinit var nomeArquivoEditText: TextInputEditText
    private var nomeArquivo: String? = null
    private var arquivoGerado: String? = null
    private lateinit var textoProjetoUser: TextView
    private var arquivo: File? = null
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(R.layout.fragment_compartilhar, container, false)
        nomeArquivoEditText = root.findViewById(R.id.nomeArquivoCompartilhar)
        textoProjetoUser = root.findViewById(R.id.textViewCompartilhar)
        botaoCompartilhar = root.findViewById(R.id.imageButtonCompartilhar)
        botaoCompartilhar.setOnClickListener{compartilhar()}
        recyclerCompartilhar = root.findViewById(R.id.recyclerCompartilhar)
        adaptadorListaProj = ListaProjetosAdaptador(listaProjetos)
        recyclerCompartilhar.layoutManager = LinearLayoutManager(activity)
        recyclerCompartilhar.setHasFixedSize(true)
        recyclerCompartilhar.adapter = adaptadorListaProj

        recyclerCompartilhar.addOnItemTouchListener(
            RecyclerItemClickListener(
                activity,
                recyclerCompartilhar, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        /*a seleção de uma linha com o nome do projeto a ser aberto atualiza
                        uma string com o nome de usuário do projeto*/
                        //nomeProjetoUser = listaProjetos.get(position);
                        textoProjetoUser.text = listaProjetos[position]
                    }

                    override fun onLongItemClick(view: View?, position: Int) {}

                    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
                })
        )

        this.carregarListaProjeto()
        return root
    }


    fun carregarListaProjeto() {
        listaProjetos.clear()
        val bdDAO: BdDAO = BdDAO(requireActivity(), getString(R.string.bd_tabela_principal))
        listaDAO = bdDAO.listarTodosProjetos()
        listaProjetos.addAll(listaDAO)
        adaptadorListaProj.notifyDataSetChanged()
    }


    private fun compartilhar() {
        //verificar se o usuário nomeou o arquivo
        nomeArquivo = nomeArquivoEditText.getText().toString()
        if (nomeArquivo.isNullOrEmpty()) {
            ToastPersonalizado(
                requireContext(),
                getString(R.string.compartilhar_nomeArquivo),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        nomeProjetoUser = textoProjetoUser.text.toString()
        //verificar se o usuário selecionou algo na lista
        if (nomeProjetoUser.isNullOrEmpty()) {
            ToastPersonalizado(
                requireContext(),
                getString(R.string.compartilhar_selecionar),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        //verificar se existem dados a serem exportados no item selecionado
        val bdDAO: BdDAO = BdDAO(requireActivity())
        val nomeProjSist = bdDAO.obterNomeSistema(nomeProjetoUser)
        if (nomeProjSist == null) return
        val bdDAO1 = BdDAO(requireActivity(), nomeProjSist)
        listaDAO1 = bdDAO1.listarPontosTabelaReal()
        if (listaDAO1.isEmpty()) {
            //não há dados a serem exportados
            ToastPersonalizado(
                requireContext(),
                getString(R.string.exportar_csv_sem_dados) + ". " + getString(R.string.exportar_csv_sem_dados2) + ".",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        ToastPersonalizado(
            requireContext(),
            getString(R.string.exportar_aguardar),
            Toast.LENGTH_SHORT
        ).show()

        //o arquivo gerado é salvo no armazenamento interno
        Thread(GerarCSV()).start()
    }

    private inner class GerarCSV : Runnable {
        override fun run() {
            try {
                //gera arquivo e o salva na pasta Compartilhamento deste aplicativo
                if (this@CompartilharFragment.isExternalStorageWritable) {
                    arquivoGerado = ""
                    val path = File(
                        requireActivity().getExternalFilesDir(null)
                            .toString() + File.separator + getString(R.string.exportar_nome_pasta) + File.separator + getString(
                            R.string.compartilhar_nome_pasta
                        )
                    )
                    arquivo = File(path, "$nomeArquivo.csv")

                    if (!path.exists()) {
                        if (!path.mkdirs()) {
                            onProgressUpdate(1)
                            return
                        }
                    }

                    val titulo1 = getString(R.string.exportar_titulo1) + "; ; ;" + getString(R.string.exportar_titulo2) + "; \n"
                    val titulo2 = "x;y; ;x;y\n"
                    val titulo3 = getString(R.string.exportar_titulo2) + "; ; ;" + getString(R.string.exportar_titulo1) + "; \n"
                    val titulo4 = "x;y\n"
                    if (listaDAO1[0]?.valorX.toString().contains(".")) {
                        //separador decimal é o ponto
                        var fos: FileOutputStream? = null
                        try {
                            fos = FileOutputStream(arquivo)
                            fos.write(titulo1.toByteArray())
                            fos.write(titulo2.toByteArray())
                            for (i in listaDAO1) {
                                i?.let {
                                    val conteudo = "${it.valorX};${it.valorY}; ;${it.valorX.toString()
                                        .replace('.', ',')};${it.valorY.toString()
                                            .replace('.', ',')}\n"
                                    fos.write(conteudo.toByteArray())
                                }
                            }
                            fos.flush()
                            fos.close()
                        } catch (e: IOException) {
                            onProgressUpdate(2)
                            return
                        } finally {
                            fos?.close()
                        }
                        arquivoGerado = "$nomeArquivo.csv"
                    } else if (listaDAO1[0]?.valorX.toString().contains(",")) {
                        //o separador decimal é a vírgula
                        var fos: FileOutputStream? = null
                        try {
                            fos = FileOutputStream(arquivo)
                            fos.write(titulo3.toByteArray())
                            fos.write(titulo2.toByteArray())
                            for (i in listaDAO1) {
                                i?.let{
                                    val conteudo = "${it.valorX};${it.valorY}; ;${it.valorX.toString()
                                        .replace(',', '.')};${it.valorY.toString()
                                        .replace(',', '.')}\n"
                                    fos.write(conteudo.toByteArray())
                                }
                            }
                            fos.flush()
                            fos.close()
                        } catch (e: IOException) {
                            onProgressUpdate(2)
                            return
                        } finally {
                            fos?.close()
                        }
                        arquivoGerado = "$nomeArquivo.csv"
                    } else {
                        var fos: FileOutputStream? = null
                        try {
                            fos = FileOutputStream(arquivo)
                            fos.write(titulo4.toByteArray())
                            for (i in listaDAO1) {
                                i?.let{
                                    val conteudo = "${it.valorX};${it.valorY}\n"
                                    fos.write(conteudo.toByteArray())
                                }
                            }
                            fos.flush()
                            fos.close()
                        } catch (e: IOException) {
                            onProgressUpdate(2)
                            return
                        } finally {
                            fos?.close()
                        }
                        arquivoGerado = "$nomeArquivo.csv"
                    }
                } else {
                    onProgressUpdate(3)
                }

                //verificar se o armazenamento externo está disponível para leitura e escrita; o gmail, outlook etc vão buscar o arquivo lá
                if (!arquivoGerado.isNullOrEmpty()) {
                    if (this@CompartilharFragment.isExternalStorageWritable) {
                        //abrir intent para compartilhar
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                        }
                        var uri: Uri? = null
                        try {
                            uri = FileProvider.getUriForFile(
                                requireActivity(),
                                getString(R.string.manifest_authorities),
                                arquivo!!)
                        } catch (e: Exception) {
                            onProgressUpdate(4)
                            return
                        }
                        if (uri != null) {
                            intent.putExtra(Intent.EXTRA_STREAM, uri)
                        }
                        try {
                            startActivity(
                                Intent.createChooser(
                                    intent,
                                    getString(R.string.compartilhar_com)
                                )
                            )
                        } catch (e: Exception) {
                            onProgressUpdate(5)
                        }
                    } else {
                        onProgressUpdate(6)
                    }
                }
            } finally {
                //onPostExcecute
                //tarefa na UI Thread ============================
                handler.post {}
            }
        }

        fun onProgressUpdate(valor: Int) {
            handler.post {
                val msg = when (valor) {
                    1 -> getString(R.string.compartilhar_csv_diretorio_falha)
                    2 -> getString(R.string.compartilhar_arquivo_nao_encontrado)
                    3, 6 -> getString(R.string.compartilhar_armazenamento_ext_indisp)
                    else -> getString(R.string.compartilhar_csv_erro)
                }
                val toastLength = when (valor) {
                    1, 2, 5 -> Toast.LENGTH_SHORT
                    else -> Toast.LENGTH_LONG
                }
                ToastPersonalizado(requireContext(), msg, toastLength).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    val isExternalStorageWritable: Boolean get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}



