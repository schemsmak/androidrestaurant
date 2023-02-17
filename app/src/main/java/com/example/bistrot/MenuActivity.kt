package com.example.bistrot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bistrot.databinding.ActivityMenuBinding
import com.example.bistrot.network.Item
import com.example.bistrot.network.MenuResult
import com.example.bistrot.network.NetworkConstant
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.io.File

enum class Category { ENTREE, PLAT, DESSERT }

class MenuActivity: AppCompatActivity() {
    companion object {
        const val extraKey = "extraKey"
    }
    private lateinit var categorie: String
    private lateinit var binding: ActivityMenuBinding
    lateinit var currentCategory: Category

    override fun onCreate(savedInstanceState: Bundle?) {

        categorie = intent.getStringExtra("category").toString()
        title = categorie
        super.onCreate(savedInstanceState)
        binding= ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title.text = categorie
        binding.toolbar.panier.setOnClickListener {
            startActivity(Intent(this, PanierActivity::class.java))
        }
        refreshPanier()

        val category = intent.getSerializableExtra(extraKey) as? Category
        currentCategory= category ?: Category.ENTREE
        supportActionBar?.title = categoryName(category ?: Category.ENTREE)
        //si la category est null {category=Starter}
        makeRequest()

    }

    private fun makeRequest(){
        val queue = Volley.newRequestQueue(this)
        val params = JSONObject()
        params.put(NetworkConstant.idShopKey, 1)
        val request =  JsonObjectRequest(
            Request.Method.POST,
            NetworkConstant.url,
            params,
            { result ->
                Log.d("request", result.toString(2))
                parseData(result.toString())
            },
            { error ->
                Log.e("request", error.toString())
            }
        )
        queue.add(request)
    }
    private fun parseData(data: String){
        // data: "{"data":[{"name":"Entrées","items":[{"name":"Macedoine","price":5.5},{"name":"Oeuf mimosa","price":5.5},{"name":"Burrata","price":5.5}]},{"name":"Plats","items":[{"name":"Pâtes","price":5.5},{"name":"Poisson","price":5.5},{"name":"Viande","price":5.5}]},{"name":"Desserts","items":[{"name":"Tiramisu","price":5.5},{"name":"Crème brûlée","price":5.5},{"name":"Mousse au chocolat","price":5.5}]}]}"
        val result = GsonBuilder().create().fromJson(data, MenuResult::class.java)
        val category= result.data.first{ it.name == categoryFilterKey()}
        showDatas(category)
    }

    private fun showDatas(category: com.example.bistrot.network.Category) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter =
            CustomAdapter(category.items) {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra(DetailActivity.PLATE_EXTRA, it)
                startActivity(intent)
            }
    }

    private fun categoryFilterKey(): String {
        return when(currentCategory) {
            Category.ENTREE -> "Entrées"
            Category.PLAT -> "Plats"
            Category.DESSERT -> "Desserts"
        }
    }


    private fun categoryName(category: Category): String {
        return when(category) {
            Category.ENTREE -> getString(R.string.buttonStart)
            Category.PLAT -> getString(R.string.buttonMain)
            Category.DESSERT -> getString(R.string.buttonFinish)
        }
    }

    private fun refreshPanier() {
        val file = File(this.filesDir, "panier.json")
        if (file.exists()) {
            val json = file.readText()
            val panier = Gson().fromJson(json, Array<Item>::class.java)
            if(panier.isNotEmpty()) {
                binding.toolbar.pastille.visibility = View.VISIBLE
            }else{
                binding.toolbar.pastille.visibility = View.GONE
            }
            binding.toolbar.pastille.text = panier.size.toString()
        }else{
            binding.toolbar.pastille.visibility = View.GONE
        }
    }
}


