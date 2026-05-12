package org.example.project.data

import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(val key: String, val displayName: String) {
    English("en", "English"),
    Spanish("es", "Español");

    companion object {
        fun fromKey(key: String?): AppLanguage =
            entries.firstOrNull { it.key == key } ?: English
    }
}

interface Strings {
    // Navigation
    val navExplore: String
    val navPlay: String
    val navPet: String
    val navSettings: String

    // Generic
    val ok: String
    val cancel: String
    val close: String
    val back: String
    val retry: String

    // Loading
    val loadingTitle: String
    val loadingSubtitle: String

    // No internet
    val noInternet: String

    // Settings
    val settingsEyebrow: String
    val settingsTitle: String
    val settingsSubtitle: String
    val sectionAppearance: String
    val darkMode: String
    val darkModeSubtitle: String
    val language: String
    val sectionData: String
    val clearLocalData: String
    val clearLocalDataSubtitle: String
    val yourPets: String
    val noPetsYet: String
    val sectionLegal: String
    val versionFooter: String

    // Pet screen
    val petEyebrow: String
    fun petEyebrowWithIndex(current: Int, total: Int): String
    val addAnotherPet: String
    val petProfile: String
    val saveToSwipe: String
    val addRealPet: String

    // Add pet form
    val addYourPet: String
    val addYourPetSubtitle: String
    val takePhoto: String
    val choose: String
    val name: String
    val breedOptional: String
    val ageYrs: String
    val weightG: String
    val savePet: String
    val type: String

    // Pet profile
    val ageLabel: String
    val weightLabel: String
    fun ageValue(years: Int): String
    val mealPlanner: String
    val mealPlannerSubtitle: String
    val vaccines: String
    val vaccinesSubtitle: String
    val noVaccines: String
    val vaccineName: String
    val datePlaceholder: String
    val addVaccine: String
    val dateFormatError: String
    val notes: String
    val notesSubtitle: String
    val notePlaceholder: String
    val addNote: String
    val noNotes: String

    // Explore
    val exploreEyebrow: String
    val exploreTitle: String
    fun exploreSubtitle(count: Int): String
    val allAnimals: String
    val byType: String
    val all: String
    val searchAnimals: String
    val factOfTheDay: String
    val featured: String
    val noTracks: String
    val tryDifferentFilter: String

    // Animal detail
    val statSpeed: String
    val statWeight: String
    val statLifespan: String
    val statLifespanUnit: String
    val behavior: String
    val habitat: String
    val diet: String
    val dangerLevel: String
    val countriesTitle: String
    val factsTitle: String
    val compareWithAnother: String
    val compareWith: String
    fun compareWithSubtitle(count: Int): String
    val stats: String
    val profile: String
    val danger: String
    val regions: String
    val countries: String

    // Play
    val playEyebrow: String
    val gameQuizFull: String
    val gameQuizShort: String
    val gameQuizTagline: String
    val gameSurvivalFull: String
    val gameSurvivalShort: String
    val gameSurvivalTagline: String
    val gameCompareFull: String
    val gameCompareShort: String
    val gameCompareTagline: String
    val playing: String
    val tapToPlay: String
    val animalQuizLabel: String
    val survivalLabel: String
    val compareLabel: String
    val quizComplete: String
    val runComplete: String
    val compareComplete: String
    val survivedLabel: String
    val rightLabel: String
    val seeResults: String
    val nextQuestion: String
    val nextScenario: String
    val nextRound: String
    val youSurvived: String
    val badCall: String
    val correct: String
    val notQuite: String
    val gradeExpert: String
    val gradeSolid: String
    val gradeDecent: String
    val gradeRough: String
    val gradeBetterLuck: String
    val playAgain: String
    fun scoreOutOf(score: Int, label: String, total: Int): String

    // iOS dialogs
    val iosCameraPermissionToast: String

    // Enums
    fun petType(t: PetType): String
    fun mealSlot(s: MealSlot): String
    fun weekDayShort(d: WeekDay): String
    fun mealHint(s: MealSlot): String
    fun animalCategory(c: AnimalCategory): String
    fun region(r: Region): String
    fun dangerLabel(d: Danger): String
}

