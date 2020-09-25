package pro.devapp.currencyrates.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pro.devapp.currencyrates.R
import pro.devapp.currencyrates.ui.rates.RatesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, RatesFragment.newInstance())
            .commit()

    }
}