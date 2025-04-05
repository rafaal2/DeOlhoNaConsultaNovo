//package com.recifemecatron.deolhonaconsulta.data.repository
//
//import com.saudejaboatao.AppAdolescente.data.Api.response.UsuarioResponse
//import io.ktor.client.HttpClient
//import io.ktor.client.call.body
//import io.ktor.client.request.post
//import io.ktor.client.request.setBody
//import io.ktor.http.ContentType
//import io.ktor.http.contentType
//
//class UsuarioRepository(private val httpClient: HttpClient) {
//
//    suspend fun postUsuario(usuario: UsuarioResponse): UsuarioResponse {
//        return httpClient.post("https://jaboatao.gerenciasus.com.br/adolescentes/api/usuarios.php") {
//            contentType(ContentType.Application.Json)
//            setBody(usuario)
//        }.body()
//    }
//}