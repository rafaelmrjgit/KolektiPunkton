package com.rma.kolektipunkton.helper

import android.content.Context
import com.rma.kolektipunkton.R

/* Carrega valores nas seguintes variáveis:
    Constantes.corEixosPontos
    Constantes.tamanhoPontos
    Constantes.nomeUsuario
    Constantes.nomeProjeto
    Constantes.tema_estilo
    Constantes.primeiro_acesso
    Constantes.sensibilidade
    Constantes.buscacor
   */
class CarregarConstantes(private val context: Context) {
    fun executar() {
        val prefs = context.getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0) //modo privado
        Constantes.nomeProjetoPref = prefs
        val bdDAO = BdDAO(context)
        if (prefs.contains(context.getString(R.string.nomeprojetokey))) { //testa por um valor; o ideal seria por todos os bancos de dados e preferências
            Constantes.nomeProjeto =
                prefs.getString(context.getString(R.string.nomeprojetokey), "")
            Constantes.nomeUsuario = bdDAO.obterNomeUsuario(Constantes.nomeProjeto)
        }

        //cor do eixo e pontos
        if (prefs.contains(context.getString(R.string.coreixo))) {
            if (prefs.getAll()
                    .get(context.getString(R.string.coreixo)) is String
            ) {
                //compatibilidade com versão antiga, 1 usuário até 09/05/2021
                val coreixo: String = prefs.getString(
                    context.getString(R.string.coreixo),
                    "pr"
                )!!
                if (coreixo == "pr") {
                    Constantes.corEixosPontos = context.getResources().getColor(R.color.corEixoPPr)
                } else if (coreixo == "br") {
                    Constantes.corEixosPontos = context.getResources().getColor(R.color.corEixoPBr)
                } else if (coreixo == "vm") {
                    Constantes.corEixosPontos = context.getResources().getColor(R.color.corEixoPAm)
                } else if (coreixo == "am") {
                    Constantes.corEixosPontos = context.getResources().getColor(R.color.corEixoPVm)
                } else if (coreixo == "az") {
                    Constantes.corEixosPontos = context.getResources().getColor(R.color.corEixoPAz)
                } else if (coreixo == "vd") {
                    Constantes.corEixosPontos = context.getResources().getColor(R.color.corEixoPVd)
                }
            } else {
                Constantes.corEixosPontos = prefs.getInt(
                    context.getString(R.string.coreixo),
                    context.getResources().getColor(R.color.corEixoPAm)
                )
            }
        }

        //tamanho dos pontos
        if (prefs.contains(context.getString(R.string.tamanhoponto))) {
            if (prefs.getAll()
                    .get(context.getString(R.string.tamanhoponto)) is String
            ) {
                //compatibilidade com versão antiga, 1 usuário até 09/05/2021
                val tamanhoponto: String = prefs.getString(
                    context.getString(R.string.tamanhoponto),
                    "pq"
                )!!
                if (tamanhoponto == "pq") {
                    Constantes.tamanhoPontos = Constantes.tamanhoPequeno
                } else if (tamanhoponto == "md") {
                    Constantes.tamanhoPontos = Constantes.tamanhoMedio
                } else if (tamanhoponto == "gd") {
                    Constantes.tamanhoPontos = Constantes.tamanhoGrande
                }
            } else {
                Constantes.tamanhoPontos = prefs.getInt(
                    context.getString(R.string.tamanhoponto),
                    Constantes.tamanhoPequeno
                )
            }
        }

        // primeiro acesso à câmera
        if (prefs.contains(context.getString(R.string.primeiro_acesso_camera))) {
            Constantes.primeiro_acesso_camera = prefs.getString(
                context.getString(R.string.primeiro_acesso_camera),
                ""
            )
        }

        // modo semiautomático de seleção de pontos
        /*if (prefs.contains("modo_auto")) {
            Constantes.modo_auto = prefs.getString("modo_auto", "");;
        }*/

        // sensibilidade - range de cores
        if (prefs.contains(context.getString(R.string.sensibilidade))) {
            Constantes.sensibilidade = prefs.getInt(
                context.getString(R.string.sensibilidade),
                Constantes.SENSIBILIDADE_ALTA
            )
        }

        // cor padrão para busca de ponto
        if (prefs.contains(context.getString(R.string.buscacor))) {
            Constantes.buscacor = prefs.getInt(
                context.getString(R.string.buscacor),
                context.getResources().getColor(R.color.cor6)
            )
        }

        //tema escuro/claro
        if (prefs.contains(context.getString(R.string.temaestilo))) {
            Constantes.tema_estilo =
                prefs.getString(context.getString(R.string.temaestilo), "dia")
        }

        //salvamento automático
        if (prefs.contains(context.getString(R.string.autosave))) {
            Constantes.autosave =
                prefs.getBoolean(context.getString(R.string.autosave), false)
        }
    }
}
