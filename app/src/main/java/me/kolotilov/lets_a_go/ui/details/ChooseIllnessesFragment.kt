package me.kolotilov.lets_a_go.ui.details

import android.view.View
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Illness
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.details.ChooseIllnessesViewModel
import me.kolotilov.lets_a_go.ui.base.Recycler
import me.kolotilov.lets_a_go.ui.buildArguments
import org.kodein.di.instance

class ChooseIllnessesFragment @Deprecated(Constants.NEW_INSTANCE_MESSAGE) constructor() : BaseChooseFragment<Illness>() {

    companion object {

        /**
         * Новый фрагмент.
         *
         * @param type Тип.
         */
        @Suppress("DEPRECATION")
        fun newInstance(type: EditDetailsType): ChooseIllnessesFragment {
            return ChooseIllnessesFragment().buildArguments {
                putSerializable(TYPE, type)
            }
        }
    }

    override val viewModel: ChooseIllnessesViewModel by instance()
    override val title: String get() = requireContext().getString(R.string.choose_illnesses_title)
    override val hint: String get() = requireContext().getString(R.string.choose_illnesses_hint)

    override fun getFactory(): ChooseItemFactory<Illness> = ChooseIllnessFactory()

    override fun String.toItem(): Illness = Illness(this)
}

private class ChooseIllnessFactory : ChooseItemFactory<Illness>() {

    override fun getViewHolder(
        type: Int,
        itemView: View,
        delegate: Recycler.Delegate<Illness>
    ): Recycler.ViewHolder<Illness> {
        return ChooseIllnessViewHolder(itemView, delegate)
    }
}

private class ChooseIllnessViewHolder(itemView: View, delegate: Recycler.Delegate<Illness>) :
    ChooseItemViewHolder<Illness>(itemView, delegate) {

    override fun getName(item: Illness) = item.name
}