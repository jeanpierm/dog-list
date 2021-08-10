package com.example.dog_list

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dog_list.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DogAdapter
    private val dogImages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchDogs.setOnQueryTextListener(this)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = DogAdapter(dogImages)
        binding.recyclerDogs.layoutManager = LinearLayoutManager(this)
        binding.recyclerDogs.adapter = adapter
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // coroutines
    private fun searchByName(query: String) {
        // se ejecuta en un hilo secundario
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getDogsByBreed("$query/images")
            val puppies = call.body()
            runOnUiThread {
                if (!call.isSuccessful) return@runOnUiThread showErrorToast()

                val images = puppies?.images ?: emptyList()
                dogImages.clear()
                dogImages.addAll(images)
                adapter.notifyDataSetChanged()

            }
        }
    }

    private fun showErrorToast() {
        Toast.makeText(this, "Error al obtener imágenes", Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchDogs.windowToken, 0)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            searchByName(query.lowercase())
        }
        hideKeyboard()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean = true
}