package org.example.project.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.text.input.TextFieldValue
import org.example.project.platform.KeyValueStorage
import org.example.project.platform.nowDate
import org.example.project.platform.nowTime

enum class PetType(val emoji: String) {
    Dog("🐶"),
    Cat("🐱"),
    Bird("🐦"),
    Other("🐾"),
}

enum class WeekDay {
    Mon, Tue, Wed, Thu, Fri, Sat, Sun;
}

enum class MealSlot(val emoji: String) {
    Morning("🌅"),
    Day("☀️"),
    Evening("🌙"),
}

data class Pet(
    val id: Long,
    val name: String,
    val type: PetType,
    val breed: String,
    val ageYears: Int,
    val weightGrams: Int,
    val photoPath: String? = null,
)

data class Vaccine(val id: Long, val name: String, val date: String)

data class GameProgress(val seed: Long, val index: Int, val score: Int)

data class PetNote(val id: Long, val date: String, val text: String)

fun formatPetWeight(grams: Int): String {
    if (grams < 1000) return "$grams g"
    val tens = (grams / 100)
    val intKg = tens / 10
    val frac = tens % 10
    return if (frac == 0) "$intKg kg" else "$intKg.$frac kg"
}

fun mealKey(day: WeekDay, slot: MealSlot): String = "${day.name}_${slot.name}"

private const val PETS_KEY = "pets"
private const val CURRENT_INDEX_KEY = "current_index"
private const val LANGUAGE_KEY = "language"
private const val DARK_MODE_KEY = "dark_mode"
private const val LEGACY_PET_KEY = "pet"
private const val LEGACY_MEALS_KEY = "meals"
private const val LEGACY_VACCINES_KEY = "vaccines"
private const val LEGACY_NOTES_KEY = "notes"

private const val FS = ""
private const val RS = "\n"

private fun safeText(value: String): String =
    value.replace('\n', ' ').replace('', ' ')

// --- Pet serialization ---

private fun Pet.serialize(): String = listOf(
    id.toString(),
    safeText(name),
    type.name,
    safeText(breed),
    ageYears.toString(),
    weightGrams.toString(),
    photoPath ?: "",
).joinToString(FS)

private fun deserializePet(raw: String): Pet? {
    val parts = raw.split(FS)
    if (parts.size != 7) return null
    return try {
        Pet(
            id = parts[0].toLong(),
            name = parts[1],
            type = PetType.valueOf(parts[2]),
            breed = parts[3],
            ageYears = parts[4].toInt(),
            weightGrams = parts[5].toInt(),
            photoPath = parts[6].takeIf { it.isNotEmpty() },
        )
    } catch (_: Throwable) {
        null
    }
}

private fun deserializeLegacyPet(raw: String, id: Long): Pet? {
    val parts = raw.split("\n")
    if (parts.size != 6) return null
    return try {
        Pet(
            id = id,
            name = parts[0],
            type = PetType.valueOf(parts[1]),
            breed = parts[2],
            ageYears = parts[3].toInt(),
            weightGrams = parts[4].toInt(),
            photoPath = parts[5].takeIf { it.isNotEmpty() },
        )
    } catch (_: Throwable) {
        null
    }
}

private fun serializePets(pets: List<Pet>): String =
    pets.joinToString(RS) { it.serialize() }

private fun deserializePets(raw: String): List<Pet> {
    if (raw.isEmpty()) return emptyList()
    return raw.split(RS).filter { it.isNotEmpty() }.mapNotNull { deserializePet(it) }
}

// --- Per-pet data serialization ---

private fun serializeMeals(meals: Map<String, String>): String =
    meals.entries.joinToString(RS) { (k, v) -> "$k$FS${safeText(v)}" }

private fun deserializeMeals(raw: String): Map<String, String> {
    if (raw.isEmpty()) return emptyMap()
    return raw.split(RS).mapNotNull { line ->
        if (line.isEmpty()) return@mapNotNull null
        val parts = line.split(FS)
        if (parts.size == 2) parts[0] to parts[1] else null
    }.toMap()
}

private fun serializeVaccines(items: List<Vaccine>): String =
    items.joinToString(RS) { "${it.id}$FS${safeText(it.name)}$FS${safeText(it.date)}" }

private fun deserializeVaccines(raw: String): List<Vaccine> {
    if (raw.isEmpty()) return emptyList()
    return raw.split(RS).mapNotNull { line ->
        if (line.isEmpty()) return@mapNotNull null
        val parts = line.split(FS)
        if (parts.size != 3) return@mapNotNull null
        val id = parts[0].toLongOrNull() ?: return@mapNotNull null
        Vaccine(id = id, name = parts[1], date = parts[2])
    }
}

