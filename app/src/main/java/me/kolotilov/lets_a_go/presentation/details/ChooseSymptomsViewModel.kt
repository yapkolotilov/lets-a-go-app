package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Single
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.Screens
import ru.terrakok.cicerone.Router

class ChooseSymptomsViewModel(
    private val repository: Repository,
    private val router: Router,
    private val container: UserDetailsContainer
) : BaseChooseViewModel() {

    override fun loadItems(): Single<List<String>> {
        return repository.getIllnessesAndSymptoms()
            .map { it.symptoms }
    }

    override fun next() {
        container.symptoms = selectedCache.map { it.name }
        repository.editDetails(
            name = container.name,
            age = container.age,
            height = container.height,
            weight = container.weight,
            illnesses = container.illnesses,
            symptoms = container.symptoms,
            filter = null,
            updateFilter = true
        )
            .load()
            .doOnSuccess {
                router.navigateTo(Screens.userDetails())
            }
            .emptySubscribe()
            .autoDispose()
    }
}