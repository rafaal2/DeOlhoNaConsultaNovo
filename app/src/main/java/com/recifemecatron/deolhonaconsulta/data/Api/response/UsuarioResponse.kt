package com.recifemecatron.deolhonaconsulta.data.Api.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class UsuarioResponse (

    @SerialName("id") var id : String? = null,
    @SerialName("nome") var nome : String? = null,
    @SerialName("telefone") var telefone : String? = null,
    @SerialName("email") var email : String? = null,
    @SerialName("id_bairro") var idBairro : String? = null,
    @SerialName("interesses") var interesses : String? = null,
    @SerialName("idade") var idade : String? = null,
    @SerialName("token") var token : String? = null

)