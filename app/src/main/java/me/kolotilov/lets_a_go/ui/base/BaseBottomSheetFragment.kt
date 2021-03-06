package me.kolotilov.lets_a_go.ui.base

import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.ui.animateLayoutChanges
import me.kolotilov.lets_a_go.utils.castTo
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.direct
import org.kodein.di.instance
import ru.terrakok.cicerone.Router

abstract class BaseBottomSheetFragment(
    @LayoutRes
    private val layoutRes: Int
) : BottomSheetDialogFragment(), DIAware {

    private val compositeDisposable = CompositeDisposable()
    private val delegates = mutableListOf<ViewDelegate<*>>()

    protected open val peekHeight: Int? = null
    private var behavior: BottomSheetBehavior<FrameLayout>? = null

    protected var animateLayoutChanges: Boolean = false
        set(value) {
            field = value
            if (value)
                requireView().castTo<ViewGroup>().animateLayoutChanges = true
        }

    override val di: DI by closestDI()

    private val router: Router by lazy {
        di.direct.instance()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val touchOutsideView = dialog!!.window
            ?.decorView
            ?.findViewById<View>(R.id.touch_outside)
        touchOutsideView?.setOnClickListener(null)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val d = dialog as BottomSheetDialog

            behavior = d.behavior
            val behavior = behavior!!
            behavior.skipCollapsed = true

            val peekHeight = peekHeight
            if (peekHeight != null) {
                behavior.peekHeight = peekHeight
                behavior.isHideable = false
            }
            router.sendMenuVisibility(false)
        }
        return dialog
    }

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
        delegates.forEach { it.clear() }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        router.sendMenuVisibility(true)
        viewModel.detach()
    }

    protected fun expand() {
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    /**
     * ??????????????????.
     */
    protected abstract val viewModel: BaseViewModel

    /**
     * ???????????? ??????????????????.
     */
    protected open fun Bundle.readArguments() {}

    /**
     * ???????????????????? ????????????.
     */
    protected open fun fillViews() = Unit

    /**
     * ???????????????????????? ?????????????? UI ???? ViewModel.
     */
    protected open fun bind() = Unit

    /**
     * ?????????????????????????? ???? ?????????????????? ViewModel.
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

    //region ????????????????????

    /**
     * ???????? View ???? ID.
     *
     * @param id ID.
     */
    protected fun <T : View> lazyView(@IdRes id: Int): ViewDelegate<T> {
        val delegate = ViewDelegate<T>(id)
        delegates.add(delegate)
        return delegate
    }

    /**
     * ?????????????????????????? ?????????????????????????? ????????????????.
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