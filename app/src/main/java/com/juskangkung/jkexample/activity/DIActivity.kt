package com.juskangkung.jkexample.activity

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.juskangkung.jkexample.R
import dagger.Component
import kotlinx.android.synthetic.main.activity_di.*
import javax.inject.Inject

class DIActivity : AppCompatActivity() {

    @Inject lateinit var info: Info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_di)
        DaggerMagicBox.create().poke(this)
        text_view.text = info.text
    }
}

class Info @Inject constructor() {
    val text = "Yoga Swami Devanada"
}

@Component
interface MagicBox {
    fun poke(app: DIActivity)
}