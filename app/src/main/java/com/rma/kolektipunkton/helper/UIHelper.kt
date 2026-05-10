package com.rma.kolektipunkton.helper

import android.graphics.Color
import android.view.View
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Configura o modo Edge-to-Edge e aplica paddings automáticos para as barras do sistema.
 * @param root O layout raiz que receberá o padding inferior (Navigation Bar)
 * @param topView O componente que receberá o padding superior (geralmente o AppBarLayout)
 */

fun Window.setupEdgeToEdge(root: View, topView: View? = null) {
    // Ativa o modo de desenho atrás das barras do sistema
    WindowCompat.setDecorFitsSystemWindows(this, false)

    // Garante que as barras do sistema sejam transparentes
    statusBarColor = Color.TRANSPARENT
    navigationBarColor = Color.TRANSPARENT

    // Aplica Insets (espaçamentos)
    ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val imeVisible = insets.getInsets(WindowInsetsCompat.Type.ime())

        // Aplica padding no topo apenas se uma View de topo for passada
        topView?.updatePadding(top = systemBars.top)

        // Aplica padding embaixo no layout raiz para não cobrir o conteúdo (barra de navegação ou teclado)
        val bottomPadding = if (imeVisible.bottom > 0) imeVisible.bottom else systemBars.bottom
        root.updatePadding(bottom = bottomPadding)

        insets
    }
}