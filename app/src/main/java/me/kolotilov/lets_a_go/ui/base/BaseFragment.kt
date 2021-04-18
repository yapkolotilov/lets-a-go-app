package me.kolotilov.lets_a_go.ui.base

import android.animation.LayoutTransition
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.utils.castTo
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI

/**
 * Базовый фрагмент.
 */
abstract class BaseFragment(
    @LayoutRes layoutRes: Int
) : Fragment(layoutRes), DIAware {

    private val viewDisposable = CompositeDisposable()
    private val fragmentDisposable = CompositeDisposable()
    private val delegates = mutableListOf<Delegate>()

    override val di: DI by closestDI()
    protected open val toolbar: Toolbar? = null
    protected var animateLayoutChanges: Boolean = false
        set(value) {
            if (field == value)
                return
            field = value
            if (value)
                view?.castTo<ViewGroup>()?.layoutTransition = LayoutTransition().apply {
                    enableTransitionType(LayoutTransition.CHANGING)
                    enableTransitionType(LayoutTransition.APPEARING)
//                    enableTransitionType(LayoutTransition.DISAPPEARING)
//                    enableTransitionType(LayoutTransition.CHANGE_APPEARING)
//                    enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)
                }
        }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defaultFillViews()
        fillViews()
        bind()
        defaultSubscribe()
        subscribe()
        if (arguments != null)
            requireArguments().readArguments()
        viewModel.attach()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        viewDisposable.clear()
        delegates.forEach { it.clear() }
        viewModel.detach()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        fragmentDisposable.clear()
    }

    /**
     * Вьюмодель.
     */
    protected abstract val viewModel: BaseViewModel

    /**
     * Читает аргументы.
     */
    protected open fun Bundle.readArguments() {}

    /**
     * Наполнение вьюшек.
     */
    protected open fun fillViews() = Unit

    /**
     * Проталкивает события UI во ViewModel.
     */
    protected open fun bind() = Unit

    /**
     * Подписывается на изменения ViewModel.
     */
    protected open fun subscribe() = Unit

    private fun defaultFillViews() {
        if (toolbar != null) {
            toolbar?.setNavigationIcon(R.drawable.ic_back_button)
            requireActivity().castTo<AppCompatActivity>().let { activity ->
                activity.setSupportActionBar(toolbar)
                activity.supportActionBar!!.apply {
                    setDisplayHomeAsUpEnabled(true)
                    setDisplayShowHomeEnabled(true)
                }
            }
        }
    }

    private fun defaultSubscribe() {
        viewModel.popup.subscribe {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }.autoDispose()
    }

    protected fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    //region Расширения

    /**
     * Ленивое свойство.
     *
     * @param factory Инициализатор.
     */
    protected fun <T> lazyProperty(factory: () -> T) : PropertyDelegate<T> {
        val delegate = PropertyDelegate(factory)
        delegates.add(delegate)
        return delegate
    }

    /**
     * Ищет View по ID.
     *
     * @param id ID.
     */
    protected fun <T : View> lazyView(@IdRes id: Int): ViewDelegate<T> {
        val delegate = ViewDelegate<T>(id)
        delegates.add(delegate)
        return delegate
    }

    /**
     * Автоматически останавливает подписку при уничтожении вьюшки.
     */
    protected fun Disposable.autoDispose() {
        viewDisposable.add(this)
    }

    /**
     * Автоматически останавливает подписку при уничтожении фрагмента.
     */
    protected fun Disposable.disposeOnDestroy() {
        fragmentDisposable.add(this)
    }

    protected fun <T> Observable<T>.emptySubscribe(): Disposable {
        return subscribe({}, {
            Log.e("ERROR", it.toString())
        })
    }

    protected fun <T> Single<T>.emptySubscribe(): Disposable {
        return subscribe({}, {
            Log.e("ERROR", it.toString())
        })
    }

    protected fun Completable.emptySubscribe(): Disposable {
        return subscribe({}, {
            Log.e("ERROR", it.toString())
        })
    }
    //endregion
}