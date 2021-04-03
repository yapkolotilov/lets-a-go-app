package me.kolotilov.lets_a_go.ui.details

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.details.BaseChooseViewModel
import me.kolotilov.lets_a_go.presentation.details.IllnessModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.base.Recycler

abstract class BaseChooseFragment : BaseFragment(R.layout.fragment_choose_illnesses) {

    abstract override val viewModel: BaseChooseViewModel

    private val searchView: SearchView by lazyView(R.id.search_illness_view)
    private val recycler: RecyclerView by lazyView(R.id.recycler)
    private val nextButton: Button by lazyView(R.id.next_button)

    private var adapter = Recycler.Adapter(ChooseIllnessFactory(), Recycler.Delegate.create {
        viewModel.select(it)
    })

    override fun fillViews() {
        val delegate = object : Recycler.Delegate<IllnessModel> {

            override fun onClick(item: IllnessModel) {
                viewModel.select(item)
            }
        }
        adapter = Recycler.Adapter(ChooseIllnessFactory(), delegate)
        recycler.adapter = adapter
    }

    override fun bind() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.search(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })
        searchView.setOnCloseListener {
            viewModel.search("")
            true
        }

        nextButton.setOnClickListener { viewModel.next() }
    }

    override fun subscribe() {
        viewModel.items.subscribe {
            adapter.items = it
        }.autoDispose()

        viewModel.selectedItems.subscribe {
            adapter.selectedItems = it
        }.autoDispose()
    }
}

private class ChooseIllnessFactory : Recycler.Factory<IllnessModel> {

    override fun getType(item: IllnessModel): Int = R.layout.illness_item_approved

    override fun getViewHolder(
        type: Int,
        view: View,
        delegate: Recycler.Delegate<IllnessModel>
    ): Recycler.ViewHolder<IllnessModel> {
        return ChooseIllnessViewHolder(view, delegate)
    }
}

private class ChooseIllnessViewHolder(itemView: View, delegate: Recycler.Delegate<IllnessModel>) :
    Recycler.ViewHolder<IllnessModel>(itemView, delegate) {

    private val titleView: TextView = itemView.findViewById(R.id.name_text_view)
    private val checkedImageView: View = itemView.findViewById(R.id.checked_image_view)

    private lateinit var currentItem: IllnessModel

    init {
        itemView.setOnClickListener {
            delegate.onClick(currentItem)
        }
    }

    override fun bind(item: IllnessModel, selected: Boolean) {
        currentItem = item
        titleView.text = item.name
        checkedImageView.isVisible = selected
    }
}
