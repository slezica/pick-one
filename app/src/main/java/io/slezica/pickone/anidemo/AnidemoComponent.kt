package io.slezica.pickone.anidemo

import android.graphics.drawable.Animatable
import android.view.LayoutInflater
import io.slezica.androidexperiments.components.Component
import io.slezica.pickone.databinding.AnidemoBinding

class AnidemoComponent: Component<AnidemoBinding>() {

    override fun createView(inflater: LayoutInflater) =
        AnidemoBinding.inflate(inflater)

    override fun onCreateView() {
        super.onCreateView()

        ui.parent.setOnClickListener {
            (ui.image.drawable as Animatable).start()
        }
    }
}