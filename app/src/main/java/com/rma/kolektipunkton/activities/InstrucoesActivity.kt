package com.rma.kolektipunkton.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.rma.kolektipunkton.R
import com.rma.kolektipunkton.databinding.ActivityInstrucoesBinding

class InstrucoesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInstrucoesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInstrucoesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.appbarToolbar.toolbar2)
        binding.appbarToolbar.toolbar2.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        carregarImagens()
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
