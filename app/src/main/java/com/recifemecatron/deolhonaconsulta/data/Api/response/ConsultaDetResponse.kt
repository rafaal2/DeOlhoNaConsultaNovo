package com.recifemecatron.deolhonaconsulta.data.Api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsultaDetResponse(
    @SerialName("DATA_DA_SOLICITACAO")
    val dataDaSolicitacao: String? = null,
    @SerialName("NOME_DO_PACIENTE")
    val nomeDoPaciente: String? = null,
    @SerialName("PROCEDIMENTO")
    val procedimento: String? = null,
    @SerialName("UNIDADE_SOLICITANTE")
    val unidadeSolicitante: String? = null,
    @SerialName("UNIDADE_EXECUTANTE")
    val unidadeExecutante: String? = null,
    @SerialName("DATA_DE_ATENDIMENTO")
    val dataDeAtendimento: String? = null,
    @SerialName("OBS")
    val obs: String? = null
)