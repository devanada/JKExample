package com.juskangkung.jkexample.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.juskangkung.jkexample.R
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_debounce.*
import java.util.*
import java.util.concurrent.TimeUnit

class DebounceActivity : AppCompatActivity() {
    private val disposable = CompositeDisposable()
    private val _textInput = BehaviorSubject.create<String>()
    private val textInput = _textInput.toFlowable(BackpressureStrategy.LATEST)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debounce)

        et_input.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                _textInput.onNext(et_input.text.toString())
            }

            override fun beforeTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        disposable.add(
            textInput
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    tv_display.text = tv_display.text.toString() + "\n ${Date()}: " + it
                }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}