private fun serializeNotes(items: List<PetNote>): String =
    items.joinToString(RS) { "${it.id}$FS${safeText(it.date)}$FS${safeText(it.text)}" }

private fun deserializeNotes(raw: String): List<PetNote> {
    if (raw.isEmpty()) return emptyList()
    return raw.split(RS).mapNotNull { line ->
        if (line.isEmpty()) return@mapNotNull null
        val parts = line.split(FS)
        if (parts.size != 3) return@mapNotNull null
        val id = parts[0].toLongOrNull() ?: return@mapNotNull null
        PetNote(id = id, date = parts[1], text = parts[2])
    }
}

private fun mealsKey(petId: Long) = "meals_$petId"
private fun vaccinesKey(petId: Long) = "vaccines_$petId"
private fun notesKey(petId: Long) = "notes_$petId"

class PetState(private val storage: KeyValueStorage) {

    val pets: SnapshotStateList<Pet> = mutableStateListOf<Pet>().also { list ->
        val petsRaw = storage.get(PETS_KEY)
        if (petsRaw != null) {
            list.addAll(deserializePets(petsRaw))
        } else {
            // Legacy migration — single pet under "pet" key + shared meals/vaccines/notes.
            val legacy = storage.get(LEGACY_PET_KEY)?.let { deserializeLegacyPet(it, id = 1L) }
            if (legacy != null) {
                list.add(legacy)
                storage.put(PETS_KEY, serializePets(list))
                storage.get(LEGACY_MEALS_KEY)?.let { storage.put(mealsKey(legacy.id), it) }
                storage.get(LEGACY_VACCINES_KEY)?.let { storage.put(vaccinesKey(legacy.id), it) }
                storage.get(LEGACY_NOTES_KEY)?.let { storage.put(notesKey(legacy.id), it) }
                storage.put(LEGACY_PET_KEY, null)
                storage.put(LEGACY_MEALS_KEY, null)
                storage.put(LEGACY_VACCINES_KEY, null)
                storage.put(LEGACY_NOTES_KEY, null)
            }
        }
    }

    var currentIndex: Int by mutableStateOf(
        storage.get(CURRENT_INDEX_KEY)?.toIntOrNull()
            ?.coerceIn(0, (pets.size - 1).coerceAtLeast(0))
            ?: 0
    )
        private set

    var language: AppLanguage by mutableStateOf(
        AppLanguage.fromKey(storage.get(LANGUAGE_KEY))
    )
        private set

    fun changeLanguage(lang: AppLanguage) {
        language = lang
        storage.put(LANGUAGE_KEY, lang.key)
    }

    var darkMode: Boolean by mutableStateOf(
        storage.get(DARK_MODE_KEY)?.let { it == "true" } ?: true
    )
        private set

    fun changeDarkMode(value: Boolean) {
        darkMode = value
        storage.put(DARK_MODE_KEY, value.toString())
    }

    fun gameProgress(mode: String): GameProgress? {
        val raw = storage.get("game_$mode") ?: return null
        val parts = raw.split('|')
        if (parts.size != 3) return null
        val seed = parts[0].toLongOrNull() ?: return null
        val index = parts[1].toIntOrNull() ?: return null
        val score = parts[2].toIntOrNull() ?: return null
        return GameProgress(seed, index, score)
    }

    fun saveGameProgress(mode: String, progress: GameProgress) {
        storage.put("game_$mode", "${progress.seed}|${progress.index}|${progress.score}")
    }

    val currentPet: Pet?
        get() = pets.getOrNull(currentIndex)

    private var idCounter: Long = pets.maxOfOrNull { it.id } ?: 0L

    private val mealsByPet: SnapshotStateMap<Long, SnapshotStateMap<String, String>> = mutableStateMapOf()
    private val vaccinesByPet: SnapshotStateMap<Long, SnapshotStateList<Vaccine>> = mutableStateMapOf()
    private val notesByPet: SnapshotStateMap<Long, SnapshotStateList<PetNote>> = mutableStateMapOf()

    // Form draft (in-memory).
    var draftName by mutableStateOf(TextFieldValue(""))
    var draftBreed by mutableStateOf(TextFieldValue(""))
    var draftAge by mutableStateOf(TextFieldValue(""))
    var draftWeight by mutableStateOf(TextFieldValue(""))
    var draftType by mutableStateOf(PetType.Dog)
    var draftPhotoPath by mutableStateOf<String?>(null)

