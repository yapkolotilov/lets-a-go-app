package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.presentation.BaseViewModel

/**
 * Базовая логика экрана выбора
 */
abstract class BaseChooseViewModel<T> : BaseViewModel() {

    private var itemsCache = mutableListOf<T>()
    protected var selectedCache = mutableSetOf<T>()

    val items: Observable<List<T>> get() = itemsSubject
    private val itemsSubject: Subject<List<T>> = BehaviorSubject.create()

    val selectedItems: Observable<Set<T>> get() = selectedItemsSubject
    private val selectedItemsSubject: Subject<Set<T>> = BehaviorSubject.create()

    /**
     * Приказ показать диалог для обновления деталей.
     */
    val updateFilterDialog: Observable<Unit> get() = updateFilterDialogSubject
    private val updateFilterDialogSubject: Subject<Unit> = PublishSubject.create()

    private var healthUpdated: Boolean = false

    override fun attach() {
        loadItems()
            .map { it.sorted() }
            .load()
            .doOnSuccess {
                itemsCache = it.map { it.toItem() }.toMutableList()
                itemsSubject.onNext(itemsCache)
            }
            .emptySubscribe()
    }

    fun init(selectedItems: List<T>) {
        selectedCache = selectedItems.toMutableSet()
        selectedItemsSubject.onNext(selectedCache)
    }

    fun search(query: String) {
        val foundItems = itemsCache.filter { it.name().contains(query, ignoreCase = true) }
        itemsSubject.onNext(foundItems)
    }

    fun select(item: T) {
        healthUpdated = true
        if (selectedCache.contains(item))
            selectedCache.remove(item)
        else
            selectedCache.add(item)
        selectedItemsSubject.onNext(selectedCache)
    }

    fun save() {
        if (healthUpdated)
            updateFilterDialogSubject.onNext(Unit)
        else
            performSave(false)
    }

    abstract fun next()

    abstract fun performSave(updateFilter: Boolean)

    protected abstract fun loadItems(): Single<List<String>>

    protected abstract fun String.toItem(): T

    protected abstract fun T.name(): String
}