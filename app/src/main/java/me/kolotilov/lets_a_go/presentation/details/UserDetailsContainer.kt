package me.kolotilov.lets_a_go.presentation.details

class UserDetailsContainer {

    var name: String? = ""
    var age: Int? = null
    var height: Int? = null
    var weight: Int? = null
    var illnesses: List<String>? = null
    var symptoms: List<String>? = null

    fun clear() {
        name = ""
        age = null
        height = null
        weight = null
        illnesses = null
        symptoms = null
    }
}