val LocalAppStrings = compositionLocalOf<Strings> { EnglishStrings }
val LocalAppLanguage = compositionLocalOf { AppLanguage.English }

object EnglishStrings : Strings {
    override val navExplore = "Explore"
    override val navPlay = "Play"
    override val navPet = "Pet"
    override val navSettings = "Settings"

    override val ok = "OK"
    override val cancel = "Cancel"
    override val close = "Close"
    override val back = "Back"
    override val retry = "Retry"

    override val loadingTitle = "Tracking the wild"
    override val loadingSubtitle = "Gathering paw prints from across the planet…"

    override val noInternet = "No Internet Connection"

    override val settingsEyebrow = "Settings"
    override val settingsTitle = "Preferences"
    override val settingsSubtitle = "Tune the app to your routine."
    override val sectionAppearance = "Appearance"
    override val darkMode = "Dark mode"
    override val darkModeSubtitle = "Asphalt theme tuned for the night."
    override val language = "Language"
    override val sectionData = "Data"
    override val clearLocalData = "Clear local data"
    override val clearLocalDataSubtitle = "Remove cached photos and timeline entries."
    override val yourPets = "Your pets"
    override val noPetsYet = "No pets added yet."
    override val sectionLegal = "Legal"
    override val versionFooter = "v1.0 · made with care"

    override val petEyebrow = "Pet"
    override fun petEyebrowWithIndex(current: Int, total: Int) = "Pet · $current/$total"
    override val addAnotherPet = "Add another pet"
    override val petProfile = "Pet Profile"
    override val saveToSwipe = "Save to swipe between your pets."
    override val addRealPet = "Add your real pet to start tracking."

    override val addYourPet = "Add your pet"
    override val addYourPetSubtitle = "Photo, name, type, age, weight."
    override val takePhoto = "Take Photo"
    override val choose = "Choose"
    override val name = "Name"
    override val breedOptional = "Breed (optional)"
    override val ageYrs = "Age (yrs)"
    override val weightG = "Weight (g)"
    override val savePet = "Save pet"
    override val type = "Type"

    override val ageLabel = "Age"
    override val weightLabel = "Weight"
    override fun ageValue(years: Int) = "$years yr"
    override val mealPlanner = "Meal planner"
    override val mealPlannerSubtitle = "What you feed by day of the week"
    override val vaccines = "Vaccines"
    override val vaccinesSubtitle = "Track every shot you've given"
    override val noVaccines = "No vaccines yet — add the first one below."
    override val vaccineName = "Vaccine name"
    override val datePlaceholder = "yyyy-mm-dd"
    override val addVaccine = "Add vaccine"
    override val dateFormatError = "Date must be in yyyy-MM-dd format"
    override val notes = "Notes"
    override val notesSubtitle = "Symptoms, behavior, anything worth tracking"
    override val notePlaceholder = "Eg. Limping a bit on the left back paw"
    override val addNote = "Add note"
    override val noNotes = "No notes yet — start logging observations above."

    override val exploreEyebrow = "Explore"
    override val exploreTitle = "Wildlife Encyclopedia"
    override fun exploreSubtitle(count: Int) = "$count species across the planet"
    override val allAnimals = "All animals"
    override val byType = "BY TYPE"
    override val all = "All"
    override val searchAnimals = "Search animals…"
    override val factOfTheDay = "FACT OF THE DAY"
    override val featured = "FEATURED"
    override val noTracks = "No tracks here yet"
    override val tryDifferentFilter = "Try a different filter or search term."

    override val statSpeed = "Speed"
    override val statWeight = "Weight"
    override val statLifespan = "Lifespan"
    override val statLifespanUnit = "yr"
    override val behavior = "Behavior"
    override val habitat = "Habitat"
    override val diet = "Diet"
    override val dangerLevel = "Danger level"
    override val countriesTitle = "Countries"
    override val factsTitle = "Facts"
    override val compareWithAnother = "Compare with another animal"
    override val compareWith = "Compare with"
    override fun compareWithSubtitle(count: Int) = "$count other species"
    override val stats = "Stats"
    override val profile = "Profile"
    override val danger = "Danger"
    override val regions = "Regions"
    override val countries = "Countries"

