package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Single
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.Screens
import ru.terrakok.cicerone.Router

class ChooseIllnessesViewModel(
    private val repository: Repository,
    private val router: Router,
    private val container: UserDetailsContainer
) : BaseChooseViewModel() {

    override fun loadItems(): Single<List<String>> {
        return repository.getIllnessesAndSymptoms()
            .map { it.illnesses }
    }

    override fun next() {
        container.illnesses = selectedCache.map { it.name }
        router.navigateTo(Screens.ChooseSymptomsScreen)
    }
}