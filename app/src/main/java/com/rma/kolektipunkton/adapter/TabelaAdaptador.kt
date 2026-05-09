package com.rma.kolektipunkton.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.helper.PontoFloat
import java.text.DecimalFormat

class TabelaAdaptador(private val lista: ArrayList<PontoFloat>) :
    RecyclerView.Adapter<TabelaAdaptador.MyViewHolder>() {
    private val df = DecimalFormat("0.00")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLista = LayoutInflater.from(parent.context).inflate(R.layout.adapter_tabela, parent, false)
        return MyViewHolder(itemLista)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pontoFloat = lista[position]
        holder.campo1.text = df.format(pontoFloat.valorX.toDouble())
        holder.campo2.text = df.format(pontoFloat.valorY.toDouble())
    }

    override fun getItemCount(): Int = lista.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val campo1: TextView = itemView.findViewById(R.id.textTab1)
        val campo2: TextView = itemView.findViewById(R.id.textTab2)
    }
}
