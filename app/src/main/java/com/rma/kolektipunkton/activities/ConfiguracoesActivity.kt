package com.rma.kolektipunkton.activities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Spinner
import com.google.android.material.materialswitch.MaterialSwitch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.button.MaterialButtonToggleGroup.OnButtonCheckedListener
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.helper.Constantes
import com.rma.kolektipunkton.helper.setupEdgeToEdge

class ConfiguracoesActivity : AppCompatActivity() {
    //private RadioButton radioVm, radioAz, radioVd, radioAm, radioPr, radioBr;
    //private RadioGroup radioGroup;
    private lateinit var chipVm: Chip
    private lateinit var chipAm: Chip
    private lateinit var chipAz: Chip
    private lateinit var chipVd: Chip
    private lateinit var chipPr: Chip
    private lateinit var chipBr: Chip
    private lateinit var chipGroup: ChipGroup
    private lateinit var spinner: Spinner
    private lateinit var spinnerSens: Spinner
    private lateinit var imgTamanhoPonto: ImageView

    /*private String[] permissoesNecessarias = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };*/
    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var bt_default: MaterialButton
    private lateinit var bt_dark: MaterialButton
    private lateinit var bt_light: MaterialButton
    private lateinit var autoSave: MaterialSwitch
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        // status bar e navigation bar
        val root = findViewById<View>(R.id.configuracoesLayout)
        val appBar = findViewById<View>(R.id.appBarLayout)
        window.setupEdgeToEdge(root, appBar)
        //toolbar
        val toolbar = findViewById<Toolbar>(R.id.appBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }


