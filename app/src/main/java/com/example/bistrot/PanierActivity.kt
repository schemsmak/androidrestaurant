package com.example.bistrot



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bistrot.databinding.ActivityPanierBinding
import com.example.bistrot.network.Item
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.io.File


class PanierActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanierBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panier)

        binding = ActivityPanierBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title.text = "Panier"

        refreshPanier()
        reloadLayout()

        binding.valider.setOnClickListener {
            val file = File(this.filesDir, "panier.json")
            if (file.exists()) {
                val json = file.readText()
                val panier = Gson().fromJson(json, Array<Item>::class.java)
                if(panier.isNotEmpty()) {
                    Snackbar.make(binding.root, "Commande envoyer", Snackbar.LENGTH_SHORT).show()
                    file.delete()
                    refreshPanier()
                    reloadLayout()
                }
            }
        }
    }

    private fun reloadLayout() {
        binding.list.layoutManager = LinearLayoutManager(null)
        val file = File(this.filesDir, "panier.json")
        if (file.exists()) {
            val json = file.readText()
            val panier = Gson().fromJson(json, Array<Item>::class.java)
            binding.list.adapter = PanierAdapter(panier) { target ->
                File(this.filesDir, "panier.json").writeText(Gson().toJson(panier.filter { it !== target }.toTypedArray()))
                refreshPanier()
                reloadLayout()
            }
        }else{
            binding.list.adapter = null
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

    override fun onResume() {
        super.onResume()
        refreshPanier()
    }
}