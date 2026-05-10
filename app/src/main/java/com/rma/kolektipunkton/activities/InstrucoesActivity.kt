package com.rma.kolektipunkton.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import coil.load
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.databinding.ActivityInstrucoesBinding
import com.rma.kolektipunkton.helper.setupEdgeToEdge

class InstrucoesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInstrucoesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInstrucoesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // status bar e navigation bar
        val root = findViewById<View>(R.id.instrucoesLayout)
        val appBar = findViewById<View>(R.id.appBarLayout)
        window.setupEdgeToEdge(root, appBar)
        val toolbar = findViewById<Toolbar>(R.id.appBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        carregarImagens()
    }

    override fun onSupportNavigateUp(): Boolean {
        // This tells the activity to go back to the previous screen
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun carregarImagens(){
        val mapeamento = listOf(
            binding.imageViewIntrucoes1 to R.drawable.image1,
            binding.imageViewIntrucoes2 to R.drawable.image2,
            binding.imageViewIntrucoes3 to R.drawable.image3,
            binding.imageViewIntrucoes4 to R.drawable.image4,
            binding.imageViewIntrucoes5 to R.drawable.image5,
            binding.imageViewIntrucoes6 to R.drawable.image6,
            binding.imageViewIntrucoes7 to R.drawable.image7,
            binding.imageViewIntrucoes8 to R.drawable.image8,
            binding.imageViewIntrucoes9 to R.drawable.image9,
            binding.imageViewIntrucoes10 to R.drawable.image10,
            binding.imageViewIntrucoes11 to R.drawable.image11,
            binding.imageViewIntrucoes12 to R.drawable.image12,
            binding.imageViewIntrucoes13 to R.drawable.image13,
            binding.imageViewIntrucoes14 to R.drawable.image14,
            binding.imageViewIntrucoes15 to R.drawable.image15,
            binding.imageViewIntrucoes16 to R.drawable.image16
        )
        mapeamento.forEach { (imageView, resId) ->
            imageView.load(resId)
        }
    }
}
