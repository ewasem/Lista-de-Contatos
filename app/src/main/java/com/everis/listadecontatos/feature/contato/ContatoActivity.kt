package com.everis.listadecontatos.feature.contato

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.everis.listadecontatos.R
import com.everis.listadecontatos.application.ContatoApplication
import com.everis.listadecontatos.bases.BaseActivity
import com.everis.listadecontatos.feature.listacontatos.model.ContatosVO
import com.everis.listadecontatos.singleton.ContatoSingleton
import kotlinx.android.synthetic.main.activity_contato.*
import kotlinx.android.synthetic.main.activity_contato.toolBar

class ContatoActivity : BaseActivity() {

    private var index: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contato)
        setupToolBar(toolBar, "Contato", true)
        setupContato()
        btnSalvarConato.setOnClickListener { onClickSalvarContato() }
    }

    private fun setupContato() {

        index = intent.getIntExtra("index", -1)
        if (index == -1) {
            btnExcluirContato.visibility = View.GONE
            return
        }
        progress_cont.visibility = View.VISIBLE
        Thread(Runnable {
            var lista =
                ContatoApplication.instance.helperDB?.buscarContatos("$index", true) ?: return@Runnable
            var contato = lista.getOrNull(0) ?: return@Runnable
            runOnUiThread {
                etNome.setText(contato.nome)
                etTelefone.setText(contato.telefone)
                progress_cont.visibility = View.GONE
            }
        }).start()
    }

    private fun onClickSalvarContato() {
        progress_cont.visibility = View.VISIBLE
        val nome = etNome.text.toString()
        val telefone = etTelefone.text.toString()
        val contato = ContatosVO(
            index,
            nome,
            telefone
        )
        Thread(Runnable {
            if (index == -1) {
                ContatoApplication.instance.helperDB?.salvarContato(contato)
            } else {
                ContatoApplication.instance.helperDB?.updateContato(contato)
            }
            runOnUiThread {
                progress_cont.visibility = View.GONE
                finish()
            }
        }).start()
    }

    fun onClickExcluirContato(view: View) {
        progress_cont.visibility = View.VISIBLE
        Thread(Runnable {
            if (index > -1) {
                ContatoApplication.instance.helperDB?.deletarContato(index)
                runOnUiThread {
                    progress_cont.visibility = View.GONE
                    finish()
                }
            }
        }).start()
    }
}
