package com.rma.kolektipunkton.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.helper.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.abs
import kotlin.math.pow


class GraficoFragment : Fragment() {
    private lateinit var imagem_carregada: ImageView
    private lateinit var imageButtonCamera: ImageButton
    private lateinit var imageButtonGaleria: ImageButton
    private lateinit var botaoDesfazer: ImageButton
    private lateinit var imagem_amostra: ImageButton
    private lateinit var botao_parar: ImageButton
    private lateinit var buttonAutomatico: ImageButton
    private lateinit var botao_gerar_tabela: Button
    
    private var p_escolhido_sistema: Point? = null
    private var p_origem_sistema: Point? = null
    private var p_x_max_sistema: Point? = null
    private var p_y_max_sistema: Point? = null
    private var contagem_toques = 0
    
    private lateinit var p_escolhido_sistema_float: PontoFloat
    private lateinit var p_origem_sistema_float: PontoFloat
    private lateinit var p_x_max_sistema_float: PontoFloat
    private lateinit var p_y_max_sistema_float: PontoFloat
    
    private var densidadeDisplay = 0f
    private var bitmap_original: Bitmap? = null
    private var paint_eixo: Paint? = null
    private var paint_curva: Paint? = null
    private var paint_externo: Paint? = null
    private var paint_externoL: Paint? = null
    
    private lateinit var textoOrigemX: TextInputEditText
    private lateinit var textoOrigemY: TextInputEditText
    private lateinit var textoMaxX: TextInputEditText
    private lateinit var textoMaxY: TextInputEditText
    private lateinit var progressBar: ProgressBar
    private lateinit var cardViewProgressBar: CardView
    private lateinit var cardViewBottom: CardView
    private lateinit var textoStatus: TextView
    
