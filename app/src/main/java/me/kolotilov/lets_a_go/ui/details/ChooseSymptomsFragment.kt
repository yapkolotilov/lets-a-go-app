package me.kolotilov.lets_a_go.ui.details

import android.view.View
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Symptom
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.details.ChooseSymptomsViewModel
import me.kolotilov.lets_a_go.ui.base.Recycler
import me.kolotilov.lets_a_go.ui.buildArguments
import org.kodein.di.instance

class ChooseSymptomsFragment @Deprecated(Constants.NEW_INSTANCE_MESSAGE) constructor() : BaseChooseFragment<Symptom>() {

    companion object {

        /**
         * Новый фрагмент.
         *
         * @param type Тип.
         */
        @Suppress("DEPRECATION")
        fun newInstance(type: Type, symptoms: List<Symptom>): ChooseSymptomsFragment {
            return ChooseSymptomsFragment().buildArguments {
                putInt(TYPE, type.ordinal)
                putStringArray(ITEMS, symptoms.map { it.name }.toTypedArray())
            }
        }
    }

    override val viewModel: ChooseSymptomsViewModel by instance()
    override val title: String get() = requireContext().getString(R.string.choose_symptoms_title)
    override val hint: String get() = requireContext().getString(R.string.choose_symptoms_hint)

    override fun getFactory(): ChooseItemFactory<Symptom> = ChooseSymptomFactory()

    override fun String.toItem(): Symptom = Symptom(this)
}

private class ChooseSymptomFactory : ChooseItemFactory<Symptom>() {

    override fun getViewHolder(
        type: Int,
        itemView: View,
        delegate: Recycler.Delegate<Symptom>
    ): Recycler.ViewHolder<Symptom> {
        return ChooseSymptomViewHolder(itemView, delegate)
    }
}

private class ChooseSymptomViewHolder(itemView: View, delegate: Recycler.Delegate<Symptom>) :
    ChooseItemViewHolder<Symptom>(itemView, delegate) {

    override fun getName(item: Symptom) = item.name
}