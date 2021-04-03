package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.presentation.BaseViewModel

abstract class BaseChooseViewModel : BaseViewModel() {

    private var itemsCache = mutableListOf<IllnessModel>()
    protected var selectedCache = mutableSetOf<IllnessModel>()

    val items: Observable<List<IllnessModel>> get() = itemsSubject
    private val itemsSubject: Subject<List<IllnessModel>> = BehaviorSubject.create()

    val selectedItems: Observable<Set<IllnessModel>> get() = selectedItemsSubject
    private val selectedItemsSubject: Subject<Set<IllnessModel>> = BehaviorSubject.create()

    override fun attach() {
        loadItems()
            .load()
            .doOnSuccess {
                itemsCache = it.map { it.toApprovedModel() }.toMutableList()
                itemsSubject.onNext(itemsCache)
            }
            .emptySubscribe()
    }

    fun search(query: String) {
        val foundItems = itemsCache.filter { it.name.contains(query, ignoreCase = true) }
        itemsSubject.onNext(foundItems)
    }

    fun select(item: IllnessModel) {
        if (selectedCache.contains(item))
            selectedCache.remove(item)
        else
            selectedCache.add(item)
        selectedItemsSubject.onNext(selectedCache)
    }

    abstract fun next()

    protected abstract fun loadItems(): Single<List<String>>

    private fun String.toApprovedModel() = IllnessModel.Approved(this)
}

sealed class IllnessModel {

    data class Approved(
        override val name: String
    ) : IllnessModel()

    data class Custom(
        override val name: String
    ) : IllnessModel()

    data class New(
        override val name: String
    ) : IllnessModel()

    abstract val name: String
}