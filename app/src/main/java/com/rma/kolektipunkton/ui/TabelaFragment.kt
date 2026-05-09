package com.rma.kolektipunkton.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.adapter.TabelaAdaptador
import com.rma.kolektipunkton.helper.BdDAO
import com.rma.kolektipunkton.helper.Constantes
import com.rma.kolektipunkton.helper.PontoFloat
import com.rma.kolektipunkton.helper.ToastPersonalizado
import java.io.File

class TabelaFragment : Fragment() {
    private lateinit var recyclerViewTabela: RecyclerView
    private val lista = ArrayList<PontoFloat>()
    private var listaDAO = ArrayList<PontoFloat>()
    private lateinit var adaptador: TabelaAdaptador
    private lateinit var divider1: View
    private lateinit var divider2: View
    private lateinit var divider3: View
    private lateinit var divider4: View
    private lateinit var divider5: View
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_tabela, container, false)

        divider1 = root.findViewById(R.id.dividerTab1)
        divider2 = root.findViewById(R.id.dividerTab2)
        divider3 = root.findViewById(R.id.dividerTab3)
        divider4 = root.findViewById(R.id.dividerTab4)
        divider5 = root.findViewById(R.id.dividerTab5)
        textView1 = root.findViewById(R.id.textViewTab1)
        textView2 = root.findViewById(R.id.textViewTab2)

        recyclerViewTabela = root.findViewById(R.id.recyclerTabela)
        adaptador = TabelaAdaptador(lista)
        recyclerViewTabela.layoutManager = LinearLayoutManager(requireActivity())
        recyclerViewTabela.setHasFixedSize(true)
        recyclerViewTabela.adapter = adaptador

        return root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.title = Constantes.nomeUsuario

        val path = File(requireActivity().filesDir, "kpfolderoriginal")
        val arquivo = File(path, "${Constantes.nomeProjeto}.jpeg")
        if (!arquivo.exists()) {
            ToastPersonalizado(requireActivity(), getString(R.string.tbl_sem_img_pasta), Toast.LENGTH_SHORT).show()
        }

        Thread(ThreadOnResume()).start()
    }

    private inner class ThreadOnResume : Runnable {
        override fun run() {
            try {
                val bdDAO = BdDAO(requireActivity(), Constantes.nomeProjeto)
                listaDAO = bdDAO.listarPontosTabelaReal()
                
                lista.clear()
                var contagemZeros = 0
                for (p in listaDAO) {
                    lista.add(p)
                    if (p.valorX == 0f && p.valorY == 0f) {
                        contagemZeros++
                    }
                }

                handler.post {
                    if (listaDAO.isEmpty() || (contagemZeros == listaDAO.size && listaDAO.isNotEmpty())) {
                        ToastPersonalizado(requireActivity(), getString(R.string.tabela_valores_nulos), Toast.LENGTH_LONG).show()
                        ocultarViews()
                    } else {
                        mostrarViews()
                    }
                    adaptador.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mostrarViews() {
        divider1.visibility = View.VISIBLE
        divider2.visibility = View.VISIBLE
        divider3.visibility = View.VISIBLE
        divider4.visibility = View.VISIBLE
        divider5.visibility = View.VISIBLE
        textView1.visibility = View.VISIBLE
        textView2.visibility = View.VISIBLE
    }

    private fun ocultarViews() {
        divider1.visibility = View.INVISIBLE
        divider2.visibility = View.INVISIBLE
        divider3.visibility = View.INVISIBLE
        divider4.visibility = View.INVISIBLE
        divider5.visibility = View.INVISIBLE
        textView1.visibility = View.INVISIBLE
        textView2.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
