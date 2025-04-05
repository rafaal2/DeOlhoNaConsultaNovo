package com.recifemecatron.deolhonaconsulta.data.repository

import com.recifemecatron.deolhonaconsulta.data.Api.response.AvaliacaoRequest
import com.recifemecatron.deolhonaconsulta.data.Api.response.AvaliacaoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class AvaliacaoRepository(private val httpClient: HttpClient) {
    suspend fun createAvaliacao(request: AvaliacaoRequest): AvaliacaoResponse {
        return httpClient.post("http://10.0.2.2/jaboataoAPI/src/Api/avaliacao.php?acao=create") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            setBody(request)
        }.body()
    }
}