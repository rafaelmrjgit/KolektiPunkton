package com.rma.kolektipunkton.helper

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.graphics.Point
import com.rma.kolektipunkton.R

class BdDAO : IbdDAO {
    private val escrever: SQLiteDatabase
    private val ler: SQLiteDatabase
    private val contexto: Context
    private var nomeTabela: String? = null
    private val dbHelper: DbHelper

    constructor(context: Context, nomeTabelaBD: String?) {
        this.dbHelper = DbHelper(context)
        this.escrever = dbHelper.writableDatabase
        this.ler = dbHelper.readableDatabase
        this.nomeTabela = nomeTabelaBD
        this.contexto = context
    }

    constructor(context: Context) {
        this.dbHelper = DbHelper(context)
        this.escrever = dbHelper.writableDatabase
        this.ler = dbHelper.readableDatabase
        this.contexto = context
    }

    override fun tabelaPrincipalInstalacaoPrimeiroProjeto(): Boolean {
        val cv = ContentValues()
        try {
            cv.put("nomeprojsistema", DbHelper.tabela + "1")
            cv.put("nomeprojuser", contexto.getString(R.string.db_primeiro_projeto))
            cv.put("emuso", contexto.getString(R.string.bd_sim))
            escrever.insert(contexto.getString(R.string.bd_tabela_principal), null, cv)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun existePrimeiroProj(): Boolean {
        val sql = "SELECT nomeprojsistema FROM ${contexto.getString(R.string.bd_tabela_principal)} WHERE nomeprojsistema='${DbHelper.tabela}1';"
        return try {
            ler.rawQuery(sql, null).use { cursor ->
                cursor.count != 0
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun excluirLinhas(nomeProjeto: String?): Boolean {
        return if (nomeProjeto != null) dbHelper.excluirLinhas(escrever, nomeProjeto) else false
    }

    override fun criarTabela(numeroProj: Long, nomeUsuario: String?): Boolean {
        if (!dbHelper.criarTabela(escrever, numeroProj)) {
            return false
        }
        val cv = ContentValues()
        try {
            cv.put("nomeprojsistema", DbHelper.tabela + numeroProj.toString())
            cv.put("nomeprojuser", nomeUsuario)
            cv.put("emuso", contexto.getString(R.string.bd_sim))
            escrever.insert(contexto.getString(R.string.bd_tabela_principal), null, cv)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun numeroLinhas(): Long {
        return DatabaseUtils.queryNumEntries(ler, nomeTabela)
    }

    override fun atualizarTabela(
        pontosCurva: ArrayList<Point>?,
        pontoOrigem: Point?,
        pontoXmax: Point?,
        pontoYmax: Point?,
        pontosCurvaReal: ArrayList<PontoFloat>?,
        voxreal: Float?,
        voyreal: Float?,
        vmxreal: Float?,
        vmyreal: Float?
    ): Boolean {
        val cv1 = ContentValues()
        val primeiraLinha: Long
        try {
            if (pontoOrigem != null && pontoOrigem.x != Int.MAX_VALUE) cv1.put("p_origem_x_sist", pontoOrigem.x) else cv1.putNull("p_origem_x_sist")
            if (pontoOrigem != null && pontoOrigem.y != Int.MAX_VALUE) cv1.put("p_origem_y_sist", pontoOrigem.y) else cv1.putNull("p_origem_y_sist")
            if (pontoXmax != null && pontoXmax.x != Int.MAX_VALUE) cv1.put("p_x_max_sist", pontoXmax.x) else cv1.putNull("p_x_max_sist")
            if (pontoYmax != null && pontoYmax.y != Int.MAX_VALUE) cv1.put("p_y_max_sist", pontoYmax.y) else cv1.putNull("p_y_max_sist")
            
            if (voxreal != null) cv1.put("p_origem_x_real", voxreal) else cv1.putNull("p_origem_x_real")
            if (voyreal != null) cv1.put("p_origem_y_real", voyreal) else cv1.putNull("p_origem_y_real")
            if (vmxreal != null) cv1.put("p_x_max_real", vmxreal) else cv1.putNull("p_x_max_real")
            if (vmyreal != null) cv1.put("p_y_max_real", vmyreal) else cv1.putNull("p_y_max_real")
            
            val tabela = nomeTabela ?: return false
            primeiraLinha = escrever.insert(tabela, null, cv1)
        } catch (e: Exception) {
            return false
        }

        try {
            val curva = pontosCurva ?: return true
            for (i in curva.indices) {
                val cv2 = ContentValues()
                val pSist = curva[i]
                if (pSist.x != Int.MAX_VALUE) cv2.put("ponto_curva_x_sist", pSist.x) else cv2.putNull("ponto_curva_x_sist")
                if (pSist.y != Int.MAX_VALUE) cv2.put("ponto_curva_y_sist", pSist.y) else cv2.putNull("ponto_curva_y_sist")
                
                if (pontosCurvaReal != null && i < pontosCurvaReal.size) {
                    val pReal = pontosCurvaReal[i]
                    if (pReal.valorX != Float.MAX_VALUE) cv2.put("ponto_curva_x_real", pReal.valorX) else cv2.putNull("ponto_curva_x_real")
                    if (pReal.valorY != Float.MAX_VALUE) cv2.put("ponto_curva_y_real", pReal.valorY) else cv2.putNull("ponto_curva_y_real")
                }
                
                if (i == 0) {
                    escrever.update(nomeTabela!!, cv2, "i=?", arrayOf(primeiraLinha.toString()))
                } else {
                    escrever.insert(nomeTabela!!, null, cv2)
                }
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun atualizarTabelaSemParametros(
        pontosCurvaSist: ArrayList<Point>?,
        pontoOrigem: Point?,
        pontoXmax: Point?,
        pontoYmax: Point?
    ): Boolean {
        val cv1 = ContentValues()
        val primeiraLinha: Long
        try {
            if (pontoOrigem != null && pontoOrigem.x != Int.MAX_VALUE) cv1.put("p_origem_x_sist", pontoOrigem.x) else cv1.putNull("p_origem_x_sist")
            if (pontoOrigem != null && pontoOrigem.y != Int.MAX_VALUE) cv1.put("p_origem_y_sist", pontoOrigem.y) else cv1.putNull("p_origem_y_sist")
            if (pontoXmax != null && pontoXmax.x != Int.MAX_VALUE) cv1.put("p_x_max_sist", pontoXmax.x) else cv1.putNull("p_x_max_sist")
            if (pontoYmax != null && pontoYmax.y != Int.MAX_VALUE) cv1.put("p_y_max_sist", pontoYmax.y) else cv1.putNull("p_y_max_sist")
            
            val tabela = nomeTabela ?: return false
            primeiraLinha = escrever.insert(tabela, null, cv1)
        } catch (e: Exception) {
            return false
        }

        try {
            val curva = pontosCurvaSist ?: return true
            for (i in curva.indices) {
                val cv2 = ContentValues()
                val pSist = curva[i]
                if (pSist.x != Int.MAX_VALUE) cv2.put("ponto_curva_x_sist", pSist.x) else cv2.putNull("ponto_curva_x_sist")
                if (pSist.y != Int.MAX_VALUE) cv2.put("ponto_curva_y_sist", pSist.y) else cv2.putNull("ponto_curva_y_sist")
                
                if (i == 0) {
                    escrever.update(nomeTabela!!, cv2, "i=?", arrayOf(primeiraLinha.toString()))
                } else {
                    escrever.insert(nomeTabela!!, null, cv2)
                }
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun obterNomeUsuario(nomeProj: String?): String? {
        val sql = "SELECT nomeprojuser FROM ${contexto.getString(R.string.bd_tabela_principal)} WHERE nomeprojsistema=?;"
        try {
            ler.rawQuery(sql, arrayOf(nomeProj)).use { cursor ->
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow("nomeprojuser"))
                }
            }
        } catch (e: Exception) {}
        return ""
    }

    override fun obterNomeSistema(nomeUsuario: String?): String? {
        val sql = "SELECT nomeprojsistema FROM ${contexto.getString(R.string.bd_tabela_principal)} WHERE nomeprojuser=?;"
        try {
            ler.rawQuery(sql, arrayOf(nomeUsuario)).use { cursor ->
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow("nomeprojsistema"))
                }
            }
        } catch (e: Exception) {}
        return ""
    }

    override fun listarPontosTabelaReal(): ArrayList<PontoFloat> {
        val pontos = ArrayList<PontoFloat>()
        val sql = "SELECT ponto_curva_x_real, ponto_curva_y_real FROM $nomeTabela ORDER BY ponto_curva_x_real ASC;"
        try {
            ler.rawQuery(sql, null).use { c ->
                val indexX = c.getColumnIndexOrThrow("ponto_curva_x_real")
                val indexY = c.getColumnIndexOrThrow("ponto_curva_y_real")
                while (c.moveToNext()) {
                    if (!c.isNull(indexX)) {
                        val ponto = PontoFloat()
                        ponto.valorX = c.getFloat(indexX)
                        ponto.valorY = c.getFloat(indexY)
                        pontos.add(ponto)
                    }
                }
            }
        } catch (e: Exception) {}
        return pontos
    }

    override fun listarPontosTabelaSist(): ArrayList<PontoFloat> {
        val pontos = ArrayList<PontoFloat>()
        val sql = "SELECT ponto_curva_x_sist, ponto_curva_y_sist FROM $nomeTabela;"
        try {
            ler.rawQuery(sql, null).use { c ->
                val indexX = c.getColumnIndexOrThrow("ponto_curva_x_sist")
                val indexY = c.getColumnIndexOrThrow("ponto_curva_y_sist")
                while (c.moveToNext()) {
                    if (!c.isNull(indexX)) {
                        val ponto = PontoFloat()
                        ponto.valorX = c.getFloat(indexX)
                        ponto.valorY = c.getFloat(indexY)
                        pontos.add(ponto)
                    }
                }
            }
        } catch (e: Exception) {}
        return pontos
    }

    override fun existePontoOrigemSist(): Int {
        val sql = "SELECT p_origem_x_sist FROM $nomeTabela;"
        try {
            ler.rawQuery(sql, null).use { c ->
                val indexX = c.getColumnIndexOrThrow("p_origem_x_sist")
                while (c.moveToNext()) {
                    if (!c.isNull(indexX)) return 1
                }
            }
        } catch (e: Exception) {}
        return 0
    }

    override fun existePontoMaxX(): Int {
        val sql = "SELECT p_x_max_sist FROM $nomeTabela;"
        try {
            ler.rawQuery(sql, null).use { c ->
                val indexX = c.getColumnIndexOrThrow("p_x_max_sist")
                while (c.moveToNext()) {
                    if (!c.isNull(indexX)) return 1
                }
            }
        } catch (e: Exception) {}
        return 0
    }

    override fun existePontoMaxY(): Int {
        val sql = "SELECT p_y_max_sist FROM $nomeTabela;"
        try {
            ler.rawQuery(sql, null).use { c ->
                val indexY = c.getColumnIndexOrThrow("p_y_max_sist")
                while (c.moveToNext()) {
                    if (!c.isNull(indexY)) return 1
                }
            }
        } catch (e: Exception) {}
        return 0
    }

    override fun listarTabela(): ArrayList<BDCampos> {
        val tabelaCompleta = ArrayList<BDCampos>()
        val sql = "SELECT * FROM $nomeTabela;"
        try {
            ler.rawQuery(sql, null).use { c ->
                while (c.moveToNext()) {
                    if (!c.isNull(c.getColumnIndexOrThrow("i"))) {
                        val bdCampos = BDCampos()
                        bdCampos.i = c.getLong(c.getColumnIndexOrThrow("i"))
                        bdCampos.ponto_curva_x_sist = c.getFloat(c.getColumnIndexOrThrow("ponto_curva_x_sist"))
                        bdCampos.ponto_curva_y_sist = c.getFloat(c.getColumnIndexOrThrow("ponto_curva_y_sist"))
                        bdCampos.p_origem_x_sist = c.getFloat(c.getColumnIndexOrThrow("p_origem_x_sist"))
                        bdCampos.p_origem_y_sist = c.getFloat(c.getColumnIndexOrThrow("p_origem_y_sist"))
                        bdCampos.p_x_max_sist = c.getFloat(c.getColumnIndexOrThrow("p_x_max_sist"))
                        bdCampos.p_y_max_sist = c.getFloat(c.getColumnIndexOrThrow("p_y_max_sist"))
                        bdCampos.ponto_curva_x_real = c.getFloat(c.getColumnIndexOrThrow("ponto_curva_x_real"))
                        bdCampos.ponto_curva_y_real = c.getFloat(c.getColumnIndexOrThrow("ponto_curva_y_real"))
                        bdCampos.p_origem_x_real = c.getFloat(c.getColumnIndexOrThrow("p_origem_x_real"))
                        bdCampos.p_origem_y_real = c.getFloat(c.getColumnIndexOrThrow("p_origem_y_real"))
                        bdCampos.p_x_max_real = c.getFloat(c.getColumnIndexOrThrow("p_x_max_real"))
                        bdCampos.p_y_max_real = c.getFloat(c.getColumnIndexOrThrow("p_y_max_real"))
                        tabelaCompleta.add(bdCampos)
                    }
                }
            }
        } catch (e: Exception) {}
        return tabelaCompleta
    }

    override fun listarTodosProjetos(): ArrayList<String> {
        val projetos = ArrayList<String>()
        val sql = "SELECT nomeprojuser FROM ${contexto.getString(R.string.bd_tabela_principal)} WHERE emuso='${contexto.getString(R.string.bd_sim)}';"
        try {
            ler.rawQuery(sql, null).use { cursor ->
                val index = cursor.getColumnIndexOrThrow("nomeprojuser")
                while (cursor.moveToNext()) {
                    val nome = cursor.getString(index)
                    if (!nome.isNullOrEmpty()) projetos.add(nome)
                }
            }
        } catch (e: Exception) {}
        return projetos
    }

    override fun listaProjetosForaUso(): ArrayList<String> {
        val projetos = ArrayList<String>()
        val sql = "SELECT nomeprojsistema FROM ${contexto.getString(R.string.bd_tabela_principal)} WHERE emuso='${contexto.getString(R.string.bd_nao)}';"
        try {
            ler.rawQuery(sql, null).use { cursor ->
                val index = cursor.getColumnIndexOrThrow("nomeprojsistema")
                while (cursor.moveToNext()) {
                    if (!cursor.isNull(index)) projetos.add(cursor.getString(index))
                }
            }
        } catch (e: Exception) {}
        return projetos
    }

    override fun excluirProjeto(nomeProjetoExclusao: String?): String? {
        val nomeProjSistema = obterNomeSistema(nomeProjetoExclusao)
        try {
            escrever.delete(contexto.getString(R.string.bd_tabela_principal), "nomeprojsistema=?", arrayOf(nomeProjSistema))
            if (nomeProjSistema != null) dbHelper.excluirTabela(escrever, nomeProjSistema)
        } catch (e: Exception) {}
        return nomeProjSistema
    }

    override fun excluirListaProjetos(listaProjetosForaUso: ArrayList<String>?): Boolean {
        if (listaProjetosForaUso == null) return true
        try {
            for (nome in listaProjetosForaUso) {
                escrever.delete(contexto.getString(R.string.bd_tabela_principal), "nomeprojsistema=?", arrayOf(nome))
                dbHelper.excluirTabela(escrever, nome)
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun limparTabela(): Boolean {
        val tabela = nomeTabela ?: return false
        return dbHelper.excluirLinhas(escrever, tabela)
    }
}
