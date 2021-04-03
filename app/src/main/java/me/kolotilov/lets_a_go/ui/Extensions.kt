package me.kolotilov.lets_a_go.ui

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import io.reactivex.Single
import me.kolotilov.lets_a_go.models.Point

@SuppressLint("MissingPermission")
fun Task<Location>.toSingle(): Single<Location> {
    return Single.create { emitter ->
        addOnSuccessListener {
            if (it != null)
                emitter.onSuccess(it)
            else
                Log.d("BRUH", "location == null")
        }
        addOnFailureListener {
            emitter.onError(it)
        }
    }
}

fun Point.toLatLng() = LatLng(latitude, longitude)

fun Location.toLatLng() = LatLng(latitude, longitude)

val RecyclerView.ViewHolder.context get() = itemView.context