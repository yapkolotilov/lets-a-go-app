package me.kolotilov.lets_a_go.presentation.details

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.ui.details.EditDetailsType
import org.joda.time.DateTime
import ru.terrakok.cicerone.Router

/**
 * Логика редактирования базовой информации.
 */
class EditBasicInfoViewModel(
    private val router: Router,
    private val repository: Repository,
) : BaseViewModel() {

    /**
     * Данные для экрана.
     *
     * @param name Название.
     * @param birthDate Дата рождения.
     * @param height Высота (см).
     * @param weight Вес (кг).
     */
    data class Data(
        val name: String?,
        val birthDate: DateTime?,
        val height: Int?,
        val weight: Int?,
    )

    /**
     * Данные для отображения.
     */
    val data: Observable<Data> get() = dataSubject
    private val dataSubject: Subject<Data> = BehaviorSubject.create()

    /**
     * Приказ отобразить DatePickerDialog.
     */
    val datePicker: Observable<DateTime> get() = datePickerSubject
    private val datePickerSubject: Subject<DateTime> = PublishSubject.create()

    /**
     * Приказ отобразить NumberPickerDialog для роста.
     */
    val heightPicker: Observable<Int> get() = heightPickerSubject
    private val heightPickerSubject: Subject<Int> = PublishSubject.create()

    /**
     * Приказ отобразить NumberPickerDialog для веса.
     */
    val weightPicker: Observable<Int> get() = weightPickerSubject
    private val weightPickerSubject: Subject<Int> = PublishSubject.create()

    /**
     * Приказ показать диалог для обновления деталей.
     */
    val updateFilterDialog: Observable<Unit> get() = updateFilterDialogSubject
    private val updateFilterDialogSubject: Subject<Unit> = PublishSubject.create()

    private var name: String? = null
    private var birthDate: DateTime? = null
    private var height: Int? = null
    private var weight: Int? = null
    private var healthUpdated: Boolean = false

    override fun attach() {
        repository.getDetails()
            .load()
            .doOnSuccess {
                init(
                    name = it.name,
                    birthDate = it.birthDate,
                    height = it.height,
                    weight = it.weight
                )
            }
            .emptySubscribe()
            .autoDispose()
    }

    /**
     * Инициализирует вьюмодель.
     *
     * @param name Название.
     * @param birthDate Дата рождения.
     * @param height Высота (см).
     * @param weight Вес (кг).
     */
    private fun init(name: String?, birthDate: DateTime?, height: Int?, weight: Int?) {
        this.name = name
        this.birthDate = birthDate
        this.height = height
        this.weight = weight
        updateData()
    }

    /**
     * Устанавливает значение имени.
     *
     * @param name Имя.
     */
    fun setName(name: String) {
        this.name = name.takeIf { it.isNotEmpty() }
    }

    /**
     * Устанавливает дату рождения.
     *
     * @param date Дата рождения..
     */
    fun setBirthDate(date: DateTime) {
        this.birthDate = date
        healthUpdated = true
        updateData()
    }

    /**
     * Устанавливает рост.
     *
     * @param height Рост.
     */
    fun setHeight(height: Int) {
        this.height = height
        healthUpdated = true
        updateData()
    }

    /**
     * Устанавливает вес.
     *
     * @param weight вес.
     */
    fun setWeight(weight: Int) {
        this.weight = weight
        healthUpdated = true
        updateData()
    }

    /**
     * Открывает редактирование даты рождения.
     */
    fun openBirthDatePicker() {
        datePickerSubject.onNext(birthDate ?: DateTime.now())
    }

    /**
     * Открывает редактирование роста.
     */
    fun openHeightPicker() {
        heightPickerSubject.onNext(height ?: 172)
    }

    /**
     * Открывает редактирование веса.
     */
    fun openWeightPicker() {
        weightPickerSubject.onNext(weight ?: 71)
    }

    /**
     * Сохраняет изменения с запросом на изменения.
     */
    fun save() {
        if (healthUpdated)
            updateFilterDialogSubject.onNext(Unit)
        else
            performSave(false)
    }

    fun next() {
        repository.editDetails(
            name = name,
            birthDate = birthDate,
            height = height,
            weight = weight,
            updateFilter = true,
        ).load()
            .doOnSuccess {
                router.navigateTo(Screens.chooseIllnesses(EditDetailsType.ONBOARDING))
            }
            .emptySubscribe()
            .autoDispose()
    }

    /**
     * Форсированный запрос на изменения.
     */
    fun performSave(updateFilter: Boolean) {
        repository.editDetails(
            name = name,
            birthDate = birthDate,
            height = height,
            weight = weight,
            updateFilter = updateFilter,
        ).load()
            .doOnSuccess {
                router.exit()
            }
            .emptySubscribe()
            .autoDispose()
    }

    private fun updateData() {
        dataSubject.onNext(
            Data(
                name = name,
                birthDate = birthDate,
                height = height,
                weight = weight
            )
        )
    }
}