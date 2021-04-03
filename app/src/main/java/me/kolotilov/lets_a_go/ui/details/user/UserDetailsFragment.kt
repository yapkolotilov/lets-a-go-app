package me.kolotilov.lets_a_go.ui.details.user

import android.widget.Button
import android.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.details.UserDetailsViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.Recycler
import org.kodein.di.instance

class UserDetailsFragment : BaseFragment(R.layout.fragment_user_details) {

    override val viewModel: UserDetailsViewModel by instance()
    private val baseDetailsGrid: GridLayout by lazyView(R.id.base_details_grid)
    private val illnessesRecycler: RecyclerView by lazyView(R.id.illnesses_recycler)
    private val symptomsRecycler: RecyclerView by lazyView(R.id.symptoms_recycler)
    private val editDetailsButton: Button by lazyView(R.id.edit_details_button)
    private val logOutButton: Button by lazyView(R.id.log_out_button)

    private lateinit var baseDetailsAdapter: Grid.Adapter
    private lateinit var illnessesAdapter: Recycler.Adapter<String>
    private lateinit var symptomsAdapter: Recycler.Adapter<String>

    override fun fillViews() {
        baseDetailsAdapter = Grid.ListAdapter(baseDetailsGrid, BaseDetailsFactory())
        illnessesAdapter = Recycler.Adapter(IllnessFactory())
        illnessesRecycler.adapter = illnessesAdapter

        symptomsAdapter = Recycler.Adapter(SymptomsFactory())
        symptomsRecycler.adapter = symptomsAdapter
    }

    override fun bind() {
        logOutButton.setOnClickListener { viewModel.logOut() }
    }

    override fun subscribe() {
        viewModel.userDetails.subscribe {
            baseDetailsAdapter.items = listOf(
                BaseDetailsViewModel("ФИО", it.name),
                BaseDetailsViewModel("Возраст", it.age.toString()),
                BaseDetailsViewModel("Рост", it.height.toString()),
                BaseDetailsViewModel("Вес", it.weight.toString())
            )
            illnessesAdapter.items = it.illnesses
            symptomsAdapter.items = it.symptoms
        }.autoDispose()
    }
}