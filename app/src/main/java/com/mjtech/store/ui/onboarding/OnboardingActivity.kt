package com.mjtech.store.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mjtech.store.R
import com.mjtech.store.databinding.ActivityOnboardingBinding
import com.mjtech.store.ui.products.ProductsActivity
import com.mjtech.store.ui.weigh.WeighActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initListeners()
    }

    private fun initListeners() {
        binding.cardWeighDish.setOnClickListener {
            Intent(this, WeighActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.cardComplementaryProducts.setOnClickListener {
            Intent(this, ProductsActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}