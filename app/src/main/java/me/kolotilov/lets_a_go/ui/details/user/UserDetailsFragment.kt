package me.kolotilov.lets_a_go.ui.details.user

import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.details.UserDetailsViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import me.kolotilov.lets_a_go.ui.base.EntriesView
import me.kolotilov.lets_a_go.ui.details.*
import me.kolotilov.lets_a_go.utils.smartSetText
import org.kodein.di.instance

class UserDetailsFragment : BaseFragment(R.layout.fragment_user_details) {

    override val viewModel: UserDetailsViewModel by instance()

    override val toolbar: Toolbar by lazyView(R.id.toolbar)
    private val usernameTextView: TextView by lazyView(R.id.username_text_view)
    private val baseDetailsView: BaseDetailsView by lazyView(R.id.base_details_view)
    private val illnessesView: IllnessesView by lazyView(R.id.illnesses_view)
    private val symptomsView: SymptomsView by lazyView(R.id.symptoms_view)
    private val filterView: FilterView by lazyView(R.id.filter_view)
    private val routesHint: TextView by lazyView(R.id.routes_hint_text_view)
    private val routesView: RoutesView by lazyView(R.id.routes_view)
    private val entriesHint: TextView by lazyView(R.id.entries_hint_text_view)
    private val entriesView: EntriesView by lazyView(R.id.entries_view)
    private val logOutButton: Button by lazyView(R.id.log_out_button)

    override fun bind() {
        logOutButton.setOnClickListener { viewModel.logOut() }
        baseDetailsView.setOnClickListener { viewModel.editBasicInfo() }
        illnessesView.setOnClickListener { viewModel.editIllnesses() }
        symptomsView.setOnClickListener { viewModel.editSymptoms() }
        filterView.setOnClickListener { viewModel.editFilter() }
        routesView.setOnRouteClickListener { viewModel.openRoute(it.id) }
        entriesView.setOnEntryClickListener { viewModel.openEntry(it.id, it.routeId) }
    }

    override fun subscribe() {
        viewModel.userDetails.subscribe {
            usernameTextView smartSetText it.username
            baseDetailsView.setData(
                name = it.name,
                age = it.age,
                height = it.height,
                weight = it.weight,
            )
            illnessesView.setItems(it.illnesses)
            symptomsView.setItems(it.symptoms)
            val filter = it.filter
            filterView.setItems(
                filter.length,
                filter.duration,
                filter.typesAllowed,
                filter.groundsAllowed,
                filter.enabled
            )

            routesHint.isVisible = it.routes.isNotEmpty()
            routesView.isVisible = it.routes.isNotEmpty()
            routesView.setItems(
                routes = it.routes
            )

            entriesHint.isVisible = it.entries.isNotEmpty()
            entriesView.isVisible = it.entries.isNotEmpty()
            entriesView.setItems(
                totalDistance = it.totalDistance,
                totalKilocaloriesBurnt = it.totalKilocaloriesBurnt,
                entries = it.entries
            )
        }.autoDispose()
    }
}