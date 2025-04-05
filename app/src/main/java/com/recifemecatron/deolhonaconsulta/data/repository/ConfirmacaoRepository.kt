package com.recifemecatron.deolhonaconsulta.data.repository

import com.recifemecatron.deolhonaconsulta.data.Api.response.ConfirmacaoRequest
import com.recifemecatron.deolhonaconsulta.data.Api.response.ConfirmacaoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class ConfirmacaoRepository(private val httpClient: HttpClient) {
    suspend fun createConfirmacao(request: ConfirmacaoRequest): ConfirmacaoResponse {
        return httpClient.post("http://10.0.2.2/jaboataoAPI/src/Api/confirmacao.php") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            setBody(request)
        }.body()
    }
}
