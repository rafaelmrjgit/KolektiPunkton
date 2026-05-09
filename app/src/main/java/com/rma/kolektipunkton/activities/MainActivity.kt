package com.rma.kolektipunkton.activities

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.helper.CarregarConstantes
import com.rma.kolektipunkton.helper.Constantes
import com.rma.kolektipunkton.helper.Instalacao
import com.rma.kolektipunkton.helper.ToastPersonalizado
import java.lang.Thread

class MainActivity : AppCompatActivity() {
    private var mAppBarConfiguration: AppBarConfiguration? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        aplicarTemaSalvo()

        Thread(Inicializacao()).start()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar?>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer: DrawerLayout? = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_grafico,
            R.id.nav_tabela,
            R.id.nav_enviar,
            R.id.nav_sobre_app,
        )
            .setDrawerLayout(drawer)
            .build()

        val navController: NavController = Navigation.findNavController(this, R.id.nav_host_fragment)

        mAppBarConfiguration?.let {
            NavigationUI.setupActionBarWithNavController(this, navController, it)
        }

        navigationView?.let {
            NavigationUI.setupWithNavController(it, navController)
            it.itemIconTintList = null
        }
    }


    override fun onResume() {
        super.onResume()
        navigationView?.menu?.getItem(0)?.isChecked = true

        /* onResume é executado depois de onCreate e onStart.*/
        /*Quando o usuário sair do app e retornar, deve aparecer tudo do último projeto visualizado.*/
        //Abrir último banco de dados
        //recuperar na string Constantes.nomeProjeto o nome em preferencias; ver onStop
        if (!Constantes.nomeProjetoSistema_Excluir_Opcao_Sim.isNullOrEmpty()) {
            //Esse if refere-se a ExclusaoProjActivity
            abrirComFade(Intent(this, ConfirmaExclusaoProjetoActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return mAppBarConfiguration?.let {
            NavigationUI.navigateUp(navController, it)
        } ?: super.onSupportNavigateUp() || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuNovoP -> novoProjeto()
            R.id.menuAbrirP -> abrirProjeto()
            R.id.menuExcluirP -> excluirProjeto()
            R.id.menuLimparP -> limparProjeto()
            R.id.menuPref -> configuracoes()
            R.id.menuInstrucoes -> instrucoes()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onStop() {
        super.onStop()
        /*Quando o usuário abrir outro app, fechar o app atual ou ir para outra activity
        do app atual, o método onStop será executado. Esse método é executado depois de
        onPause e antes de onDestroy (fechamento do app). Se o usuário abrir outro app ou for
        para outra activity, a activity atual para no onStop.*/
        //salvar em preferencias o nome do projeto em uso; recuperar na string nomeProjeto usando o método onResume
        val prefs = getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0)
        Constantes.nomeProjetoPref = prefs
        val editor: SharedPreferences.Editor = prefs.edit()
        if (Constantes.nomeProjeto.isNullOrEmpty()) {
            ToastPersonalizado(
                applicationContext,
                getString(R.string.pref_nome_vazio),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            editor.putString(getString(R.string.nomeprojetokey), Constantes.nomeProjeto)
            editor.apply()
        }

        //
        handler.removeCallbacksAndMessages(null)
    }

    private fun instrucoes() = abrirComFade(Intent(this, InstrucoesActivity::class.java))

    private fun novoProjeto() = abrirComFade(Intent(this, CriarProjActivity::class.java))

    private fun abrirProjeto() = abrirComFade(Intent(this, AbrirProjActivity::class.java))

    private fun excluirProjeto() = abrirComFade(Intent(this, ExclusaoProjActivity::class.java))

    private fun limparProjeto() = abrirComFade(Intent(this, LimparProjActivity::class.java))

    private fun configuracoes() = abrirComFade(Intent(this, ConfiguracoesActivity::class.java))

    private fun abrirComFade(intent: Intent) {
        val options = android.app.ActivityOptions.makeCustomAnimation(
            this, android.R.anim.fade_in, android.R.anim.fade_out
        )
        startActivity(intent, options.toBundle())
    }

    companion object {
        private var navigationView: NavigationView? = null
    }

    private inner class Inicializacao : Runnable {
        override fun run() {
            val toasts = ArrayList<String?>()
            try {
                val instalacao = Instalacao(applicationContext)
                instalacao.instalarBDePref()
                toasts.addAll(instalacao.toasts)

                val carregarConstantes = CarregarConstantes(applicationContext)
                carregarConstantes.executar()
            } catch (ignored: Exception) {
                toasts.add(getString(R.string.db_erro_instalacao))
            }

            handler.post {
                for (msg in toasts) {
                    msg?.let { ToastPersonalizado(this@MainActivity, it, Toast.LENGTH_LONG).show() }
                }
            }
        }
    }

    private fun aplicarTemaSalvo() {
        val prefs = getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0)
        val temaKey = getString(R.string.temaestilo)

        // Busca o tema ou usa "sistema" como padrão
        val tema = prefs.getString(temaKey, getString(R.string.tema_sistema))

        // Atualiza a constante global para o restante do app
        Constantes.tema_estilo = tema

        when (tema) {
            getString(R.string.tema_dia) ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            getString(R.string.tema_noite) ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            else ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }


}