    private var corPixel_onTouch = 0
    private var pararExecAutomatica = false
    private val handler = Handler(Looper.getMainLooper())
    private var salvandoBotao = false
    private var salvandoTemp = false
    private var variacaoQtdePontos = false
    private var origemX: String? = null
    private var origemY: String? = null
    private var maxX: String? = null
    private var maxY: String? = null
    private var executandoSelecaoAuto = false
    private var salvamentoTemporizado: Thread? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_grafico, container, false)

        progressBar = root.findViewById(R.id.progressBarCarregamento)
        cardViewProgressBar = root.findViewById(R.id.cardViewProgressBar)
        cardViewBottom = root.findViewById(R.id.cardViewBottom)
        textoStatus = root.findViewById(R.id.textViewStatus)

        textoOrigemX = root.findViewById(R.id.editTextOrigemX)
        textoOrigemY = root.findViewById(R.id.editTextOrigemY)
        textoMaxX = root.findViewById(R.id.editTextXmax)
        textoMaxY = root.findViewById(R.id.editTextYmax)

        imageButtonCamera = root.findViewById(R.id.imageButtonCamera)
        imageButtonGaleria = root.findViewById(R.id.imageButtonGaleria)
        botaoDesfazer = root.findViewById(R.id.imageButtonUndo)
        buttonAutomatico = root.findViewById(R.id.buttonAutomatico)
        botao_gerar_tabela = root.findViewById(R.id.botao_gerar_tabela)

        imagem_amostra = root.findViewById(R.id.imageButtonCorEscolhida)
        imagem_carregada = root.findViewById(R.id.imagem_carregada)

        botao_parar = root.findViewById(R.id.imageButtonStop)

        selecaoImagem()
        selecaoAutomaticaPontos()
        pararSelecaoAutomatica()
        salvarTudo()
        toquesImagem()
        desfazerUltimoPonto()

        val layout: ConstraintLayout = root.findViewById(R.id.constraintGrafico)
        val vto: ViewTreeObserver = layout.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                desenhaGraficoInicializacao()
            }
        })
        return root
    }

    private fun desenhaGraficoInicializacao() {
        val path = File(requireActivity().filesDir, "kpfolderoriginal")
        val arquivoImg = File(path, (Constantes.nomeProjeto ?: "") + ".jpeg")
        if (arquivoImg.exists()) {
            var imagem: Bitmap? = null
            val localImagem = Uri.fromFile(arquivoImg)
            try {
                @Suppress("DEPRECATION")
                imagem = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    localImagem
                )
            } catch (e: IOException) {
            }
            if (imagem != null) {
                imagem_carregada.setImageBitmap(imagem)
                try {
                    p_origem_sistema?.let { desenharOrigemEixos(it) }
                    p_x_max_sistema?.let { desenharXmax(it) }
                    p_y_max_sistema?.let { desenharYmax(it) }
                    desenharPontosCurva(pontos_curva_sistema)
                } catch (ignored: Exception) {
                }
            }
        } else {
            //imagem_carregada.setImageResource(R.color.md_theme_secondary)
        }
    }

    private fun desfazerUltimoPonto() {
        botaoDesfazer.setOnClickListener {
            val path = File(requireActivity().filesDir, "kpfolderoriginal")
            val arquivo = File(path, (Constantes.nomeProjeto ?: "") + ".jpeg")
            if (!arquivo.exists()) {
                pontos_curva_sistema.clear()
                pontos_curva_sistema_float.clear()
                ToastPersonalizado(
                    requireActivity(),
                    getString(R.string.img_sem_img_pasta),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            var imagem: Bitmap? = null
            try {
                val localImagem = Uri.fromFile(arquivo)
                @Suppress("DEPRECATION")
                imagem = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    localImagem
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (imagem != null) {
                imagem_carregada.setImageBitmap(imagem)
                if (pontos_curva_sistema_float.isNotEmpty()) {
                    pontos_curva_sistema_float.removeAt(pontos_curva_sistema_float.size - 1)
                }
                if (pontos_curva_sistema.isNotEmpty()) {
                    pontos_curva_sistema.removeAt(pontos_curva_sistema.size - 1)
                    variacaoQtdePontos = true
                    origemX = textoOrigemX.text.toString()
                    origemY = textoOrigemY.text.toString()
                    maxX = textoMaxX.text.toString()
                    maxY = textoMaxY.text.toString()
                }
                try {
                    p_origem_sistema?.let { desenharOrigemEixos(it) }
                    p_x_max_sistema?.let { desenharXmax(it) }
                    p_y_max_sistema?.let { desenharYmax(it) }
                    desenharPontosCurva(pontos_curva_sistema)
                } catch (ignored: Exception) {
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun toquesImagem() {
        imagem_carregada.setOnTouchListener { v, event ->
            p_escolhido_sistema = Point(event.x.toInt(), event.y.toInt())
            p_escolhido_sistema_float.valorX = event.x
            p_escolhido_sistema_float.valorY = event.y
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                @Suppress("DEPRECATION")
                imagem_carregada.isDrawingCacheEnabled = true
                @Suppress("DEPRECATION")
                val bitmap = imagem_carregada.drawingCache
                if (bitmap != null) {
                    if (event.x >= 0f && event.x <= bitmap.width && event.y >= 0f && event.y <= bitmap.height) {
                        val pixel = bitmap.getPixel(event.x.toInt(), event.y.toInt())
                        imagem_amostra.imageTintList = ColorStateList.valueOf(pixel)
                        corPixel_onTouch = pixel
                    }
                }
                @Suppress("DEPRECATION")
                imagem_carregada.isDrawingCacheEnabled = false
            }
            false
        }

        contagem_toques = 0

        imagem_carregada.setOnClickListener {
            val path = File(requireActivity().filesDir, "kpfolderoriginal")
            val arquivo = File(path, (Constantes.nomeProjeto ?: "") + ".jpeg")
            if (!arquivo.exists()) {
                pontos_curva_sistema.clear()
                pontos_curva_sistema_float.clear()
                ToastPersonalizado(
                    requireActivity(),
                    getString(R.string.img_sem_pasta),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            contagem_toques++
            val bdDAO = BdDAO(requireActivity(), Constantes.nomeProjeto ?: "")
            val qtdePontosSist = bdDAO.listarPontosTabelaSist().size
            val qtdePontoOrigemSist = bdDAO.existePontoOrigemSist()
            val qtePontoMaxXSist = bdDAO.existePontoMaxX()
            val qtePontoMaxYSist = bdDAO.existePontoMaxY()

            if (qtdePontosSist + qtdePontoOrigemSist + qtePontoMaxXSist + qtePontoMaxYSist + contagem_toques == 1) {
                p_escolhido_sistema?.let { p ->
                    p_origem_sistema = Point(p.x, p.y)
                    p_origem_sistema_float.valorX = p_escolhido_sistema_float.valorX
                    p_origem_sistema_float.valorY = p_escolhido_sistema_float.valorY
                    desenharOrigemEixos(p_origem_sistema!!)
                }
            } else if (qtdePontosSist + qtdePontoOrigemSist + qtePontoMaxXSist + qtePontoMaxYSist + contagem_toques == 2) {
                p_escolhido_sistema?.let { p ->
                    p_x_max_sistema = Point(p.x, p.y)
                    p_x_max_sistema_float.valorX = p_escolhido_sistema_float.valorX
                    p_x_max_sistema_float.valorY = p_escolhido_sistema_float.valorY
                    desenharXmax(p_x_max_sistema!!)
                }
            } else if (qtdePontosSist + qtdePontoOrigemSist + qtePontoMaxXSist + qtePontoMaxYSist + contagem_toques == 3) {
                p_escolhido_sistema?.let { p ->
                    p_y_max_sistema = Point(p.x, p.y)
                    p_y_max_sistema_float.valorX = p_escolhido_sistema_float.valorX
                    p_y_max_sistema_float.valorY = p_escolhido_sistema_float.valorY
                    desenharYmax(p_y_max_sistema!!)
                }
            } else if (qtdePontosSist + qtdePontoOrigemSist + qtePontoMaxXSist + qtePontoMaxYSist + contagem_toques > 3) {
                buttonAutomatico.visibility = View.VISIBLE
                p_escolhido_sistema?.let { p ->
                    pontos_curva_sistema.add(Point(p.x, p.y))
                    val pontoFloat = PontoFloat()
                    pontoFloat.valorX = p_escolhido_sistema_float.valorX
                    pontoFloat.valorY = p_escolhido_sistema_float.valorY
                    pontos_curva_sistema_float.add(pontoFloat)
                    desenharPontosCurva(pontos_curva_sistema)
                    variacaoQtdePontos = true
                    origemX = textoOrigemX.text.toString()
                    origemY = textoOrigemY.text.toString()
                    maxX = textoMaxX.text.toString()
                    maxY = textoMaxY.text.toString()
                }
            }
        }
    }

    private fun salvarTudo() {
        botao_gerar_tabela.setOnClickListener {
            salvandoBotao = true
            botaoSalvarExecutar()
        }
    }

    private fun selecaoAutomaticaPontos() {
        buttonAutomatico.visibility = View.INVISIBLE
        buttonAutomatico.setOnClickListener {
            botaoSelecaoAutoPontos()
        }
    }

    private fun pararSelecaoAutomatica() {
        botao_parar.isEnabled = false
        botao_parar.visibility = View.INVISIBLE
        botao_parar.setOnClickListener {
            pararExecAutomatica = true
        }
    }

    private fun selecaoImagem() {
        val permissaoNecessariaCamera = arrayOf(Manifest.permission.CAMERA)
        imageButtonCamera.setOnClickListener {
            Permissao.validarPermissoes(permissaoNecessariaCamera, requireActivity(), 1)
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    @Suppress("DEPRECATION")
                    startActivityForResult(intent, SELECAO_CAMERA)
                }
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                val prefs = requireActivity().getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0)
                Constantes.nomeProjetoPref = prefs
                if (Constantes.primeiro_acesso_camera == getString(R.string.opcao_sim)) {
                    prefs.edit().putString(getString(R.string.primeiro_acesso_camera), getString(R.string.opcao_nao)).apply()
                } else if (Constantes.primeiro_acesso_camera == getString(R.string.opcao_nao)) {
                    MaterialAlertDialogBuilder(requireActivity(), R.style.AlertDialogCustom)
                        .setTitle(getString(R.string.img_permissao_titulo))
                        .setMessage(getString(R.string.img_permissao_camera) + "\n" + getString(R.string.img_permissoes_negadas_conf))
                        .setPositiveButton(getString(R.string.sim)) { dialog, which ->
                            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + requireActivity().packageName)))
                        }
                        .setNegativeButton(getString(R.string.nao), null)
                        .show()
                }
            }
        }

        imageButtonGaleria.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                @Suppress("DEPRECATION")
                startActivityForResult(intent, SELECAO_GALERIA)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            var imagem: Bitmap? = null
            try {
                when (requestCode) {
                    SELECAO_CAMERA -> imagem = data?.extras?.get("data") as Bitmap?
                    SELECAO_GALERIA -> {
                        val localImagemSelecionada = data?.data
                        if (localImagemSelecionada != null) {
                            imagem = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, localImagemSelecionada)
                        }
                    }
                }
                if (imagem != null) {
                    imagem_carregada.setImageBitmap(imagem)
                    val bdDAO = BdDAO(requireActivity(), Constantes.nomeProjeto ?: "")
                    bdDAO.limparTabela()
                    val path = File(requireActivity().filesDir, "kpfolderoriginal")
                    if (!path.exists()) {
                        if (!path.mkdirs()) {
                            ToastPersonalizado(requireActivity(), getString(R.string.salvar_img_falha_arquivo), Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    val arquivo = File(path, (Constantes.nomeProjeto ?: "") + ".jpeg")
                    val baos = ByteArrayOutputStream()
                    imagem.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val dadosImagem = baos.toByteArray()
                    FileOutputStream(arquivo).use { it.write(dadosImagem) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        densidadeDisplay = resources.displayMetrics.density
        paint_eixo = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = Constantes.corEixosPontos
            strokeWidth = 6f
        }
        paint_curva = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Constantes.corEixosPontos }
        paint_externo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = resources.getColor(R.color.corEixoPPr, null) }
        paint_externoL = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = resources.getColor(R.color.corEixoPPr, null)
            strokeWidth = 10f
        }

        p_escolhido_sistema = Point(Int.MAX_VALUE, Int.MAX_VALUE)
        p_origem_sistema = Point(Int.MAX_VALUE, Int.MAX_VALUE)
        p_x_max_sistema = Point(Int.MAX_VALUE, Int.MAX_VALUE)
        p_y_max_sistema = Point(Int.MAX_VALUE, Int.MAX_VALUE)
        
        p_escolhido_sistema_float = PontoFloat().apply { valorX = Float.MAX_VALUE; valorY = Float.MAX_VALUE }
        p_origem_sistema_float = PontoFloat().apply { valorX = Float.MAX_VALUE; valorY = Float.MAX_VALUE }
        p_x_max_sistema_float = PontoFloat().apply { valorX = Float.MAX_VALUE; valorY = Float.MAX_VALUE }
        p_y_max_sistema_float = PontoFloat().apply { valorX = Float.MAX_VALUE; valorY = Float.MAX_VALUE }
        
        pontos_curva_sistema.clear()
        pontos_curva_sistema_float.clear()
        contagem_toques = 0

        if (Constantes.autosave) {
            if (salvamentoTemporizado == null) {
                salvamentoTemporizado = Thread(SalvamentoTemporizado()).apply { start() }
            }
        } else {
            salvandoTemp = false
        }
        Thread(ThreadOnResume()).start()
    }

    private inner class ThreadOnResume : Runnable {
        override fun run() {
            var resposta: String? = null
            val lista = ArrayList<BDCampos>()
            try {
                CarregarConstantes(requireActivity()).executar()
                val prefs = requireActivity().getSharedPreferences(Constantes.ARQUIVO_PREFERENCIA, 0)
                if (!prefs.contains(getString(R.string.nomeprojetokey))) {
                    resposta = getString(R.string.db_semdados)
                    return
                }
                val bdDAO = BdDAO(requireActivity(), Constantes.nomeProjeto ?: "")
                lista.addAll(bdDAO.listarTabela())
                if (lista.isNotEmpty()) {
                    val p0 = lista[0]
                    p_origem_sistema = Point(p0.p_origem_x_sist.toInt(), p0.p_origem_y_sist.toInt())
                    p_origem_sistema_float.valorX = p0.p_origem_x_sist
                    p_origem_sistema_float.valorY = p0.p_origem_y_sist
                    p_x_max_sistema = Point(p0.p_x_max_sist.toInt(), p0.p_y_max_sist.toInt())
                    p_x_max_sistema_float.valorX = p0.p_x_max_sist
                    p_x_max_sistema_float.valorY = p0.p_y_max_sist
                    p_y_max_sistema = Point(p0.p_x_max_sist.toInt(), p0.p_y_max_sist.toInt())
                    p_y_max_sistema_float.valorX = p0.p_x_max_sist
                    p_y_max_sistema_float.valorY = p0.p_y_max_sist
                }
                pontos_curva_sistema.clear()
                pontos_curva_sistema_float.clear()
                for (item in lista) {
                    pontos_curva_sistema.add(Point(item.ponto_curva_x_sist.toInt(), item.ponto_curva_y_sist.toInt()))
                    pontos_curva_sistema_float.add(PontoFloat().apply {
                        valorX = item.ponto_curva_x_sist
                        valorY = item.ponto_curva_y_sist
                    })
                }
            } finally {
                handler.post {
                    if (lista.isNotEmpty()) {
                        textoOrigemX.setText(lista[0].p_origem_x_real.toString())
                        textoOrigemY.setText(lista[0].p_origem_y_real.toString())
                        textoMaxX.setText(lista[0].p_x_max_real.toString())
                        textoMaxY.setText(lista[0].p_y_max_real.toString())
                    }
                    val activity = requireActivity() as AppCompatActivity
                    activity.supportActionBar?.title = Constantes.nomeUsuario
                    resposta?.let { ToastPersonalizado(requireActivity(), it, Toast.LENGTH_LONG).show() }
                    try { desenhaGraficoInicializacao() } catch (ignored: Exception) {}
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun botaoSelecaoAutoPontos() {
        pararExecAutomatica = false
        @Suppress("DEPRECATION")
        imagem_carregada.isDrawingCacheEnabled = true
        @Suppress("DEPRECATION")
        val bitmap = imagem_carregada.drawingCache
        if (bitmap == null) {
            ToastPersonalizado(requireActivity(), getString(R.string.img_falha_bitmap), Toast.LENGTH_LONG).show()
            return
        }
        imagem_carregada.isEnabled = false
        buttonAutomatico.isEnabled = false
        botao_parar.isEnabled = true
        botao_parar.visibility = View.VISIBLE
        botaoDesfazer.isEnabled = false
        imageButtonCamera.isEnabled = false
        imageButtonGaleria.isEnabled = false
        botao_gerar_tabela.isEnabled = false
        //botao_gerar_tabela.visibility = View.INVISIBLE
        cardViewBottom.visibility = View.GONE

        val pixel = corPixel_onTouch
        val red = Color.red(pixel)
        val green = Color.green(pixel)
        val blue = Color.blue(pixel)

        val variacao = Constantes.sensibilidade
        val progressBarMax = ((variacao * 2) + 1).toDouble().pow(3.0).toInt()
        progressBar.max = progressBarMax
        //progressBar.visibility = View.VISIBLE
        cardViewProgressBar.visibility = View.VISIBLE
        progressBar.progress = 0
        //textoStatus.visibility = View.VISIBLE
        textoStatus.text = getString(R.string.bitmap_range)

        Thread(SelecaoAutoPontos(pixel, red, green, blue, variacao, bitmap)).start()
    }

    private inner class SelecaoAutoPontos(
        private val pixel: Int,
        private val red: Int,
        private val green: Int,
        private val blue: Int,
        private val variacao: Int,
        private val bitmap: Bitmap
    ) : Runnable {
        private val rangeCores = ArrayList<Int>()

        override fun run() {
            try {
                executandoSelecaoAuto = true
                var atualizarProgressBar = 0
                rangeCores.clear()
                for (r in -variacao..variacao) {
                    for (g in -variacao..variacao) {
                        for (b in -variacao..variacao) {
                            if (red + r in 0..255 && green + g in 0..255 && blue + b in 0..255) {
                                rangeCores.add(Color.rgb(red + r, green + g, blue + b))
                            }
                            if (pararExecAutomatica) return
                        }
                    }
                    atualizarProgressBar = (atualizarProgressBar + ((variacao * 2) + 1).toDouble().pow(2.0)).toInt()
                    onProgressUpdate(atualizarProgressBar)
                }

                onProgressUpdate(Int.MAX_VALUE)
                val pontosColuna = ArrayList<PontoFloat>()
                atualizarProgressBar = 0
                for (x in 0 until bitmap.width) {
                    if (x != p_escolhido_sistema_float.valorX.toInt()) {
                        for (y in 0 until bitmap.height) {
                            if (pararExecAutomatica) return
                            val corPixel = bitmap.getPixel(x, y)
                            if (rangeCores.contains(corPixel)) {
                                pontosColuna.add(PontoFloat().apply {
                                    valorX = x.toFloat()
                                    valorY = y.toFloat()
                                    cor = corPixel
                                })
                            }
                        }
                        if (pontosColuna.isNotEmpty()) {
                            val melhorPonto = if (pontosColuna.size > 1) escolherMelhorPonto(pontosColuna) else pontosColuna[0]
                            pontos_curva_sistema.add(Point(melhorPonto.valorX.toInt(), melhorPonto.valorY.toInt()))
                            pontos_curva_sistema_float.add(PontoFloat().apply {
                                valorX = melhorPonto.valorX
                                valorY = melhorPonto.valorY
                            })
                            variacaoQtdePontos = true
                        }
                        pontosColuna.clear()
                        atualizarProgressBar += bitmap.height
                        onProgressUpdate(atualizarProgressBar)
                    }
                }
                handler.post {
                    origemX = textoOrigemX.text.toString()
                    origemY = textoOrigemY.text.toString()
                    maxX = textoMaxX.text.toString()
                    maxY = textoMaxY.text.toString()
                }
            } finally {
                handler.post {
                    desenharPontosCurva(pontos_curva_sistema)
                    progressBar.progress = 0
                    cardViewProgressBar.visibility = View.GONE
                    //progressBar.visibility = View.GONE
                    textoStatus.text = ""
                    //textoStatus.visibility = View.GONE
                    imagem_carregada.isEnabled = true
                    buttonAutomatico.isEnabled = true
                    botaoDesfazer.isEnabled = true
                    imageButtonCamera.isEnabled = true
                    imageButtonGaleria.isEnabled = true
                    botao_gerar_tabela.isEnabled = true
                    //botao_gerar_tabela.visibility = View.VISIBLE
                    cardViewBottom.visibility = View.VISIBLE
                    botao_parar.isEnabled = false
                    botao_parar.visibility = View.INVISIBLE
                    executandoSelecaoAuto = false
                }
            }
        }

        fun onProgressUpdate(valor: Int) {
            handler.post {
                if (valor != Int.MAX_VALUE) {
                    progressBar.progress = valor
                } else {
                    progressBar.progress = 0
                    progressBar.max = bitmap.width * bitmap.height
                    textoStatus.text = getString(R.string.pontos_identif)
                }
            }
        }

        fun escolherMelhorPonto(pontosColuna: ArrayList<PontoFloat>): PontoFloat {
            val corEscolhida = Color.rgb(red, green, blue)
            var menorDiferenca = abs(pontosColuna[0].cor - corEscolhida)
            var indice = 0
            for (i in pontosColuna.indices) {
                val diff = abs(pontosColuna[i].cor - corEscolhida)
                if (diff < menorDiferenca) {
                    menorDiferenca = diff
                    indice = i
                }
            }
            return pontosColuna[indice]
        }
    }

    private inner class SalvamentoTemporizado : Runnable {
        override fun run() {
            try {
                if (!Constantes.autosave || salvandoBotao || !variacaoQtdePontos || executandoSelecaoAuto) return
                salvandoTemp = true
                handler.post {
                    imagem_carregada.isEnabled = false
                    buttonAutomatico.isEnabled = false
                    botao_parar.isEnabled = false
                    botaoDesfazer.isEnabled = false
                    imageButtonCamera.isEnabled = false
                    imageButtonGaleria.isEnabled = false
                    botao_gerar_tabela.isEnabled = false
                }

                val vOrigemX = origemX?.toFloatOrNull()
                val vOrigemY = origemY?.toFloatOrNull()
                val vMaxX = maxX?.toFloatOrNull()
                val vMaxY = maxY?.toFloatOrNull()

                val pontos_curva_real = ArrayList<PontoFloat>()
                if (vOrigemX != null && vOrigemY != null && vMaxX != null && vMaxY != null) {
                    try {
                        for (i in pontos_curva_sistema.indices) {
                            val pf = PontoFloat()
                            pf.valorX = (((pontos_curva_sistema_float[i].valorX - p_origem_sistema_float.valorX) / (p_x_max_sistema_float.valorX - p_origem_sistema_float.valorX)) * (vMaxX - vOrigemX)) + vOrigemX
                            pf.valorY = (((p_origem_sistema_float.valorY - pontos_curva_sistema_float[i].valorY) / (p_origem_sistema_float.valorY - p_y_max_sistema_float.valorY)) * (vMaxY - vOrigemY)) + vOrigemY
                            pontos_curva_real.add(pf)
                        }
                    } catch (ignored: Exception) {}
                }

                val bdDAO = BdDAO(requireActivity(), Constantes.nomeProjeto ?: "")
                if (!bdDAO.excluirLinhas(Constantes.nomeProjeto ?: "")) onProgressUpdate(1)

                if (vOrigemX != null && vOrigemY != null && vMaxX != null && vMaxY != null) {
                    if (bdDAO.atualizarTabela(pontos_curva_sistema, p_origem_sistema, p_x_max_sistema, p_y_max_sistema, pontos_curva_real, vOrigemX, vOrigemY, vMaxX, vMaxY)) {
                        contagem_toques = 0
                    } else onProgressUpdate(2)
                } else {
                    if (bdDAO.atualizarTabelaSemParametros(pontos_curva_sistema, p_origem_sistema, p_x_max_sistema, p_y_max_sistema)) {
                        contagem_toques = 0
                    } else onProgressUpdate(3)
                }
                variacaoQtdePontos = false
            } finally {
                handler.post {
                    if (!salvandoBotao) {
                        imagem_carregada.isEnabled = true
                        buttonAutomatico.isEnabled = true
                        botao_parar.isEnabled = true
                        botaoDesfazer.isEnabled = true
                        imageButtonCamera.isEnabled = true
                        imageButtonGaleria.isEnabled = true
                        botao_gerar_tabela.isEnabled = true
                    }
                }
                salvandoTemp = false
                if (Constantes.autosave) handler.postDelayed(this, Constantes.INTERVALO_AUTOSAVE)
            }
        }

        fun onProgressUpdate(valor: Int) {
            handler.post {
                val msg = when(valor) {
                    1 -> getString(R.string.db_erro_tabela_atualizacao)
                    else -> getString(R.string.db_salvo_temp_falha)
                }
                ToastPersonalizado(requireActivity(), msg, if(valor==1) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun botaoSalvarExecutar() {
        val oX = textoOrigemX.text.toString()
        val oY = textoOrigemY.text.toString()
        val mX = textoMaxX.text.toString()
        val mY = textoMaxY.text.toString()

        imagem_carregada.isEnabled = false
        buttonAutomatico.isEnabled = false
        botao_parar.isEnabled = false
        botaoDesfazer.isEnabled = false
        imageButtonCamera.isEnabled = false
        imageButtonGaleria.isEnabled = false
        botao_gerar_tabela.isEnabled = false

        Thread(BotaoSalvar(oX, oY, mX, mY)).start()
    }

    private inner class BotaoSalvar(
        private val oX: String, private val oY: String, private val mX: String, private val mY: String
    ) : Runnable {
        override fun run() {
            var retorno: String? = null
            try {
                for (i in 1..10) {
                    if (salvandoTemp) Thread.sleep(2000) else break
                }
                if (salvandoTemp) {
                    retorno = getString(R.string.salvar_falha)
                    return
                }
                if (pontos_curva_sistema.isEmpty()) {
                    retorno = getString(R.string.imagem_cumprir_etapas)
                    return
                }
                if (oX.isEmpty() || oY.isEmpty() || mX.isEmpty() || mY.isEmpty()) {
                    retorno = getString(R.string.imagem_preencher_tudo)
                    return
                }
                val vOrigemX = oX.toFloatOrNull()
                val vOrigemY = oY.toFloatOrNull()
                val vMaxX = mX.toFloatOrNull()
                val vMaxY = mY.toFloatOrNull()
                if (vOrigemX == null || vOrigemY == null || vMaxX == null || vMaxY == null) {
                    retorno = getString(R.string.imagem_tipos_incorretos)
                    return
                }

                onProgressUpdate(1)
                val pontos_curva_real = ArrayList<PontoFloat>()
                for (i in pontos_curva_sistema.indices) {
                    val pf = PontoFloat()
                    pf.valorX = (((pontos_curva_sistema_float[i].valorX - p_origem_sistema_float.valorX) / (p_x_max_sistema_float.valorX - p_origem_sistema_float.valorX)) * (vMaxX - vOrigemX)) + vOrigemX
                    pf.valorY = (((p_origem_sistema_float.valorY - pontos_curva_sistema_float[i].valorY) / (p_origem_sistema_float.valorY - p_y_max_sistema_float.valorY)) * (vMaxY - vOrigemY)) + vOrigemY
                    pontos_curva_real.add(pf)
                }

                val bdDAO = BdDAO(requireActivity(), Constantes.nomeProjeto ?: "")
                if (!bdDAO.excluirLinhas(Constantes.nomeProjeto ?: "")) onProgressUpdate(5)
                if (bdDAO.atualizarTabela(pontos_curva_sistema, p_origem_sistema, p_x_max_sistema, p_y_max_sistema, pontos_curva_real, vOrigemX, vOrigemY, vMaxX, vMaxY)) {
                    onProgressUpdate(2)
                    contagem_toques = 0
                } else onProgressUpdate(4)
                variacaoQtdePontos = false
            } finally {
                handler.post {
                    retorno?.let { ToastPersonalizado(requireActivity(), it, Toast.LENGTH_LONG).show() }
                    imagem_carregada.isEnabled = true
                    buttonAutomatico.isEnabled = true
                    botao_parar.isEnabled = true
                    botaoDesfazer.isEnabled = true
                    imageButtonCamera.isEnabled = true
                    imageButtonGaleria.isEnabled = true
                    botao_gerar_tabela.isEnabled = true
                    salvandoBotao = false
                }
            }
        }

        fun onProgressUpdate(valor: Int) {
            handler.post {
                val msg = when(valor) {
                    1 -> getString(R.string.db_aguardar_gravacao)
                    2 -> getString(R.string.db_salvo_sucesso)
                    3 -> getString(R.string.db_erro_tabela_branco)
                    else -> getString(R.string.db_erro_tabela_atualizacao)
                }
                ToastPersonalizado(requireActivity(), msg, if(valor>=4) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun desenharXmax(ponto: Point) {
        if (ponto.x != Int.MAX_VALUE) {
            @Suppress("DEPRECATION")
            imagem_carregada.isDrawingCacheEnabled = true
            @Suppress("DEPRECATION")
            bitmap_original = imagem_carregada.drawingCache
            val original = bitmap_original ?: return
            val bitmap_novo = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap_novo)
            canvas.drawBitmap(original, 0f, 0f, null)
            p_origem_sistema?.let { origin ->
                canvas.drawLine(origin.x.toFloat(), origin.y.toFloat(), ponto.x.toFloat(), origin.y.toFloat(), paint_externoL!!)
                canvas.drawLine(origin.x.toFloat(), origin.y.toFloat(), ponto.x.toFloat(), origin.y.toFloat(), paint_eixo!!)
            }
            imagem_carregada.setImageBitmap(bitmap_novo)
            @Suppress("DEPRECATION")
            imagem_carregada.isDrawingCacheEnabled = false
        }
    }

    private fun desenharYmax(ponto: Point) {
        if (ponto.x != Int.MAX_VALUE) {
            @Suppress("DEPRECATION")
            imagem_carregada.isDrawingCacheEnabled = true
            @Suppress("DEPRECATION")
            bitmap_original = imagem_carregada.drawingCache
            val original = bitmap_original ?: return
            val bitmap_novo = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap_novo)
            canvas.drawBitmap(original, 0f, 0f, null)
            p_origem_sistema?.let { origin ->
                canvas.drawLine(origin.x.toFloat(), origin.y.toFloat(), origin.x.toFloat(), ponto.y.toFloat(), paint_externoL!!)
                canvas.drawLine(origin.x.toFloat(), origin.y.toFloat(), origin.x.toFloat(), ponto.y.toFloat(), paint_eixo!!)
            }
            imagem_carregada.setImageBitmap(bitmap_novo)
            @Suppress("DEPRECATION")
            imagem_carregada.isDrawingCacheEnabled = false
        }
    }

    private fun desenharPontosCurva(pontos: ArrayList<Point>) {
        if (pontos.isNotEmpty()) {
            @Suppress("DEPRECATION")
            imagem_carregada.isDrawingCacheEnabled = true
            @Suppress("DEPRECATION")
            bitmap_original = imagem_carregada.drawingCache
            val original = bitmap_original ?: return
            val bitmap_novo = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap_novo)
            canvas.drawBitmap(original, 0f, 0f, null)
            for (p in pontos) {
                canvas.drawCircle(p.x.toFloat(), p.y.toFloat(), (Constantes.tamanhoPontos + Constantes.tamanhoPontos / 5f), paint_externo!!)
                canvas.drawCircle(p.x.toFloat(), p.y.toFloat(), Constantes.tamanhoPontos.toFloat(), paint_curva!!)
            }
            imagem_carregada.setImageBitmap(bitmap_novo)
            @Suppress("DEPRECATION")
            imagem_carregada.isDrawingCacheEnabled = false
        }
    }

    private fun desenharOrigemEixos(ponto: Point) {
        if (ponto.x != Int.MAX_VALUE) {
            @Suppress("DEPRECATION")
            imagem_carregada.isDrawingCacheEnabled = true
            @Suppress("DEPRECATION")
            bitmap_original = imagem_carregada.drawingCache
            val original = bitmap_original ?: return
            val bitmap_novo = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap_novo)
            canvas.drawBitmap(original, 0f, 0f, null)
            
            val dp10 = converterDpEmPx(densidadeDisplay, 10f)
            val dp30 = converterDpEmPx(densidadeDisplay, 30f)
            
            canvas.drawLine(ponto.x.toFloat(), (ponto.y + dp10), ponto.x.toFloat(), (ponto.y - dp30), paint_externoL!!)
            canvas.drawLine(ponto.x.toFloat(), (ponto.y + dp10), ponto.x.toFloat(), (ponto.y - dp30), paint_eixo!!)
            canvas.drawLine((ponto.x - dp10), ponto.y.toFloat(), (ponto.x + dp30), ponto.y.toFloat(), paint_externoL!!)
            canvas.drawLine((ponto.x - dp10), ponto.y.toFloat(), (ponto.x + dp30), ponto.y.toFloat(), paint_eixo!!)

            imagem_carregada.setImageBitmap(bitmap_novo)
            @Suppress("DEPRECATION")
            imagem_carregada.isDrawingCacheEnabled = false
        }
    }

    private fun converterDpEmPx(densidade: Float, dp: Float) = dp * densidade

    companion object {
        private const val SELECAO_CAMERA = 2
        private const val SELECAO_GALERIA = 3
        private val pontos_curva_sistema = ArrayList<Point>()
        private val pontos_curva_sistema_float = ArrayList<PontoFloat>()
    }
}