    fun selectPet(index: Int) {
        if (pets.isEmpty()) return
        val coerced = index.coerceIn(0, pets.size - 1)
        currentIndex = coerced
        storage.put(CURRENT_INDEX_KEY, coerced.toString())
    }

    fun addPet(pet: Pet): Pet {
        idCounter += 1
        val newPet = pet.copy(id = idCounter)
        pets.add(newPet)
        selectPet(pets.size - 1)
        storage.put(PETS_KEY, serializePets(pets))
        clearDraft()
        return newPet
    }

    fun updatePhoto(petId: Long, path: String?) {
        val idx = pets.indexOfFirst { it.id == petId }
        if (idx < 0) return
        val updated = pets[idx].copy(photoPath = path)
        pets[idx] = updated
        storage.put(PETS_KEY, serializePets(pets))
    }

    fun removePet(petId: Long) {
        val idx = pets.indexOfFirst { it.id == petId }
        if (idx < 0) return
        pets.removeAt(idx)
        storage.put(PETS_KEY, serializePets(pets))
        storage.put(mealsKey(petId), null)
        storage.put(vaccinesKey(petId), null)
        storage.put(notesKey(petId), null)
        mealsByPet.remove(petId)
        vaccinesByPet.remove(petId)
        notesByPet.remove(petId)
        if (pets.isEmpty()) {
            currentIndex = 0
            storage.put(CURRENT_INDEX_KEY, "0")
        } else {
            val newIndex = currentIndex.coerceIn(0, pets.size - 1)
            currentIndex = newIndex
            storage.put(CURRENT_INDEX_KEY, newIndex.toString())
        }
    }

    fun clearDraft() {
        draftName = TextFieldValue("")
        draftBreed = TextFieldValue("")
        draftAge = TextFieldValue("")
        draftWeight = TextFieldValue("")
        draftType = PetType.Dog
        draftPhotoPath = null
    }

    fun mealsFor(petId: Long): SnapshotStateMap<String, String> = mealsByPet.getOrPut(petId) {
        mutableStateMapOf<String, String>().apply {
            storage.get(mealsKey(petId))?.let { putAll(deserializeMeals(it)) }
        }
    }

    fun vaccinesFor(petId: Long): SnapshotStateList<Vaccine> = vaccinesByPet.getOrPut(petId) {
        mutableStateListOf<Vaccine>().apply {
            storage.get(vaccinesKey(petId))?.let { addAll(deserializeVaccines(it)) }
        }
    }

    fun notesFor(petId: Long): SnapshotStateList<PetNote> = notesByPet.getOrPut(petId) {
        mutableStateListOf<PetNote>().apply {
            storage.get(notesKey(petId))?.let { addAll(deserializeNotes(it)) }
        }
    }

    fun setMeal(petId: Long, day: WeekDay, slot: MealSlot, text: String) {
        val meals = mealsFor(petId)
        val key = mealKey(day, slot)
        if (text.isBlank()) meals.remove(key) else meals[key] = text
        storage.put(mealsKey(petId), serializeMeals(meals))
    }

    fun addVaccine(petId: Long, name: String, date: String) {
        if (name.isBlank() || date.isBlank()) return
        idCounter += 1
        val list = vaccinesFor(petId)
        list.add(0, Vaccine(id = idCounter, name = name.trim(), date = date.trim()))
        storage.put(vaccinesKey(petId), serializeVaccines(list))
    }

    fun removeVaccine(petId: Long, id: Long) {
        val list = vaccinesFor(petId)
        list.removeAll { it.id == id }
        storage.put(vaccinesKey(petId), serializeVaccines(list))
    }

    fun addNote(petId: Long, text: String, date: String = "${nowDate()} ${nowTime()}") {
        if (text.isBlank()) return
        idCounter += 1
        val list = notesFor(petId)
        list.add(0, PetNote(id = idCounter, date = date, text = text.trim()))
        storage.put(notesKey(petId), serializeNotes(list))
    }

    fun removeNote(petId: Long, id: Long) {
        val list = notesFor(petId)
        list.removeAll { it.id == id }
        storage.put(notesKey(petId), serializeNotes(list))
    }
}

val LocalPetState = compositionLocalOf<PetState> { error("PetState not provided") }

@Composable
fun rememberPetState(storage: KeyValueStorage): PetState =
    remember(storage) { PetState(storage) }
