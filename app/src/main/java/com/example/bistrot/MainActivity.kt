package com.example.bistrot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.bistrot.databinding.ActivityMainBinding
import com.example.bistrot.network.Item
import com.google.gson.Gson
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title.text = "home"
        binding.toolbar.panier.setOnClickListener{
            startActivity(Intent(this, PanierActivity::class.java))
        }

        refreshPanier()
        buttonsListener()

    }
    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        refreshPanier()
        Log.d("MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy")
    }

    private fun  buttonsListener() {
        binding.buttonStart.setOnClickListener{
            showCategory(Category.ENTREE)
        }
        binding.buttonMain.setOnClickListener{
            showCategory(Category.PLAT)
        }
        binding.buttonFinish.setOnClickListener{
            showCategory(Category.DESSERT)

        }
    }
    private fun showCategory(category: Category) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra(MenuActivity.extraKey, category)
        startActivity(intent)
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
