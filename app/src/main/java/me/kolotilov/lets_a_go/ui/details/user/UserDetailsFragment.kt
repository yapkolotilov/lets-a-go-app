package me.kolotilov.lets_a_go.ui.details.user

import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.details.UserDetailsViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.Recycler
import me.kolotilov.lets_a_go.ui.details.BaseDetailsView
import org.kodein.di.instance
import java.text.SimpleDateFormat

class UserDetailsFragment : BaseFragment(R.layout.fragment_user_details) {

    override val viewModel: UserDetailsViewModel by instance()

    override val toolbar: Toolbar by lazyView(R.id.toolbar)
    private val usernameTextView: TextView by lazyView(R.id.username_text_view)
    private val baseDetailsView: BaseDetailsView by lazyView(R.id.base_details_view)
    private val illnessesRecycler: RecyclerView by lazyView(R.id.illnesses_recycler)
    private val symptomsRecycler: RecyclerView by lazyView(R.id.symptoms_recycler)
    private val editDetailsButton: Button by lazyView(R.id.edit_details_button)
    private val filterGrid: GridLayout by lazyView(R.id.filter_grid)
    private val logOutButton: Button by lazyView(R.id.log_out_button)

    private lateinit var filterAdapter: Grid.ListAdapter
    private lateinit var illnessesAdapter: Recycler.Adapter<String>
    private lateinit var symptomsAdapter: Recycler.Adapter<String>

    override fun fillViews() {
        illnessesAdapter = Recycler.Adapter(IllnessFactory())
        illnessesRecycler.adapter = illnessesAdapter
        filterAdapter = Grid.ListAdapter(filterGrid, BaseDetailsFactory())

        symptomsAdapter = Recycler.Adapter(SymptomsFactory())
        symptomsRecycler.adapter = symptomsAdapter
    }

    override fun bind() {
        logOutButton.setOnClickListener { viewModel.logOut() }
    }

    override fun subscribe() {
        viewModel.userDetails.subscribe {
            usernameTextView.text = it.username
            baseDetailsView.setData(
                name = it.name,
                age = it.age,
                height = it.height,
                weight = it.weight,
            )
            illnessesAdapter.items = it.illnesses
            symptomsAdapter.items = it.symptoms
            filterAdapter.items = listOf(
                BaseDetailsViewModel("Длина", (it.filter.maxLength ?: 0.0 / 1000).toString() + " км."),
                BaseDetailsViewModel("Продолжительность", (SimpleDateFormat("HH:mm:ss").format(it.filter.maxDuration?.millis ?: 0)))
            )
        }.autoDispose()
    }
}