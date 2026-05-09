package com.rma.kolektipunkton.helper

import android.graphics.Point

interface IbdDAO {
    fun criarTabela(numeroProj: Long, nomeUsuario: String?): Boolean
    fun numeroLinhas(): Long
    fun excluirLinhas(nomeProjeto: String?): Boolean
    fun atualizarTabela(
        pontosCurva: ArrayList<Point>?,
        pontoOrigem: Point?,
        pontoXmax: Point?,
        pontoYmax: Point?,
        pontosCurvaReal: ArrayList<PontoFloat>?,
        voxreal: Float?,
        voyreal: Float?,
        vmxreal: Float?,
        vmyreal: Float?
    ): Boolean

    fun tabelaPrincipalInstalacaoPrimeiroProjeto(): Boolean
    fun obterNomeUsuario(nomeProj: String?): String?
    fun listarPontosTabelaSist(): ArrayList<PontoFloat>?
    fun listarTabela(): ArrayList<BDCampos>?
    fun listarTodosProjetos(): ArrayList<String>?

    //String primeiroProjetoForaUso(String nomeusuario);
    fun obterNomeSistema(nomeUsuario: String?): String?
    fun excluirProjeto(nomeProjetoExclusao: String?): String?
    fun limparTabela(): Boolean
    fun atualizarTabelaSemParametros(
        pontosCurvaSist: ArrayList<Point>?,
        pontoOrigem: Point?,
        pontoXmax: Point?,
        pontoYmax: Point?
    ): Boolean

    fun existePontoOrigemSist(): Int
    fun existePontoMaxX(): Int
    fun existePontoMaxY(): Int
    fun listarPontosTabelaReal(): ArrayList<PontoFloat>?
    fun existePrimeiroProj(): Boolean
    fun listaProjetosForaUso(): ArrayList<String>?
    fun excluirListaProjetos(listaProjetosForaUso: ArrayList<String>?): Boolean
}