    override val playEyebrow = "Play"
    override val gameQuizFull = "Animal Quiz"
    override val gameQuizShort = "Quiz"
    override val gameQuizTagline = "How well do you know your animals?"
    override val gameSurvivalFull = "Would You Survive?"
    override val gameSurvivalShort = "Survive"
    override val gameSurvivalTagline = "Make the right call in the wild."
    override val gameCompareFull = "Compare Animals"
    override val gameCompareShort = "Compare"
    override val gameCompareTagline = "Spot the stronger, faster, longer-lived."
    override val playing = "PLAYING"
    override val tapToPlay = "TAP TO PLAY"
    override val animalQuizLabel = "ANIMAL QUIZ"
    override val survivalLabel = "WOULD YOU SURVIVE?"
    override val compareLabel = "COMPARE ANIMALS"
    override val quizComplete = "Quiz complete"
    override val runComplete = "Run complete"
    override val compareComplete = "Compare run complete"
    override val survivedLabel = "survived"
    override val rightLabel = "right"
    override val seeResults = "See results →"
    override val nextQuestion = "Next question →"
    override val nextScenario = "Next scenario →"
    override val nextRound = "Next round →"
    override val youSurvived = "✅ You survived"
    override val badCall = "💀 Bad call"
    override val correct = "Correct!"
    override val notQuite = "Not quite."
    override val gradeExpert = "Wildlife expert!"
    override val gradeSolid = "Solid run."
    override val gradeDecent = "Decent — try again?"
    override val gradeRough = "The wild is rough."
    override val gradeBetterLuck = "Better luck next time."
    override val playAgain = "Play again"
    override fun scoreOutOf(score: Int, label: String, total: Int) = "$score $label out of $total"

    override val iosCameraPermissionToast = "Please grant camera access"

    override fun petType(t: PetType) = when (t) {
        PetType.Dog -> "Dog"
        PetType.Cat -> "Cat"
        PetType.Bird -> "Bird"
        PetType.Other -> "Other"
    }
    override fun mealSlot(s: MealSlot) = when (s) {
        MealSlot.Morning -> "Morning"
        MealSlot.Day -> "Day"
        MealSlot.Evening -> "Evening"
    }
    override fun mealHint(s: MealSlot) = mealSlot(s)
    override fun weekDayShort(d: WeekDay) = when (d) {
        WeekDay.Mon -> "Mon"
        WeekDay.Tue -> "Tue"
        WeekDay.Wed -> "Wed"
        WeekDay.Thu -> "Thu"
        WeekDay.Fri -> "Fri"
        WeekDay.Sat -> "Sat"
        WeekDay.Sun -> "Sun"
    }
    override fun animalCategory(c: AnimalCategory) = when (c) {
        AnimalCategory.Mammal -> "Mammals"
        AnimalCategory.Bird -> "Birds"
        AnimalCategory.Reptile -> "Reptiles"
        AnimalCategory.Amphibian -> "Amphibians"
        AnimalCategory.Ocean -> "Ocean"
        AnimalCategory.Insect -> "Insects"
        AnimalCategory.Arachnid -> "Arachnids"
    }
    override fun region(r: Region) = when (r) {
        Region.Africa -> "Africa"
        Region.Europe -> "Europe"
        Region.Asia -> "Asia"
        Region.Australia -> "Australia"
        Region.NorthAmerica -> "North America"
        Region.SouthAmerica -> "South America"
        Region.Antarctica -> "Antarctica"
        Region.Worldwide -> "Worldwide"
    }
    override fun dangerLabel(d: Danger) = when (d) {
        Danger.Low -> "Low risk"
        Danger.Medium -> "Caution"
        Danger.High -> "Dangerous"
        Danger.Extreme -> "Extreme"
    }
}

object SpanishStrings : Strings {
    override val navExplore = "Explorar"
    override val navPlay = "Jugar"
    override val navPet = "Mascota"
    override val navSettings = "Ajustes"

    override val ok = "Aceptar"
    override val cancel = "Cancelar"
    override val close = "Cerrar"
    override val back = "Atrás"
    override val retry = "Reintentar"

