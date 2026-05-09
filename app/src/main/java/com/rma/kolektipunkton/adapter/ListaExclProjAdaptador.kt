package com.rma.kolektipunkton.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rma.kolektipunkton.R

class ListaExclProjAdaptador(private val listaProjetos: ArrayList<String>) :
    RecyclerView.Adapter<ListaExclProjAdaptador.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLista = LayoutInflater.from(parent.context).inflate(R.layout.adapter_lista_projetos, parent, false)
        return MyViewHolder(itemLista)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val nomeProjeto = listaProjetos[position]
        holder.campo1.text = nomeProjeto
    }

    override fun getItemCount(): Int = listaProjetos.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val campo1: TextView = itemView.findViewById(R.id.textCampo1)
    }
}
