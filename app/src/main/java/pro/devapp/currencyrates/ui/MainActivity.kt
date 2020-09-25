package pro.devapp.currencyrates.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pro.devapp.currencyrates.R
import pro.devapp.currencyrates.ui.rates.RatesFragment
import pro.devapp.currencyrates.ui.splash.SplashFragment

class MainActivity : AppCompatActivity() {
    private val viewMode by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, SplashFragment.newInstance())
            .commit()

        viewMode.showMainApp {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, RatesFragment.newInstance())
                .commit()
        }
    }
}