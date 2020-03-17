package io.slezica.pickone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.slezica.pickone.anidemo.AnidemoComponent
import io.slezica.pickone.component.PickerComponent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PickerComponent().toFragment())
            .commit()
    }
}
