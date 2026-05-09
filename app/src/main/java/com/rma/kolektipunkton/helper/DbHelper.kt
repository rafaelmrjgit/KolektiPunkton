package com.rma.kolektipunkton.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.rma.kolektipunkton.R

/*
========== Banco de dados ======================
Se alterar a estrutura do banco de dados, aumentar VERSION em DbHelper e configurar onUpgrade.
*/

class DbHelper(private val contexto: Context?) : SQLiteOpenHelper(contexto, DB, null, VERSION) {
    /** Este método só é executado quando o banco de dados não existe na aplicação, ou seja,
     * é executado na primeira vez que um objeto DbHelper é criado e getReadableDatabase() ou
     * getWritableDatabase() são chamados.  */
    override fun onCreate(db: SQLiteDatabase) {
        try {
            //Cria 1 tabela com os nomes "de sistema" e nomes "de usuário" dos projetos.
            val sqlprincipal =
                "CREATE TABLE IF NOT EXISTS " + contexto!!.getString(R.string.bd_tabela_principal) +
                        "(i INTEGER PRIMARY KEY," + " nomeprojsistema TEXT," +
                        " nomeprojuser TEXT, emuso TEXT)"
            db.execSQL(sqlprincipal)

            //Cria 1 tabela do projeto1. Essa tabela serve apenas para iniciar o app, após a instalação, já com um projeto.
            val sql = "CREATE TABLE IF NOT EXISTS " + tabela + 1.toString() +
                    "(i INTEGER PRIMARY KEY," +
                    " ponto_curva_x_sist DOUBLE, ponto_curva_y_sist DOUBLE, p_origem_x_sist DOUBLE, p_origem_y_sist DOUBLE, p_x_max_sist DOUBLE, p_y_max_sist DOUBLE, " +
                    "ponto_curva_x_real DOUBLE, ponto_curva_y_real DOUBLE, p_origem_x_real DOUBLE, p_origem_y_real DOUBLE, p_x_max_real DOUBLE, p_y_max_real DOUBLE)"
            db.execSQL(sql)
        } catch (e: Exception) {
            //Toast.makeText(contexto, contexto.getString(R.string.db_erro_criar), Toast.LENGTH_LONG).show();
        }
    }

    /** Esse método é executado quando se altera VERSION.  */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //alterar conforme necessidade

        //podem existir usuários em todas as versões do aplicativo
        /*String updateVersao2 = "ALTER TABLE " + tabela + " ADD COLUMN email TEXT";
        String updateVersao3 = "ALTER TABLE " + tabela + " ADD COLUMN senha TEXT";
        String updateVersao4 = "ALTER TABLE " + tabela + " ADD COLUMN codigo TEXT";
        try {
            if (oldVersion == 1) {
                db.execSQL(updateVersao2);
                db.execSQL(updateVersao3);
                db.execSQL(updateVersao4);
            }else if (oldVersion == 2) {
                db.execSQL(updateVersao3);
                db.execSQL(updateVersao4);
            }else {
                db.execSQL(updateVersao4);
            }
        }catch (Exception e) {
            //mensagem se der erro
        }*/
    }


    fun criarTabela(db: SQLiteDatabase, numeroProjeto: Long): Boolean {
        try {
            val sql = "CREATE TABLE IF NOT EXISTS " + tabela + numeroProjeto.toString() +
                    "(i INTEGER PRIMARY KEY," +
                    " ponto_curva_x_sist DOUBLE, ponto_curva_y_sist DOUBLE, p_origem_x_sist DOUBLE, p_origem_y_sist DOUBLE, p_x_max_sist DOUBLE, p_y_max_sist DOUBLE, " +
                    "ponto_curva_x_real DOUBLE, ponto_curva_y_real DOUBLE, p_origem_x_real DOUBLE, p_origem_y_real DOUBLE, p_x_max_real DOUBLE, p_y_max_real DOUBLE)"
            db.execSQL(sql)
        } catch (e: Exception) {
            return false
        }
        return true
    }


    fun excluirTabela(db: SQLiteDatabase, nomeProjeto: String) {
        try {
            val sql = "DROP TABLE IF EXISTS " + nomeProjeto
            db.execSQL(sql)
        } catch (e: Exception) {
        }
    }


    fun excluirLinhas(db: SQLiteDatabase, nomeProjeto: String): Boolean {
        //exclui todas as linhas, mas mantém a tabela
        try {
            val sql = "DELETE FROM " + nomeProjeto
            db.execSQL(sql)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    companion object {
        /** Quando o banco de dados tiver a estrutura atualizada, trocar a versão abaixo.
         * A cada nova versão do banco de dados, o método onUpgrade é executado.  */
        var VERSION: Int = 1 //versão do banco de dados
        const val DB: String = "DB" //somente 1 banco de dados no app
        var tabela: String = "projeto" //várias tabelas possíveis no banco de dados
    }
}
