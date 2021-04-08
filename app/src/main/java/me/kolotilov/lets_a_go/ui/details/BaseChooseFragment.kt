package me.kolotilov.lets_a_go.ui.details

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.base.ButtonData
import me.kolotilov.lets_a_go.presentation.base.showDialog
import me.kolotilov.lets_a_go.presentation.details.BaseChooseViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.base.Recycler

abstract class BaseChooseFragment<T> : BaseFragment(R.layout.fragment_choose_illnesses) {

    companion object {

        const val TYPE = "TYPE"
    }

    abstract override val viewModel: BaseChooseViewModel<T>

    override val toolbar: Toolbar by lazyView(R.id.toolbar)
    private val searchView: SearchView by lazyView(R.id.search_illness_view)
    private val recycler: RecyclerView by lazyView(R.id.recycler)
    private val nextButton: Button by lazyView(R.id.next_button)
    private val saveButton: Button by lazyView(R.id.save_button)

    private lateinit var adapter: Recycler.Adapter<T>

    protected abstract val title: String
    protected abstract val hint: String

    protected abstract fun getFactory(): ChooseItemFactory<T>

    protected abstract fun String.toItem(): T

    override fun Bundle.readArguments() {
        val typeArg = getInt(TYPE, -1)
        val type = EditDetailsType.values().first { it.ordinal == typeArg }
        val onboarding = type == EditDetailsType.ONBOARDING
        nextButton.isVisible = onboarding
        saveButton.isVisible = !onboarding
    }

    override fun fillViews() {
        val delegate = object : Recycler.Delegate<T> {
            override fun onClick(item: T) {
                viewModel.select(item)
            }
        }
        adapter = Recycler.Adapter(getFactory(), delegate)
        recycler.adapter = adapter
        toolbar.title = title
        searchView.queryHint = hint
    }

    override fun bind() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.search(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String) = true
        })
        searchView.setOnCloseListener {
            viewModel.search("")
            true
        }

        nextButton.setOnClickListener { viewModel.next() }
        saveButton.setOnClickListener { viewModel.save() }
    }

    override fun subscribe() {
        viewModel.items.subscribe {
            adapter.items = it
        }.autoDispose()

        viewModel.selectedItems.subscribe {
            adapter.selectedItems = it
        }.autoDispose()

        viewModel.updateFilterDialog.subscribe {
            showDialog(
                title = getString(R.string.update_filter_title),
                message = getString(R.string.update_filter_message),
                positiveButton = ButtonData(getString(R.string.yes_button)) {
                    viewModel.performSave(true)
                },
                negativeButton = ButtonData(getString(R.string.no_button)) {
                    viewModel.performSave(false)
                }
            )
        }.autoDispose()
    }
}

abstract class ChooseItemFactory<T> : Recycler.Factory<T> {

    override fun getType(item: T): Int = R.layout.illness_item_approved
}

abstract class ChooseItemViewHolder<T : Any>(itemView: View, delegate: Recycler.Delegate<T>) :
    Recycler.ViewHolder<T>(itemView, delegate) {

    private val titleView: TextView = itemView.findViewById(R.id.name_text_view)
    private val checkedImageView: View = itemView.findViewById(R.id.checked_image_view)

    protected abstract fun getName(item: T): String

    override fun bind(item: T, selected: Boolean) {
        titleView.text = getName(item)
        checkedImageView.isVisible = selected
    }
}
