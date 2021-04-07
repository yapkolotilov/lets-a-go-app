package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Single
import me.kolotilov.lets_a_go.models.Illness
import me.kolotilov.lets_a_go.network.Repository
import ru.terrakok.cicerone.Router

class ChooseIllnessesViewModel(
    private val repository: Repository,
    private val router: Router
) : BaseChooseViewModel<Illness>() {

    override fun loadItems(): Single<List<String>> {
        return repository.getIllnessesAndSymptoms()
            .map { it.illnesses }
    }

    override fun next() = Unit

    override fun performSave(updateFilter: Boolean) {
        repository.editDetails(
            illnesses = selectedCache.map { it.name },
            updateFilter = updateFilter
        )
            .load()
            .doOnSuccess {
                router.exit()
            }
            .emptySubscribe()
            .autoDispose()
    }

    override fun String.toItem() = Illness(this)

    override fun Illness.name() = name
}