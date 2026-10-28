package md.ceiti.ecosort

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import md.ceiti.ecosort.data.BinStatus
import md.ceiti.ecosort.data.Category
import md.ceiti.ecosort.data.CollectionRecord
import md.ceiti.ecosort.data.MockData
import md.ceiti.ecosort.data.TrashBin
import md.ceiti.ecosort.data.User
import kotlin.random.Random

data class EcoUiState(
    val user: User? = null,
    val isLoggingIn: Boolean = false,
    val loginError: String? = null,
    /** Bumped on every failed attempt so the login card can replay its shake. */
    val loginErrorCount: Int = 0,
    val bins: List<TrashBin> = emptyList(),
    val history: List<CollectionRecord> = emptyList(),
    val selectedFloor: String = MockData.floors.first().name,
    val onlyFull: Boolean = false,
    val hiddenCategories: Set<Category> = emptySet(),
    val liveSensors: Boolean = false,
) {
    val visibleBins: List<TrashBin>
        get() = bins.filter {
            it.floor == selectedFloor &&
                it.category !in hiddenCategories &&
                (!onlyFull || it.status == BinStatus.FULL)
        }

    fun countOn(floor: String, status: BinStatus) = bins.count { it.floor == floor && it.status == status }
    fun count(status: BinStatus) = bins.count { it.status == status }

    val totalKg: Double get() = history.sumOf { it.kg }
    val kgByCategory: Map<Category, Double>
        get() = Category.entries.associateWith { c -> history.filter { it.category == c }.sumOf { it.kg } }
}

class EcoSortViewModel : ViewModel() {

    private val _state = MutableStateFlow(EcoUiState(bins = MockData.initialBins()))
    val state: StateFlow<EcoUiState> = _state.asStateFlow()

    /** Bin snapshots taken right before a collection, so the snackbar can undo it. */
    private val undoSnapshots = mutableMapOf<Long, TrashBin>()
    private var sensorJob: Job? = null

    // region Auth

    fun login(username: String, password: String) {
        if (_state.value.isLoggingIn) return
        _state.update { it.copy(isLoggingIn = true, loginError = null) }
        viewModelScope.launch {
            delay(650) // simulated round-trip, purely cosmetic
            val user = MockData.authenticate(username, password)
            _state.update {
                if (user != null) {
                    it.copy(user = user, isLoggingIn = false, loginError = null)
                } else {
                    it.copy(
                        isLoggingIn = false,
                        loginError = "Utilizator sau parolă incorectă",
                        loginErrorCount = it.loginErrorCount + 1,
                    )
                }
            }
        }
    }

    fun quickLogin(user: User = MockData.users.first()) = login(user.username, MockData.DEMO_PASSWORD)

    fun clearLoginError() = _state.update { it.copy(loginError = null) }

    fun logout() {
        setLiveSensors(false)
        _state.update { it.copy(user = null, isLoggingIn = false) }
    }

    // endregion

    // region Map filters

    fun selectFloor(floor: String) = _state.update { it.copy(selectedFloor = floor) }

    fun setOnlyFull(value: Boolean) = _state.update { it.copy(onlyFull = value) }

    fun toggleCategory(category: Category) = _state.update {
        val hidden = it.hiddenCategories
        it.copy(hiddenCategories = if (category in hidden) hidden - category else hidden + category)
    }

    // endregion

    // region Collection

    /** Empties a bin; the collected weight is whatever its built-in scale reports. */
    fun collect(binId: Int): CollectionRecord? {
        val current = _state.value
        val user = current.user ?: return null
        val bin = current.bins.firstOrNull { it.id == binId } ?: return null
        val kg = bin.weightKg
        val now = System.currentTimeMillis()
        val record = CollectionRecord(
            id = now,
            binId = bin.id,
            collectorName = user.name,
            room = bin.room,
            floor = bin.floor,
            category = bin.category,
            kg = kg,
            timestamp = now,
        )
        undoSnapshots[record.id] = bin
        _state.update { s ->
            s.copy(
                bins = s.bins.map {
                    if (it.id == binId) it.withFill(0).copy(lastCollectedAt = now) else it
                },
                history = listOf(record) + s.history,
            )
        }
        return record
    }

    fun undoCollect(recordId: Long) {
        val previous = undoSnapshots.remove(recordId) ?: return
        _state.update { s ->
            s.copy(
                bins = s.bins.map { if (it.id == previous.id) previous else it },
                history = s.history.filterNot { it.id == recordId },
            )
        }
    }

    // endregion

    // region Demo tools

    /** Instantly fills a few random bins so the demo can be replayed. */
    fun simulateFill(count: Int = 3) = _state.update { s ->
        val targets = s.bins.filter { it.status != BinStatus.FULL }.shuffled().take(count).map { it.id }.toSet()
        s.copy(bins = s.bins.map { if (it.id in targets) it.withFill(100) else it })
    }

    fun resetDemo() {
        undoSnapshots.clear()
        _state.update {
            it.copy(bins = MockData.initialBins(), history = emptyList(), onlyFull = false, hiddenCategories = emptySet())
        }
    }

    /** Fake IoT sensors: every few seconds a random bin gets fuller (and heavier). */
    fun setLiveSensors(enabled: Boolean) {
        _state.update { it.copy(liveSensors = enabled) }
        sensorJob?.cancel()
        if (!enabled) return
        sensorJob = viewModelScope.launch {
            while (isActive) {
                delay(5_000)
                _state.update { s ->
                    val candidate = s.bins.filter { it.status != BinStatus.FULL }.randomOrNull() ?: return@update s
                    val fill = (candidate.fillPercentage + Random.nextInt(12, 30)).coerceAtMost(100)
                    s.copy(bins = s.bins.map {
                        if (it.id == candidate.id) it.withFill(fill) else it
                    })
                }
            }
        }
    }

    // endregion
}
