package io.slezica.androidexperiments.components

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import io.slezica.pickone.arch.ComponentFragment
import java.util.*

abstract class Component<T: ViewDataBinding> {

    companion object {
        private val idToComponent = mutableMapOf<String, Component<*>>()

        fun <T: Component<*>> register(component: T) {
            check(!idToComponent.contains(component.id))
            idToComponent[component.id] = component
        }

        fun <T: Component<*>> get(id: String): T {
            check(idToComponent.contains(id))
            return idToComponent[id] as T
        }

        fun <T: Component<*>>  unregister(component: T) {
            check(idToComponent.contains(component.id))
            idToComponent.remove(component.id)
        }
    }

    val id = UUID.randomUUID().toString()

    internal var viewOrNull: ViewDataBinding? = null
    internal var contextOrNull: Context? = null

    @Suppress("UNCHECKED_CAST")
    var ui
        get() = checkNotNull(viewOrNull) as T
        set(value) { viewOrNull = value }

    var context
        get() = checkNotNull(contextOrNull)
        set(value) { contextOrNull = value }

    abstract fun createView(inflater: LayoutInflater): ViewDataBinding


    open fun onCreate() {}
    open fun onCreateView() {}
    open fun onShow() {}
    open fun onHide() {}
    open fun onDestroyView() {}
    open fun onDestroy() {}


    fun toFragment() =
        ComponentFragment(this)
}
