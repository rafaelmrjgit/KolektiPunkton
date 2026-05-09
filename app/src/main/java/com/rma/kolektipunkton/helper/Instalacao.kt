package com.rma.kolektipunkton.helper

import android.content.Context
import android.content.SharedPreferences
import com.rma.kolektipunkton.R

class Instalacao(private val contexto: Context) {
    val toasts: ArrayList<String?> = ArrayList<String?>()
    private var preferenciasJaInstaladas = false

    fun instalarBDePref() {
        //instalação do bando de dados e das preferências
        val prefs = this.contexto.getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0)
        Constantes.nomeProjetoPref = prefs
        val editor: SharedPreferences.Editor = prefs.edit()
        toasts.clear()
        preferenciasJaInstaladas = true

        //nomeprojetokey implantado na versão 1.0
        if (!prefs.contains(contexto.getString(R.string.nomeprojetokey))) {
            //criar item nas preferências e salvar o nome genérico
            editor.putString(
                contexto.getString(R.string.nomeprojetokey),
                DbHelper.Companion.tabela + 1.toString()
            )
            preferenciasJaInstaladas = false
        }

        //criar item para permisssão; assim, duas caixas de diálogo não serão mostradas ao mesmo tempo no primeiro acesso
        //primeiro_acesso_camera implantado na versão 1.1
        if (!prefs.contains(contexto.getString(R.string.primeiro_acesso_camera))) {
            editor.putString(
                contexto.getString(R.string.primeiro_acesso_camera),
                contexto.getString(R.string.opcao_sim)
            )
            preferenciasJaInstaladas = false
        }

        //cor do eixo e pontos
        //coreixo implantado na versão 1.0
        if (!prefs.contains(contexto.getString(R.string.coreixo))) {
            editor.putInt(
                contexto.getString(R.string.coreixo),
                contexto.getResources().getColor(R.color.corEixoPAm)
            )
            preferenciasJaInstaladas = false
        }

        //tamanho dos pontos
        //tamanhoponto implantado na versão 1.0
        if (!prefs.contains(contexto.getString(R.string.tamanhoponto))) {
            editor.putInt(contexto.getString(R.string.tamanhoponto), Constantes.tamanhoPequeno)
            preferenciasJaInstaladas = false
        }

        //sensibilidade
        //sensibilidade implantado na versão 1.1
        if (!prefs.contains(contexto.getString(R.string.sensibilidade))) {
            editor.putInt(contexto.getString(R.string.sensibilidade), Constantes.SENSIBILIDADE_ALTA)
            preferenciasJaInstaladas = false
        }

        //cor padrão para busca de ponto
        //buscacor implantado na versão 1.1
        if (!prefs.contains(contexto.getString(R.string.buscacor))) {
            editor.putInt(
                contexto.getString(R.string.buscacor),
                contexto.getResources().getColor(R.color.cor6)
            )
            preferenciasJaInstaladas = false
        }

        //tema escuro/claro
        //temaestilo implantado na versão 1.0
        if (!prefs.contains(contexto.getString(R.string.temaestilo))) {
            editor.putString(
                contexto.getString(R.string.temaestilo),
                contexto.getString(R.string.tema_sistema)
            )
            preferenciasJaInstaladas = false
        }

        //salvamento automático
        //implantado na versão 1.1
        if (!prefs.contains(contexto.getString(R.string.autosave))) {
            editor.putBoolean(contexto.getString(R.string.autosave), false)
            preferenciasJaInstaladas = false
        }

        if (!preferenciasJaInstaladas) {
            if (!editor.commit()) {
                toasts.add(contexto.getString(R.string.db_erro_preferencias))
            }
        }

        //Preencher tabela principal em relação ao projeto1.
        // Somente para dispositivos que instalam ou reinstalam o app ou que tiveram falha no banco de dados.
        val bdDAO = BdDAO(this.contexto, this.contexto.getString(R.string.bd_tabela_principal))
        if (!bdDAO.existePrimeiroProj()) {
            if (!bdDAO.tabelaPrincipalInstalacaoPrimeiroProjeto()) {
                toasts.add(contexto.getString(R.string.db_erro_tabela_principal))
            }
        } else {
            //"Desinstalação": deletar linhas da tabela_principal e tabelas correspondentes (para usuários que já tinham instalado o app até 10/05/2021)
            val listaProjetosForaUso = bdDAO.listaProjetosForaUso()
            if (!listaProjetosForaUso.isEmpty()) {
                if (!bdDAO.excluirListaProjetos(listaProjetosForaUso)) {
                    toasts.add(contexto.getString(R.string.db_desinstalacao))
                }
            }
        }
    }
}

