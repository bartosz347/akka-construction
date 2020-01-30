package eu.bwbw.bridge.domain

data class Goal(
    val name: String,
    val instance: String? = null
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Goal)
            name == other.name && (instance == other.instance || other.instance == "ANY" || instance == "ANY")
        else super.equals(other)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    val isSingeInstance: Boolean
        get() = instance == null
}