package me.kolotilov.lets_a_go.ui.base

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Delegate {

    fun clear()
}

class ViewDelegate<T : View>(
    @IdRes
    private val id: Int
) : ReadOnlyProperty<Fragment, T>, Delegate {

    private var valueCache: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val value = valueCache
        if (value != null)
            return value
        valueCache = thisRef.requireView().findViewById<T>(id)
        return valueCache!!
    }

    override fun clear() {
        valueCache = null
    }
}

class PropertyDelegate<T>(
    private val factory: () -> T
) : ReadOnlyProperty<Fragment, T>, Delegate {

    private var valueCache: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val value = valueCache
        if (value != null)
            return value
        valueCache = factory()
        return valueCache!!
    }

    override fun clear() {
        valueCache = null
    }
}