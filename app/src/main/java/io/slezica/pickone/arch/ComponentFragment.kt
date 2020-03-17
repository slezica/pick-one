package io.slezica.pickone.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import io.slezica.androidexperiments.components.Component

class ComponentFragment<T: ViewDataBinding>: Fragment {

    lateinit var component: Component<T>

    constructor(): super()

    constructor(component: Component<T>): super() {
        this.component = component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            // First time this is created, `component` should be set by the secondary constructor.
            check(::component.isInitialized)

            Component.register(component)
            component.onCreate()

        } else {
            // We are being recreated, we need to obtain our component from the registry
            check(!::component.isInitialized)

            val componentId = savedInstanceState.getString("componentId")!!
            component = Component.get(componentId)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        component.contextOrNull = context
    }

    override fun onCreateView(inf: LayoutInflater, newParent: ViewGroup?, state: Bundle?): View? {
        val viewBinding = component.createView(inf)

        val oldParent = viewBinding.root.parent as? ViewGroup
        oldParent?.removeView(viewBinding.root)

        component.viewOrNull = viewBinding
        component.onCreateView()

        return viewBinding.root
    }

    override fun onResume() {
        super.onResume()
        component.onShow()
    }

    override fun onPause() {
        super.onPause()
        component.onHide()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("componentId", component.id)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        component.viewOrNull = null
        component.onDestroyView()

        if (isRemoving || activity!!.isFinishing) {
            Component.unregister(component)
            component.onDestroy()
        }
    }
}
