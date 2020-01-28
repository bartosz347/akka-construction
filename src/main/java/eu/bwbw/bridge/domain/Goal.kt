package eu.bwbw.bridge.domain

data class Goal(
        val name: String,
        val instance: String? = null
) {
    val isSingeInstance: Boolean
        get() = instance == null
}