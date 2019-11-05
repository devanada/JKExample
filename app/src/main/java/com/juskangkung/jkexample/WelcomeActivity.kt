package com.juskangkung.jkexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.Button
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.juskangkung.jkexample.adapter.MovieAdapter
import com.juskangkung.jkexample.model.Movie
import com.juskangkung.jkexample.model.MovieResponse
import com.juskangkung.jkexample.service.ApiClient
import com.juskangkung.jkexample.service.ApiInterface
import kotlinx.android.synthetic.main.activity_welcome.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WelcomeActivity : AppCompatActivity() {
    private val TAG : String = WelcomeActivity::class.java.canonicalName
    private lateinit var movies : ArrayList<Movie>
    private var btnlogout: Button? = null
    private var preferenceHelper: PreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        setSupportActionBar(toolbar)

        preferenceHelper = PreferenceHelper(this)
        btnlogout = findViewById<View>(R.id.btn) as Button
        btnlogout!!.setOnClickListener {
            preferenceHelper!!.putIsLogin(false)
            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this@WelcomeActivity.finish()
        }

        rvMovies.layoutManager = androidx.recyclerview.widget.GridLayoutManager(applicationContext, 2)

        val apiKey = getString(R.string.api_key)
        val apiInterface : ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        getLatestMovie(apiInterface, apiKey)
        getPopularMovies(apiInterface, apiKey)

        collapseImage.setOnClickListener {
            Toast.makeText(applicationContext, "Poster Gede", Toast.LENGTH_SHORT).show()
        }
    }

    fun getPopularMovies(apiInterface: ApiInterface, apiKey : String) {
        val call : Call<MovieResponse> = apiInterface.getPopularMovie(apiKey)
        call.enqueue(object : Callback<MovieResponse> {
            override fun onFailure(call: Call<MovieResponse>?, t: Throwable?) {
                Log.d("$TAG", "Gagal Fetch Popular Movie")
            }

            override fun onResponse(call: Call<MovieResponse>?, response: Response<MovieResponse>?) {
                movies = response!!.body()!!.results
                Log.d("$TAG", "Movie size ${movies.size}")
                rvMovies.adapter = MovieAdapter(movies)
            }

        })
    }

    fun getLatestMovie(apiInterface: ApiInterface, apiKey : String) : Movie? {
        var movie : Movie? = null
        val call : Call<Movie> = apiInterface.getMovieLatest(apiKey)
        call.enqueue(object : Callback<Movie> {
            override fun onFailure(call: Call<Movie>?, t: Throwable?) {
                Log.d("$TAG", "Gagal Fetch Popular Movie")
            }

            override fun onResponse(call: Call<Movie>?, response: Response<Movie>?) {
                if (response != null) {
                    var originalTitle : String? = response.body()?.originalTitle
                    var posterPath : String? = response.body()?.posterPath

                    collapseToolbar.title = originalTitle
                    if (posterPath == null) {
                        collapseImage.setImageResource(R.drawable.tumblr)
                    } else {
                        val imageUrl = StringBuilder()
                        imageUrl.append(getString(R.string.base_path_poster)).append(posterPath)
                        Glide.with(applicationContext).load(imageUrl.toString()).into(collapseImage)
                    }
                }
            }

        })

        return movie
    }
}