    override val loadingTitle = "Rastreando la naturaleza"
    override val loadingSubtitle = "Recogiendo huellas de todo el planeta…"

    override val noInternet = "Sin conexión a Internet"

    override val settingsEyebrow = "Ajustes"
    override val settingsTitle = "Preferencias"
    override val settingsSubtitle = "Adapta la app a tu rutina."
    override val sectionAppearance = "Apariencia"
    override val darkMode = "Modo oscuro"
    override val darkModeSubtitle = "Tema asfalto pensado para la noche."
    override val language = "Idioma"
    override val sectionData = "Datos"
    override val clearLocalData = "Borrar datos locales"
    override val clearLocalDataSubtitle = "Elimina fotos en caché y entradas del historial."
    override val yourPets = "Tus mascotas"
    override val noPetsYet = "Aún no hay mascotas añadidas."
    override val sectionLegal = "Legal"
    override val versionFooter = "v1.0 · hecho con cariño"

    override val petEyebrow = "Mascota"
    override fun petEyebrowWithIndex(current: Int, total: Int) = "Mascota · $current/$total"
    override val addAnotherPet = "Añadir otra mascota"
    override val petProfile = "Perfil de la mascota"
    override val saveToSwipe = "Guarda para deslizar entre tus mascotas."
    override val addRealPet = "Añade tu mascota real para empezar a registrar."

    override val addYourPet = "Añade tu mascota"
    override val addYourPetSubtitle = "Foto, nombre, tipo, edad, peso."
    override val takePhoto = "Hacer foto"
    override val choose = "Elegir"
    override val name = "Nombre"
    override val breedOptional = "Raza (opcional)"
    override val ageYrs = "Edad (años)"
    override val weightG = "Peso (g)"
    override val savePet = "Guardar mascota"
    override val type = "Tipo"

    override val ageLabel = "Edad"
    override val weightLabel = "Peso"
    override fun ageValue(years: Int) = "$years años"
    override val mealPlanner = "Planificador de comidas"
    override val mealPlannerSubtitle = "Qué das de comer por día de la semana"
    override val vaccines = "Vacunas"
    override val vaccinesSubtitle = "Registra cada inyección que has puesto"
    override val noVaccines = "Aún no hay vacunas — añade la primera abajo."
    override val vaccineName = "Nombre de la vacuna"
    override val datePlaceholder = "aaaa-mm-dd"
    override val addVaccine = "Añadir vacuna"
    override val dateFormatError = "La fecha debe estar en formato aaaa-MM-dd"
    override val notes = "Notas"
    override val notesSubtitle = "Síntomas, comportamiento, lo que valga la pena registrar"
    override val notePlaceholder = "Ej. Cojea un poco de la pata trasera izquierda"
    override val addNote = "Añadir nota"
    override val noNotes = "Aún no hay notas — empieza a registrar observaciones arriba."

    override val exploreEyebrow = "Explorar"
    override val exploreTitle = "Enciclopedia de fauna"
    override fun exploreSubtitle(count: Int) = "$count especies en todo el planeta"
    override val allAnimals = "Todos los animales"
    override val byType = "POR TIPO"
    override val all = "Todos"
    override val searchAnimals = "Buscar animales…"
    override val factOfTheDay = "DATO DEL DÍA"
    override val featured = "DESTACADO"
    override val noTracks = "Aún no hay rastros aquí"
    override val tryDifferentFilter = "Prueba con otro filtro o término de búsqueda."

    override val statSpeed = "Velocidad"
    override val statWeight = "Peso"
    override val statLifespan = "Esperanza"
    override val statLifespanUnit = "años"
    override val behavior = "Comportamiento"
    override val habitat = "Hábitat"
    override val diet = "Dieta"
    override val dangerLevel = "Nivel de peligro"
    override val countriesTitle = "Países"
    override val factsTitle = "Datos"
    override val compareWithAnother = "Comparar con otro animal"
    override val compareWith = "Comparar con"
    override fun compareWithSubtitle(count: Int) = "$count otras especies"
    override val stats = "Estadísticas"
    override val profile = "Perfil"
    override val danger = "Peligro"
    override val regions = "Regiones"
    override val countries = "Países"

