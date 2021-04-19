package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Single
import me.kolotilov.lets_a_go.models.Symptom
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.presentation.base.PermissionService
import ru.terrakok.cicerone.Router

class ChooseSymptomsViewModel(
    private val repository: Repository,
    private val router: Router,
    private val permissionService: PermissionService
) : BaseChooseViewModel<Symptom>() {

    override fun loadItems(): Single<List<String>> {
        return repository.getIllnessesAndSymptoms()
            .map { it.symptoms }
    }

    override fun loadSelectedItems(): Single<List<Symptom>> {
        return repository.getDetails()
            .map { it.symptoms }
    }

    override fun next() {
        repository.editDetails(
            symptoms = selectedCache.map { it.name },
            updateFilter = true
        )
            .load()
            .doOnSuccess {
                if (permissionService.isLocationEnabled())
                    router.navigateTo(Screens.onboardingEnd())
                else
                    router.navigateTo(Screens.permission())
            }
            .emptySubscribe()
            .autoDispose()
    }

    override fun performSave(updateFilter: Boolean) {
        repository.editDetails(
            symptoms = selectedCache.map { it.name },
            updateFilter = updateFilter
        )
            .load()
            .doOnSuccess {
                router.exit()
            }
            .emptySubscribe()
            .autoDispose()
    }

    override fun String.toItem() = Symptom(this)

    override fun Symptom.name() = name
}