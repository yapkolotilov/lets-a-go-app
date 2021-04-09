package me.kolotilov.lets_a_go.ui.base

import android.animation.LayoutTransition
import android.content.DialogInterface
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
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.utils.castTo
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI

abstract class BaseBottomSheetFragment(
    @LayoutRes
    private val layoutRes: Int
) : BottomSheetDialogFragment(), DIAware {

    private val compositeDisposable = CompositeDisposable()
    private val delegates = mutableListOf<ViewDelegate<*>>()

    protected var animateLayoutChanges: Boolean = false
        set(value) {
            field = value
            if (value)
                requireView().castTo<ViewGroup>().layoutTransition = LayoutTransition().apply {
                    enableTransitionType(LayoutTransition.CHANGING)
                }
        }

    override val di: DI by closestDI()

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillViews()
        bind()
        defaultSubscribe()
        subscribe()
        if (arguments != null)
            requireArguments().readArguments()
        viewModel.attach()
    }

    final override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        delegates.forEach { it.dispose() }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.detach()
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
     * Автоматически останавливает подписку.
     */
    protected fun Disposable.autoDispose() {
        compositeDisposable.add(this)
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