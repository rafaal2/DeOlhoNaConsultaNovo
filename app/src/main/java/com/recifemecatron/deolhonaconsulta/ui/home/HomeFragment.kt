package com.recifemecatron.deolhonaconsulta.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.recifemecatron.deolhonaconsulta.R
import com.recifemecatron.deolhonaconsulta.data.Api.HttpClientFactory
import com.recifemecatron.deolhonaconsulta.data.Database.ConsultaDao
import com.recifemecatron.deolhonaconsulta.data.repository.ConsultaRepository
import com.recifemecatron.deolhonaconsulta.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var tokenFirebase: String? = null

    private val consultaRepository = ConsultaRepository(HttpClientFactory.client)

    // Instância do DAO para usar no insert e getAll
    private val dao by lazy { ConsultaDao(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obter token Firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Erro ao obter token do Firebase", task.exception)
                return@addOnCompleteListener
            }
            tokenFirebase = task.result
            Log.d("FCM_TOKEN", "Token Firebase: $tokenFirebase")
        }

        // [1] Detectar clique no "drawableEnd" do EditText
        binding.editText.setOnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                val editText = binding.editText
                // Verifica se o clique foi no fim do EditText
                if (event.rawX >= (editText.right - editText.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    mostrarHistorico() // chama o AlertDialog de histórico
                    return@setOnTouchListener true
                }
            }
            false
        }

        // [2] Clique no botão para enviar solicitação
        binding.buttonSubmit.setOnClickListener {
            val codSolicitacao = binding.editText.text.toString()

            if (codSolicitacao.isBlank()) {
                Toast.makeText(context, "Digite o código da solicitação", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tokenFirebase.isNullOrEmpty()) {
                Toast.makeText(context, "Token Firebase indisponível. Tente novamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!hasInternetConnection(requireContext())) {
                Toast.makeText(context, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setLoadingState(true)

            lifecycleScope.launch {
                try {
                    val response = consultaRepository.consulta(
                        codSolicitacao,
                        tokenFirebase!!,
                        "app_A"
                    )
                    Log.d("API_RESPONSE", "Status: ${response.STATUS}")

                    // Salvar no banco se não existir
                    salvarSolicitacaoLocal(codSolicitacao)

                    // Navega para a próxima tela
                    val bundle = Bundle().apply {
                        putString("codSolicitacao", codSolicitacao)
                        putString("status", response.STATUS)
                        putString("unidade_solicitante", response.UNIDADE_SOLICITANTE)
                    }
                    findNavController().navigate(R.id.action_homeFragment_to_resultFragment, bundle)

                } catch (e: SocketTimeoutException) {
                    Toast.makeText(context, "Servidor demorou a responder. Tente novamente.", Toast.LENGTH_SHORT).show()
                    Log.e("API_RESPONSE", "Timeout", e)
                } catch (e: IOException) {
                    Toast.makeText(context, "Erro de conexão.", Toast.LENGTH_SHORT).show()
                    Log.e("API_RESPONSE", "IOException", e)
                } catch (e: Exception) {
                    Toast.makeText(context, "Erro inesperado.", Toast.LENGTH_SHORT).show()
                    Log.e("API_RESPONSE", "Erro inesperado", e)
                } finally {
                    setLoadingState(false)
                }
            }
        }
    }

    /**
     * [3] Exibir um AlertDialog com o histórico
     */
    private fun mostrarHistorico() {
        val listaConsultas = dao.getAll()
        if (listaConsultas.isEmpty()) {
            Toast.makeText(context, "Nenhuma solicitação anterior.", Toast.LENGTH_SHORT).show()
            return
        }

        val listaCodigos = listaConsultas.map { it.codSolicitacao }
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Suas Solicitações Anteriores")
        builder.setItems(listaCodigos.toTypedArray()) { dialog, which ->
            val selecionado = listaCodigos[which]
            // Preenche o EditText com o item clicado
            binding.editText.setText(selecionado)
            dialog.dismiss()
        }
        builder.setNegativeButton("Fechar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    /**
     * [4] Insere no banco local, só se não existir
     */
    private fun salvarSolicitacaoLocal(cod: String) {
        if (!dao.exists(cod)) {
            val resultado = dao.insert(cod)
            if (resultado == -1L) {
                Log.w("HOME_FRAGMENT", "Falha ao inserir (possivelmente UNIQUE já existe).")
            } else {
                Log.d("HOME_FRAGMENT", "Inserido com sucesso! ID: $resultado")
            }
        } else {
            Log.d("HOME_FRAGMENT", "Solicitação '$cod' já existe no histórico.")
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.buttonSubmit.isEnabled = !isLoading
        binding.editText.isEnabled = !isLoading
        binding.buttonSubmit.alpha = if (isLoading) 0.5f else 1.0f
    }

    private fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
