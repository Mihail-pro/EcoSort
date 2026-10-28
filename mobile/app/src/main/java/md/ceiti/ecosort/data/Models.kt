package md.ceiti.ecosort.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class User(
    val username: String,
    val name: String,
    val role: String,
) {
    val initials: String
        get() = name.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")
    val firstName: String get() = name.substringBefore(" ")
}

/** [capacityKg] = typical weight of a completely full bin of that stream. */
enum class Category(val label: String, val color: Color, val code: String, val capacityKg: Double) {
    PLASTIC("Plastic", Color(0xFF3B82F6), "PL", 3.5),
    PAPER("Hârtie", Color(0xFFFACC15), "HR", 6.0),
    GLASS("Sticlă", Color(0xFF22C55E), "ST", 12.0),
    METAL("Metal", Color(0xFF94A3B8), "MT", 4.5),
    BATTERIES("Baterii", Color(0xFFEF4444), "BT", 2.5),
}

enum class BinStatus(val label: String, val color: Color) {
    FULL("Plin", Color(0xFFEF4444)),
    MEDIUM("Mediu", Color(0xFFF59E0B)),
    EMPTY("Gol", Color(0xFF10B981));

    companion object {
        fun fromFill(fill: Int): BinStatus = when {
            fill >= 90 -> FULL
            fill >= 45 -> MEDIUM
            else -> EMPTY
        }
    }
}

/**
 * A selective-collection bin placed on a floor plan.
 * [xRatio]/[yRatio] are relative (0..1) to the floor plan image, so pins stay
 * glued to the right room at any screen size or zoom level.
 * [weightKg] comes from the bin's built-in load cell, so the worker never types it.
 */
data class TrashBin(
    val id: Int,
    val floor: String,
    val room: String,
    val category: Category,
    val xRatio: Float,
    val yRatio: Float,
    val status: BinStatus,
    val fillPercentage: Int,
    val weightKg: Double,
    val lastCollectedAt: Long,
) {
    val qrCode: String get() = "ECO-${floor.filter(Char::isDigit)}-${id.toString().padStart(3, '0')}-${category.code}"

    /** Simulated sensor reading: fill level and scale weight always change together. */
    fun withFill(fill: Int): TrashBin = copy(
        fillPercentage = fill,
        status = BinStatus.fromFill(fill),
        weightKg = measuredWeight(id, category, fill),
    )

    companion object {
        /** Weight for a fill level, with a stable per-bin density spread (±15%) so readings look real. */
        fun measuredWeight(id: Int, category: Category, fill: Int): Double {
            val density = 0.85 + (id * 37 % 31) / 100.0
            return Math.round(category.capacityKg * density * fill / 100.0 * 10) / 10.0
        }
    }
}

data class CollectionRecord(
    val id: Long,
    val binId: Int,
    val collectorName: String,
    val room: String,
    val floor: String,
    val category: Category,
    val kg: Double,
    val timestamp: Long,
)

data class FloorPlan(
    val name: String,
    @DrawableRes val image: Int,
    /** width / height of the plan image, used to keep pins aligned. */
    val aspectRatio: Float,
)