    override val playEyebrow = "Jugar"
    override val gameQuizFull = "Quiz de animales"
    override val gameQuizShort = "Quiz"
    override val gameQuizTagline = "¿Qué tan bien conoces a los animales?"
    override val gameSurvivalFull = "¿Sobrevivirías?"
    override val gameSurvivalShort = "Sobrevivir"
    override val gameSurvivalTagline = "Toma la decisión correcta en la naturaleza."
    override val gameCompareFull = "Comparar animales"
    override val gameCompareShort = "Comparar"
    override val gameCompareTagline = "Adivina el más fuerte, rápido o longevo."
    override val playing = "JUGANDO"
    override val tapToPlay = "TOCA PARA JUGAR"
    override val animalQuizLabel = "QUIZ DE ANIMALES"
    override val survivalLabel = "¿SOBREVIVIRÍAS?"
    override val compareLabel = "COMPARAR ANIMALES"
    override val quizComplete = "Quiz completado"
    override val runComplete = "Ronda completada"
    override val compareComplete = "Comparación completada"
    override val survivedLabel = "supervivencias"
    override val rightLabel = "aciertos"
    override val seeResults = "Ver resultados →"
    override val nextQuestion = "Siguiente pregunta →"
    override val nextScenario = "Siguiente escenario →"
    override val nextRound = "Siguiente ronda →"
    override val youSurvived = "✅ Sobreviviste"
    override val badCall = "💀 Mala decisión"
    override val correct = "¡Correcto!"
    override val notQuite = "No exactamente."
    override val gradeExpert = "¡Experto en fauna!"
    override val gradeSolid = "Ronda sólida."
    override val gradeDecent = "Decente — ¿otra vez?"
    override val gradeRough = "La naturaleza es dura."
    override val gradeBetterLuck = "Más suerte la próxima vez."
    override val playAgain = "Jugar de nuevo"
    override fun scoreOutOf(score: Int, label: String, total: Int) = "$score $label de $total"

    override val iosCameraPermissionToast = "Concede acceso a la cámara"

    override fun petType(t: PetType) = when (t) {
        PetType.Dog -> "Perro"
        PetType.Cat -> "Gato"
        PetType.Bird -> "Pájaro"
        PetType.Other -> "Otro"
    }
    override fun mealSlot(s: MealSlot) = when (s) {
        MealSlot.Morning -> "Mañana"
        MealSlot.Day -> "Día"
        MealSlot.Evening -> "Noche"
    }
    override fun mealHint(s: MealSlot) = mealSlot(s)
    override fun weekDayShort(d: WeekDay) = when (d) {
        WeekDay.Mon -> "Lun"
        WeekDay.Tue -> "Mar"
        WeekDay.Wed -> "Mié"
        WeekDay.Thu -> "Jue"
        WeekDay.Fri -> "Vie"
        WeekDay.Sat -> "Sáb"
        WeekDay.Sun -> "Dom"
    }
    override fun animalCategory(c: AnimalCategory) = when (c) {
        AnimalCategory.Mammal -> "Mamíferos"
        AnimalCategory.Bird -> "Aves"
        AnimalCategory.Reptile -> "Reptiles"
        AnimalCategory.Amphibian -> "Anfibios"
        AnimalCategory.Ocean -> "Océano"
        AnimalCategory.Insect -> "Insectos"
        AnimalCategory.Arachnid -> "Arácnidos"
    }
    override fun region(r: Region) = when (r) {
        Region.Africa -> "África"
        Region.Europe -> "Europa"
        Region.Asia -> "Asia"
        Region.Australia -> "Australia"
        Region.NorthAmerica -> "América del Norte"
        Region.SouthAmerica -> "América del Sur"
        Region.Antarctica -> "Antártida"
        Region.Worldwide -> "Mundial"
    }
    override fun dangerLabel(d: Danger) = when (d) {
        Danger.Low -> "Riesgo bajo"
        Danger.Medium -> "Precaución"
        Danger.High -> "Peligroso"
        Danger.Extreme -> "Extremo"
    }
}

fun stringsFor(language: AppLanguage): Strings = when (language) {
    AppLanguage.English -> EnglishStrings
    AppLanguage.Spanish -> SpanishStrings
}
