package me.kolotilov.lets_a_go.ui.details

import android.view.View
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
        fun newInstance(type: Type, illnesses: List<Illness>): ChooseIllnessesFragment {
            return ChooseIllnessesFragment().buildArguments {
                putInt(TYPE, type.ordinal)
                putStringArray(ITEMS, illnesses.map { it.name }.toTypedArray())
            }
        }
    }

    override val viewModel: ChooseIllnessesViewModel by instance()

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