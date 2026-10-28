package md.ceiti.ecosort.data

import md.ceiti.ecosort.R

/** All demo data lives here. There is no backend: every change stays in memory for the session. */
object MockData {

    const val DEMO_PASSWORD = "parola123"

    val users = listOf(
        User(username = "sofer1", name = "Ion Popescu", role = "Șofer colectare"),
        User(username = "sofer2", name = "Vasile Ceban", role = "Șofer colectare"),
    )

    val floors = listOf(
        FloorPlan("Etaj 1", R.drawable.floor_etaj1, 1446f / 1057f),
        FloorPlan("Etaj 2", R.drawable.floor_etaj2, 1504f / 1048f),
        FloorPlan("Etaj 3", R.drawable.floor_etaj3, 1193f / 1015f),
    )

    fun authenticate(username: String, password: String): User? =
        users.firstOrNull { it.username.equals(username.trim(), ignoreCase = true) }
            ?.takeIf { password == DEMO_PASSWORD }

    fun initialBins(now: Long = System.currentTimeMillis()): List<TrashBin> {
        val hour = 60 * 60 * 1000L
        fun bin(id: Int, floor: String, room: String, category: Category, x: Float, y: Float, fill: Int, hoursAgo: Int) =
            TrashBin(
                id, floor, room, category, x, y,
                status = BinStatus.fromFill(fill),
                fillPercentage = fill,
                weightKg = TrashBin.measuredWeight(id, category, fill),
                lastCollectedAt = now - hoursAgo * hour,
            )

        return listOf(
            // Etaj 1
            bin(1, "Etaj 1", "Sala 114", Category.PLASTIC, 0.411f, 0.846f, 100, 52),
            bin(2, "Etaj 1", "Coridor A", Category.PAPER, 0.279f, 0.420f, 100, 46),
            bin(3, "Etaj 1", "Bibliotecă", Category.GLASS, 0.803f, 0.520f, 64, 30),
            bin(4, "Etaj 1", "Bufet", Category.PLASTIC, 0.520f, 0.580f, 55, 20),
            bin(5, "Etaj 1", "Sala Sportivă 137", Category.METAL, 0.725f, 0.170f, 15, 6),
            bin(6, "Etaj 1", "Sala 102", Category.BATTERIES, 0.785f, 0.820f, 10, 70),
            bin(7, "Etaj 1", "Sala 125", Category.PAPER, 0.136f, 0.445f, 20, 9),
            bin(8, "Etaj 1", "Sala 130", Category.GLASS, 0.398f, 0.208f, 5, 3),
            bin(9, "Etaj 1", "Sala 113", Category.METAL, 0.321f, 0.735f, 25, 12),
            // Etaj 2
            bin(10, "Etaj 2", "Sala de Festivități", Category.BATTERIES, 0.806f, 0.660f, 100, 96),
            bin(11, "Etaj 2", "Sala 210 · Orange", Category.PLASTIC, 0.563f, 0.840f, 68, 26),
            bin(12, "Etaj 2", "Sala 222", Category.PAPER, 0.290f, 0.520f, 50, 22),
            bin(13, "Etaj 2", "Direcțiune", Category.PAPER, 0.541f, 0.525f, 18, 8),
            bin(14, "Etaj 2", "Sala 204", Category.GLASS, 0.790f, 0.820f, 12, 5),
            bin(15, "Etaj 2", "Sala 236", Category.METAL, 0.467f, 0.195f, 8, 4),
            bin(16, "Etaj 2", "Sala 245", Category.PLASTIC, 0.745f, 0.473f, 22, 10),
            bin(17, "Etaj 2", "Sala 231", Category.GLASS, 0.167f, 0.595f, 0, 2),
            // Etaj 3
            bin(18, "Etaj 3", "Sala 304", Category.METAL, 0.246f, 0.565f, 100, 60),
            bin(19, "Etaj 3", "Sala 309", Category.PAPER, 0.322f, 0.352f, 58, 28),
            bin(20, "Etaj 3", "Sala 313", Category.PLASTIC, 0.827f, 0.720f, 14, 7),
            bin(21, "Etaj 3", "Sala 310", Category.BATTERIES, 0.885f, 0.810f, 6, 80),
            bin(22, "Etaj 3", "Sala 311", Category.GLASS, 0.713f, 0.755f, 20, 11),
            bin(23, "Etaj 3", "Sala 301", Category.PLASTIC, 0.320f, 0.625f, 10, 4),
        )
    }
}
