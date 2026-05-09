package com.rma.kolektipunkton.helper

import android.content.SharedPreferences

object Constantes {
    var nomeProjeto: String? = null //nome de sistema do projeto em uso
    var nomeUsuario: String? = null //nome que o usuário colocou no projeto
    const val ARQUIVO_PREFERENCIA: String = "ArquivoPreferencia"
    var nomeProjetoPref: SharedPreferences? = null
    var nomeProjetoSistema_Excluir_Opcao_Sim: String? = null
    var nomeProjetoSistema_Excluir_Opcao_Nao: String? = null
    var nomeUsuario_Exclusao_Selecionado: String? = null
    var corEixosPontos: Int = 0
    var tamanhoPontos: Int = 0
    const val tamanhoGrande: Int = 20
    const val tamanhoMedio: Int = 10
    const val tamanhoPequeno: Int = 5
    var tema_estilo: String? = null
    var autosave: Boolean = false
    var primeiro_acesso_camera: String? = null
    var sensibilidade: Int = 0

    /* Uma sensibilidade de 20 leva mais de 20 min e consome 8% de bateria -> eliminada.
    Uma sensibilidade de 10 leva 3 min e gasta 2% de bateria.
    Uma sensibilidade de 5 leva de 35 a 40 seg e consome menos de 1% da bateria. */
    const val SENSIBILIDADE_BAIXA: Int = 10 //range de cores maior
    const val SENSIBILIDADE_ALTA: Int = 5 //range de cores menor
    var buscacor: Int = 0
    const val NOTIFICACAO_ID1: Int = 1
    const val NOTIFICACAO_ID2: Int = 2
    const val INTERVALO_AUTOSAVE: Long = 60000
}
