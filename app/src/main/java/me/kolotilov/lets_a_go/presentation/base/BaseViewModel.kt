package me.kolotilov.lets_a_go.presentation

import android.util.Log
import androidx.annotation.CallSuper
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.ErrorCode
import me.kolotilov.lets_a_go.models.ServiceException
import me.kolotilov.lets_a_go.utils.emitNext

/**
 * Базовая вьюмодель.
 */
abstract class BaseViewModel {

    val popup: Observable<String> get() = popupSubject
    private val popupSubject: Subject<String> = PublishSubject.create()

    val errorDialog: Observable<ErrorCode> get() = errorDialogSubject
    private val errorDialogSubject: Subject<ErrorCode> = PublishSubject.create()

    private val compositeDisposable = CompositeDisposable()

    /**
     * Загружает данные для страницы.
     */
    open fun attach() {}

    /**
     * Сбрасывает данные для страницы.
     */
    @CallSuper
    open fun detach() {
        compositeDisposable.clear()
    }

    /**
     * Показывает всплывающее сообщение.
     *
     * @param text Текст.
     */
    protected fun showPopup(text: String?) {
        popupSubject.emitNext(text)
    }

    //region Расширения

    /**
     * Выполняет запрос асинхронно.
     */
    protected fun Completable.schedule(): Completable {
        return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Управляет загрузкой.
     */
    protected fun Completable.load(): Completable {
        return schedule()
            .doOnSubscribe { compositeDisposable.add(it) }
            .doOnError {
                Log.e("NETWORK", it.stackTraceToString())
                if (it is ServiceException)
                    errorDialogSubject.onNext(it.code)
            }
    }

    /**
     * Выполняет запрос асинхронно.
     */
    protected fun <T> Single<T>.schedule(): Single<T> {
        return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Управляет загрузкой.
     */
    protected fun <T> Single<T>.load(): Single<T> {
        return schedule()
            .doOnSubscribe { compositeDisposable.add(it) }
            .doOnError {
                Log.e("NETWORK", it.stackTraceToString())
                if (it is ServiceException)
                    errorDialogSubject.onNext(it.code)
            }
    }

    /**
     * Выполняет запрос асинхронно.
     */
    protected fun <T> Observable<T>.schedule(): Observable<T> {
        return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Управляет загрузкой.
     */
    protected fun <T> Observable<T>.load(): Observable<T> {
        return schedule()
            .doOnSubscribe { compositeDisposable.add(it) }
            .doOnError {
                Log.e("NETWORK", it.stackTraceToString())
                if (it is ServiceException)
                    errorDialogSubject.onNext(it.code)
            }
    }

    /**
     * Автоматически очищает запросы.
     */
    protected fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

    /**
     * Пустой обработчик.
     */
    protected fun Completable.emptySubscribe(): Disposable {
        return subscribe({}, {
            Log.e("ERROR", it.toString())
        })
    }

    /**
     * Пустой обработчик.
     */
    protected fun <T> Single<T>.emptySubscribe(): Disposable {
        return subscribe({}, {
            Log.e("ERROR", it.toString())
        })
    }

    /**
     * Пустой обработчик.
     */
    protected fun <T> Observable<T>.emptySubscribe(): Disposable {
        return subscribe({}, {
            Log.e("ERROR", it.toString())
        })
    }

    //endregion
}

class EmptyViewModel() : BaseViewModel()