        val prefs = getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0) //modo privado
        Constantes.nomeProjetoPref = prefs

        imgTamanhoPonto = findViewById<ImageView>(R.id.imageViewTamanhoPonto)
        imgTamanhoPonto.setImageResource(R.drawable.ic_fundo)

        //salvamento automático
        autoSave = findViewById(R.id.switchAutoSave)
        if (Constantes.autosave) {
            autoSave.setChecked(true)
        } else {
            autoSave.setChecked(false)
        }
        autoSave.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                if (isChecked) {
                    Constantes.autosave = true
                    Constantes.nomeProjetoPref?.let { prefs ->
                        val editor = prefs.edit()
                        editor.putBoolean(getString(R.string.autosave), true)
                        editor.apply()
                    }
                } else {
                    Constantes.autosave = false
                    Constantes.nomeProjetoPref?.let { prefs ->
                        val editor = prefs.edit()
                        editor.putBoolean(getString(R.string.autosave), false)
                        editor.apply()
                    }
                }
            }
        })


        //tema escuro/claro
        toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggle_themeConf)
        bt_default = findViewById<MaterialButton>(R.id.bt_default)
        bt_dark = findViewById<MaterialButton>(R.id.bt_dark)
        bt_light = findViewById<MaterialButton>(R.id.bt_light)

        var temaestilo: String? = null
        Constantes.nomeProjetoPref?.let { prefs ->
            if (!prefs.contains(getString(R.string.temaestilo))) {
                val editor = prefs.edit()
                editor.putString(getString(R.string.temaestilo), getString(R.string.tema_sistema))
                editor.apply()
                temaestilo = getString(R.string.tema_sistema)
            } else {
                temaestilo = prefs.getString(getString(R.string.temaestilo), "")
                if (temaestilo == getString(R.string.tema_dia)) {
                    bt_light.setChecked(true)
                } else if (temaestilo == getString(R.string.tema_noite)) {
                    bt_dark.setChecked(true)
                } else if (temaestilo == getString(R.string.tema_sistema)) {
                    bt_default.setChecked(true)
                }
            }
        } ?: run {
            temaestilo = getString(R.string.tema_sistema)
        }

        //altera e grava o tema
        toggleGroup.addOnButtonCheckedListener(object : OnButtonCheckedListener {
            override fun onButtonChecked(
                group: MaterialButtonToggleGroup?,
                checkedId: Int,
                isChecked: Boolean
            ) {
                Constantes.nomeProjetoPref?.let { prefs ->
                    val editor = prefs.edit()
                    if (isChecked) {
                        if (checkedId == R.id.bt_default) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            editor.putString(
                                getString(R.string.temaestilo),
                                getString(R.string.tema_sistema)
                            )
                            editor.apply()
                            Constantes.tema_estilo = getString(R.string.tema_sistema)
                        } else if (checkedId == R.id.bt_dark) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            editor.putString(
                                getString(R.string.temaestilo),
                                getString(R.string.tema_noite)
                            )
                            editor.apply()
                            Constantes.tema_estilo = getString(R.string.tema_noite)
                        } else if (checkedId == R.id.bt_light) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            editor.putString(
                                getString(R.string.temaestilo),
                                getString(R.string.tema_dia)
                            )
                            editor.apply()
                            Constantes.tema_estilo = getString(R.string.tema_dia)
                        }
                    }
                }
            }
        })


        //sensibilidade
        spinnerSens = findViewById<Spinner>(R.id.spinnerConfig2)
        when (Constantes.sensibilidade) {
            Constantes.SENSIBILIDADE_BAIXA -> setSpinnerText(
                spinnerSens,
                getString(R.string.sens_baixa_str)
            )

            Constantes.SENSIBILIDADE_ALTA -> setSpinnerText(
                spinnerSens,
                getString(R.string.sens_alta_str)
            )
        }
        spinnerSens.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                val textospinner = spinnerSens.getSelectedItem().toString()
                if (textospinner != null && textospinner != "") {
                    Constantes.nomeProjetoPref?.let { prefs ->
                        val editor = prefs.edit()
                        if (textospinner == getString(R.string.sens_baixa_str)) {
                            Constantes.sensibilidade = Constantes.SENSIBILIDADE_BAIXA
                            editor.putInt(
                                getString(R.string.sensibilidade),
                                Constantes.SENSIBILIDADE_BAIXA
                            )
                            editor.apply()
                        } else if (textospinner == getString(R.string.sens_alta_str)) {
                            Constantes.sensibilidade = Constantes.SENSIBILIDADE_ALTA
                            editor.putInt(
                                getString(R.string.sensibilidade),
                                Constantes.SENSIBILIDADE_ALTA
                            )
                            editor.apply()
                        }
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })


        //cores dos eixos e pontos
        chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        chipVm = findViewById<Chip>(R.id.chip1)
        chipAm = findViewById<Chip>(R.id.chip2)
        chipAz = findViewById<Chip>(R.id.chip3)
        chipVd = findViewById<Chip>(R.id.chip4)
        chipPr = findViewById<Chip>(R.id.chip5)
        chipBr = findViewById<Chip>(R.id.chip6)
        val coreixo = Constantes.corEixosPontos
        if (coreixo == getResources().getColor(R.color.corEixoPPr)) {
            chipPr.setChecked(true)
        } else if (coreixo == getResources().getColor(R.color.corEixoPBr)) {
            chipBr.setChecked(true)
        } else if (coreixo == getResources().getColor(R.color.corEixoPVm)) {
            chipVm.setChecked(true)
        } else if (coreixo == getResources().getColor(R.color.corEixoPAm)) {
            chipAm.setChecked(true)
        } else if (coreixo == getResources().getColor(R.color.corEixoPAz)) {
            chipAz.setChecked(true)
        } else if (coreixo == getResources().getColor(R.color.corEixoPVd)) {
            chipVd.setChecked(true)
        }

        chipGroup.setOnCheckedChangeListener(object : ChipGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: ChipGroup, checkedId: Int) {
                Constantes.nomeProjetoPref?.let { prefs ->
                    val editor = prefs.edit()
                    editor.remove(getString(R.string.coreixo))
                    if (checkedId == R.id.chip5) {
                        editor.putInt(
                            getString(R.string.coreixo),
                            getResources().getColor(R.color.corEixoPPr)
                        )
                        editor.apply()
                        Constantes.corEixosPontos = getResources().getColor(R.color.corEixoPPr)
                    } else if (checkedId == R.id.chip6) {
                        editor.putInt(
                            getString(R.string.coreixo),
                            getResources().getColor(R.color.corEixoPBr)
                        )
                        editor.apply()
                        Constantes.corEixosPontos = getResources().getColor(R.color.corEixoPBr)
                    } else if (checkedId == R.id.chip2) {
                        editor.putInt(
                            getString(R.string.coreixo),
                            getResources().getColor(R.color.corEixoPAm)
                        )
                        editor.apply()
                        Constantes.corEixosPontos = getResources().getColor(R.color.corEixoPAm)
                    } else if (checkedId == R.id.chip1) {
                        editor.putInt(
                            getString(R.string.coreixo),
                            getResources().getColor(R.color.corEixoPVm)
                        )
                        editor.apply()
                        Constantes.corEixosPontos = getResources().getColor(R.color.corEixoPVm)
                    } else if (checkedId == R.id.chip3) {
                        editor.putInt(
                            getString(R.string.coreixo),
                            getResources().getColor(R.color.corEixoPAz)
                        )
                        editor.apply()
                        Constantes.corEixosPontos = getResources().getColor(R.color.corEixoPAz)
                    } else if (checkedId == R.id.chip4) {
                        editor.putInt(
                            getString(R.string.coreixo),
                            getResources().getColor(R.color.corEixoPVd)
                        )
                        editor.apply()
                        Constantes.corEixosPontos = getResources().getColor(R.color.corEixoPVd)
                    }
                }
                desenharCirculo(Constantes.tamanhoPontos)
            }
        })


        //tamanho dos pontos
        spinner = findViewById<Spinner>(R.id.spinnerConfig)
        val tamanhoponto = Constantes.tamanhoPontos
        when (tamanhoponto) {
            Constantes.tamanhoPequeno -> {
                setSpinnerText(spinner, getString(R.string.tpequeno))
                Constantes.tamanhoPontos = Constantes.tamanhoPequeno
            }

            Constantes.tamanhoMedio -> {
                setSpinnerText(spinner, getString(R.string.tmedio))
                Constantes.tamanhoPontos = Constantes.tamanhoMedio
            }

            Constantes.tamanhoGrande -> {
                setSpinnerText(spinner, getString(R.string.tgrande))
                Constantes.tamanhoPontos = Constantes.tamanhoGrande
            }
        }

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                Constantes.nomeProjetoPref?.let { prefs ->
                    val editor = prefs.edit()
                    editor.remove(getString(R.string.tamanhoponto))
                    val textospinner = spinner.getSelectedItem().toString()
                    if (textospinner != null && textospinner != "") {
                        if (textospinner == getString(R.string.tpequeno)) {
                            Constantes.tamanhoPontos = Constantes.tamanhoPequeno
                            editor.putInt(
                                getString(R.string.tamanhoponto),
                                Constantes.tamanhoPequeno
                            )
                            editor.apply()
                        } else if (textospinner == getString(R.string.tmedio)) {
                            Constantes.tamanhoPontos = Constantes.tamanhoMedio
                            editor.putInt(getString(R.string.tamanhoponto), Constantes.tamanhoMedio)
                            editor.apply()
                        } else if (textospinner == getString(R.string.tgrande)) {
                            Constantes.tamanhoPontos = Constantes.tamanhoGrande
                            editor.putInt(
                                getString(R.string.tamanhoponto),
                                Constantes.tamanhoGrande
                            )
                            editor.apply()
                        }
                    }
                }
                desenharCirculo(Constantes.tamanhoPontos)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })

        imgTamanhoPonto.doOnLayout {
            desenharCirculo(Constantes.tamanhoPontos)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // This tells the activity to go back to the previous screen
        onBackPressedDispatcher.onBackPressed()
        return true
    }


    //para o botão voltar ==========================
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) {
            finish()
            //return false;
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }


    // ============================================
    private fun desenharCirculo(tamanho: Int) {
        imgTamanhoPonto.setImageResource(R.drawable.ic_fundo)
        imgTamanhoPonto.setDrawingCacheEnabled(true)
        val bitmap_original = imgTamanhoPonto.getDrawingCache()
        if (bitmap_original == null) {
            return
        }
        val bitmap_novo = Bitmap.createBitmap(
            bitmap_original.getWidth(),
            bitmap_original.getHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap_novo)
        canvas.drawBitmap(bitmap_original, 0f, 0f, null)
        val paint_curva = Paint(Paint.ANTI_ALIAS_FLAG)
        paint_curva.setColor(getResources().getColor(R.color.corEixoPPr))
        canvas.drawCircle(
            imgTamanhoPonto.getWidth() / 2f,
            imgTamanhoPonto.getHeight() / 2f,
            (tamanho + tamanho / 5).toFloat(),
            paint_curva
        )
        paint_curva.setColor(Constantes.corEixosPontos)
        canvas.drawCircle(
            imgTamanhoPonto.getWidth() / 2f,
            imgTamanhoPonto.getHeight() / 2f,
            tamanho.toFloat(),
            paint_curva
        )
        imgTamanhoPonto.setImageBitmap(bitmap_novo)
        imgTamanhoPonto.setDrawingCacheEnabled(false)
    }


    fun setSpinnerText(spinner: Spinner, text: String) {
        for (i in 0..<spinner.getAdapter().getCount()) {
            if (spinner.getAdapter().getItem(i).toString().contains(text)) {
                spinner.setSelection(i)
            }
        }
    }
}


