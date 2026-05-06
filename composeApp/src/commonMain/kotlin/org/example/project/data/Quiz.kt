package org.example.project.data

import kotlin.random.Random

data class QuizQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
)

data class SurvivalScenario(
    val situation: String,
    val choices: List<SurvivalChoice>,
)

data class SurvivalChoice(
    val text: String,
    val outcome: String,
    val survives: Boolean,
)

data class CompareRound(
    val question: String,
    val left: Animal,
    val right: Animal,
    val winnerIsLeft: Boolean,
)

private data class QuizQuestionEntry(
    val prompt: Pair<String, String>,
    val options: Pair<List<String>, List<String>>,
    val correctIndex: Int,
    val explanation: Pair<String, String>,
) {
    fun localized(lang: AppLanguage): QuizQuestion {
        val en = lang == AppLanguage.English
        return QuizQuestion(
            prompt = if (en) prompt.first else prompt.second,
            options = if (en) options.first else options.second,
            correctIndex = correctIndex,
            explanation = if (en) explanation.first else explanation.second,
        )
    }
}

private data class SurvivalChoiceEntry(
    val text: Pair<String, String>,
    val outcome: Pair<String, String>,
    val survives: Boolean,
) {
    fun localized(lang: AppLanguage): SurvivalChoice {
        val en = lang == AppLanguage.English
        return SurvivalChoice(
            text = if (en) text.first else text.second,
            outcome = if (en) outcome.first else outcome.second,
            survives = survives,
        )
    }
}

private data class SurvivalScenarioEntry(
    val situation: Pair<String, String>,
    val choices: List<SurvivalChoiceEntry>,
) {
    fun localized(lang: AppLanguage): SurvivalScenario {
        val en = lang == AppLanguage.English
        return SurvivalScenario(
            situation = if (en) situation.first else situation.second,
            choices = choices.map { it.localized(lang) },
        )
    }
}

object QuizRepository {

    fun questions(lang: AppLanguage): List<QuizQuestion> = questionEntries.map { it.localized(lang) }

    fun scenarios(lang: AppLanguage): List<SurvivalScenario> = scenarioEntries.map { it.localized(lang) }

    fun generateCompareRounds(seed: Long, count: Int, lang: AppLanguage): List<CompareRound> {
        val animals = AnimalRepository.all(lang)
        val questionEn = listOf(
            "Who is faster?", "Who is heavier?", "Who lives longer?",
        )
        val questionEs = listOf(
            "¿Quién es más rápido?", "¿Quién es más pesado?", "¿Quién vive más?",
        )
        val extractors: List<(Animal) -> Int> = listOf(
            { it.speedKmh }, { it.weightKg }, { it.lifespanYears },
        )
        val rng = Random(seed)
        val rounds = mutableListOf<CompareRound>()
        var safety = 0
        while (rounds.size < count && safety < count * 30) {
            safety++
            val axisIdx = rng.nextInt(extractors.size)
            val extractor = extractors[axisIdx]
            val text = if (lang == AppLanguage.English) questionEn[axisIdx] else questionEs[axisIdx]
            val candidates = animals.filter { extractor(it) > 0 }
            if (candidates.size < 2) continue
            val pair = candidates.shuffled(rng).take(2)
            val a = pair[0]
            val b = pair[1]
            val va = extractor(a)
            val vb = extractor(b)
            if (va == vb) continue
            rounds.add(CompareRound(text, left = a, right = b, winnerIsLeft = va > vb))
        }
        return rounds
    }

    private val questionEntries: List<QuizQuestionEntry> = listOf(
        QuizQuestionEntry(
            "Which animal sleeps with one eye open?" to "¿Qué animal duerme con un ojo abierto?",
            listOf("Wolf", "Dolphin", "Eagle", "Panda") to listOf("Lobo", "Delfín", "Águila", "Panda"),
            1,
            "Dolphins rest with half the brain awake to keep breathing." to "Los delfines descansan con la mitad del cerebro despierta para seguir respirando.",
        ),
        QuizQuestionEntry(
            "Where do pandas naturally live?" to "¿Dónde viven los pandas en estado salvaje?",
            listOf("Africa", "Australia", "China", "Norway") to listOf("África", "Australia", "China", "Noruega"),
            2,
            "Wild pandas live in the misty bamboo forests of central China." to "Los pandas salvajes viven en los bosques de bambú del centro de China.",
        ),
        QuizQuestionEntry(
            "Which has the strongest bite ever measured?" to "¿Cuál tiene la mordida más fuerte jamás medida?",
            listOf("Lion", "Crocodile", "Wolf", "Eagle") to listOf("León", "Cocodrilo", "Lobo", "Águila"),
            1,
            "Crocodile bite force reaches roughly 16,460 newtons." to "La fuerza de la mordida del cocodrilo alcanza unos 16.460 newtons.",
        ),
        QuizQuestionEntry(
            "Kangaroos cannot:" to "Los canguros no pueden:",
            listOf("Swim", "Move backwards", "Jump", "Climb fences") to listOf("Nadar", "Moverse hacia atrás", "Saltar", "Trepar vallas"),
            1,
            "Their large feet and tail prevent backward movement." to "Sus grandes patas y cola les impiden moverse hacia atrás.",
        ),
        QuizQuestionEntry(
            "Which bird mates for life?" to "¿Qué ave se empareja de por vida?",
            listOf("Sparrow", "Eagle", "Pigeon", "Crow") to listOf("Gorrión", "Águila", "Paloma", "Cuervo"),
            1,
            "Eagles typically pair for life and reuse the same nest." to "Las águilas suelen emparejarse de por vida y reutilizan el mismo nido.",
        ),
        QuizQuestionEntry(
            "Which is the fastest land animal?" to "¿Cuál es el animal terrestre más rápido?",
            listOf("Lion", "Cheetah", "Tiger", "Wolf") to listOf("León", "Guepardo", "Tigre", "Lobo"),
            1,
            "Cheetahs accelerate from 0 to 95 km/h in three seconds." to "El guepardo acelera de 0 a 95 km/h en tres segundos.",
        ),
        QuizQuestionEntry(
            "What is the largest land animal?" to "¿Cuál es el animal terrestre más grande?",
            listOf("Rhino", "Elephant", "Hippo", "Giraffe") to listOf("Rinoceronte", "Elefante", "Hipopótamo", "Jirafa"),
            1,
            "African elephants can weigh up to 6 tonnes." to "Los elefantes africanos pueden pesar hasta 6 toneladas.",
        ),
        QuizQuestionEntry(
            "Tallest animal alive?" to "¿El animal más alto del mundo?",
            listOf("Giraffe", "Camel", "Ostrich", "Elephant") to listOf("Jirafa", "Camello", "Avestruz", "Elefante"),
            0,
            "Giraffes reach almost 6 metres in height." to "Las jirafas alcanzan casi 6 metros de altura.",
        ),
        QuizQuestionEntry(
            "Largest cat species?" to "¿La especie de felino más grande?",
            listOf("Lion", "Tiger", "Leopard", "Jaguar") to listOf("León", "Tigre", "Leopardo", "Jaguar"),
            1,
            "Tigers are the largest of all wild cats — Bengal and Siberian most of all." to "Los tigres son los más grandes de todos los felinos salvajes — los de Bengala y Siberianos sobre todo.",
        ),
        QuizQuestionEntry(
            "Heaviest snake in the world?" to "¿La serpiente más pesada del mundo?",
            listOf("King cobra", "Anaconda", "Python", "Black mamba") to listOf("Cobra real", "Anaconda", "Pitón", "Mamba negra"),
            1,
            "Green anacondas can weigh 200 kg+, much heavier than any python." to "Las anacondas verdes pueden pesar más de 200 kg, mucho más que cualquier pitón.",
        ),
        QuizQuestionEntry(
            "How many hearts does an octopus have?" to "¿Cuántos corazones tiene un pulpo?",
            listOf("One", "Two", "Three", "Five") to listOf("Uno", "Dos", "Tres", "Cinco"),
            2,
            "Two pump blood to the gills and one to the rest of the body." to "Dos bombean sangre a las branquias y uno al resto del cuerpo.",
        ),
        QuizQuestionEntry(
            "Which species is called the 'unicorn of the sea'?" to "¿Qué especie es llamada 'el unicornio del mar'?",
            listOf("Whale shark", "Narwhal", "Sea horse", "Manatee") to listOf("Tiburón ballena", "Narval", "Caballito de mar", "Manatí"),
            1,
            "The narwhal's tusk is actually a giant tooth that pierces the lip." to "El colmillo del narval es en realidad un diente gigante que atraviesa el labio.",
        ),
        QuizQuestionEntry(
            "Largest living lizard?" to "¿El lagarto vivo más grande?",
            listOf("Iguana", "Komodo dragon", "Chameleon", "Gila monster") to listOf("Iguana", "Dragón de Komodo", "Camaleón", "Monstruo de Gila"),
            1,
            "Komodo dragons can exceed 3 metres long and 90 kg." to "Los dragones de Komodo pueden superar los 3 metros y los 90 kg.",
        ),
        QuizQuestionEntry(
            "Which mammal is famously the slowest?" to "¿Qué mamífero es famoso por ser el más lento?",
            listOf("Sloth", "Koala", "Giant tortoise", "Panda") to listOf("Perezoso", "Koala", "Tortuga gigante", "Panda"),
            0,
            "Tortoises are reptiles — sloths are the slowest mammal." to "Las tortugas son reptiles — los perezosos son el mamífero más lento.",
        ),
        QuizQuestionEntry(
            "Which animal dives at over 380 km/h to strike prey?" to "¿Qué animal se lanza en picado a más de 380 km/h para cazar?",
            listOf("Cheetah", "Peregrine falcon", "Sailfish", "Eagle") to listOf("Guepardo", "Halcón peregrino", "Pez vela", "Águila"),
            1,
            "The peregrine's stoop is the fastest movement on Earth." to "El picado del peregrino es el movimiento más rápido de la Tierra.",
        ),
        QuizQuestionEntry(
            "How do honey bees tell each other where flowers are?" to "¿Cómo se comunican las abejas la ubicación de las flores?",
            listOf("They whistle", "Waggle dance", "Leave pheromone trails only", "By pointing") to listOf("Silban", "Danza del meneo", "Solo dejan rastros de feromonas", "Señalando"),
            1,
            "The waggle dance encodes both distance and direction." to "La danza del meneo codifica tanto la distancia como la dirección.",
        ),
        QuizQuestionEntry(
            "Which flightless bird is the world's fastest two-legged runner?" to "¿Qué ave no voladora es la corredora bípeda más rápida del mundo?",
            listOf("Penguin", "Ostrich", "Kiwi", "Rhea") to listOf("Pingüino", "Avestruz", "Kiwi", "Ñandú"),
            1,
            "Ostriches can sprint at around 70 km/h." to "Las avestruces pueden correr a unos 70 km/h.",
        ),
        QuizQuestionEntry(
            "Where are wild kiwi birds found?" to "¿Dónde se encuentran los kiwis salvajes?",
            listOf("Australia", "USA", "India", "New Zealand") to listOf("Australia", "EE. UU.", "India", "Nueva Zelanda"),
            3,
            "Kiwis are endemic to New Zealand." to "Los kiwis son endémicos de Nueva Zelanda.",
        ),
        QuizQuestionEntry(
            "Which animal has no brain, heart or bones?" to "¿Qué animal no tiene cerebro, corazón ni huesos?",
            listOf("Starfish", "Jellyfish", "Octopus", "Snail") to listOf("Estrella de mar", "Medusa", "Pulpo", "Caracol"),
            1,
            "Jellyfish are 95% water and rely on a simple nerve net." to "Las medusas son 95% agua y dependen de una red nerviosa simple.",
        ),
        QuizQuestionEntry(
            "Which marsupial sleeps up to 22 hours per day?" to "¿Qué marsupial duerme hasta 22 horas al día?",
            listOf("Sloth", "Koala", "Possum", "Wombat") to listOf("Perezoso", "Koala", "Zarigüeya", "Wombat"),
            1,
            "Sloths aren't marsupials — koalas are the famous sleepers." to "Los perezosos no son marsupiales — los koalas son los famosos dormilones.",
        ),
        QuizQuestionEntry(
            "Tasmanian devils are native to which country?" to "¿Los demonios de Tasmania son originarios de qué país?",
            listOf("Australia", "Indonesia", "USA", "Brazil") to listOf("Australia", "Indonesia", "EE. UU.", "Brasil"),
            0,
            "They live almost exclusively in Tasmania, Australia." to "Viven casi exclusivamente en Tasmania, Australia.",
        ),
        QuizQuestionEntry(
            "Largest bear species?" to "¿Cuál es la especie de oso más grande?",
            listOf("Brown bear", "Polar bear", "Black bear", "Panda") to listOf("Oso pardo", "Oso polar", "Oso negro", "Panda"),
            1,
            "Adult polar bears can exceed 600 kg." to "Los osos polares adultos pueden superar los 600 kg.",
        ),
        QuizQuestionEntry(
            "Which Arctic owl hunts in daylight all summer?" to "¿Qué búho ártico caza durante el día todo el verano?",
            listOf("Barn owl", "Snowy owl", "Eagle owl", "Tawny owl") to listOf("Lechuza común", "Búho nival", "Búho real", "Cárabo común"),
            1,
            "Snowy owls hunt during the polar day, unusual for owls." to "Los búhos nivales cazan durante el día polar, algo inusual entre los búhos.",
        ),
        QuizQuestionEntry(
            "Which group contains a biologically immortal species?" to "¿Qué grupo contiene una especie biológicamente inmortal?",
            listOf("Tortoises", "Some jellyfish", "Lobsters", "Hydras") to listOf("Tortugas", "Algunas medusas", "Langostas", "Hidras"),
            1,
            "Turritopsis dohrnii can revert to its polyp stage indefinitely." to "Turritopsis dohrnii puede revertir a su estado de pólipo indefinidamente.",
        ),
        QuizQuestionEntry(
            "What does the king cobra mostly eat?" to "¿De qué se alimenta principalmente la cobra real?",
            listOf("Frogs", "Mice", "Other snakes", "Eggs") to listOf("Ranas", "Ratones", "Otras serpientes", "Huevos"),
            2,
            "Its name in Latin literally means 'snake-eater'." to "Su nombre en latín significa literalmente 'come-serpientes'.",
        ),
        QuizQuestionEntry(
            "What makes the platypus unusual among mammals?" to "¿Qué hace al ornitorrinco inusual entre los mamíferos?",
            listOf("It has wings", "It lays eggs", "It lives only in salt water", "It glows red") to listOf("Tiene alas", "Pone huevos", "Vive solo en agua salada", "Brilla en rojo"),
            1,
            "Only five mammal species lay eggs — platypus is one." to "Solo cinco especies de mamíferos ponen huevos — el ornitorrinco es una.",
        ),
        QuizQuestionEntry(
            "In black widow spiders, the dangerous venom belongs to:" to "En las viudas negras, el veneno peligroso pertenece a:",
            listOf("Both sexes equally", "Males only", "Females only", "Only newborns") to listOf("Ambos sexos por igual", "Solo machos", "Solo hembras", "Solo recién nacidas"),
            2,
            "Females have venom up to 15× a rattlesnake's; males rarely bite humans." to "Las hembras tienen veneno hasta 15× más fuerte que el de una serpiente cascabel; los machos rara vez muerden a humanos.",
        ),
        QuizQuestionEntry(
            "Reindeer eyes change colour to blue during which season?" to "Los ojos de los renos cambian a azul en qué estación:",
            listOf("Spring", "Summer", "Autumn", "Winter") to listOf("Primavera", "Verano", "Otoño", "Invierno"),
            3,
            "The shift improves contrast in dim Arctic winter light." to "El cambio mejora el contraste en la luz tenue del invierno ártico.",
        ),
        QuizQuestionEntry(
            "What does a kangaroo use as a 'third leg' for support?" to "¿Qué usa un canguro como 'tercera pata' para apoyarse?",
            listOf("Its tail", "A fold of skin", "A long claw", "A bone in the chest") to listOf("Su cola", "Un pliegue de piel", "Una garra larga", "Un hueso del pecho"),
            0,
            "The tail can support a kangaroo's full weight." to "La cola puede soportar todo el peso del canguro.",
        ),
        QuizQuestionEntry(
            "Which animal can hold its breath underwater for over an hour?" to "¿Qué animal puede aguantar la respiración bajo el agua más de una hora?",
            listOf("Polar bear", "Sea otter", "Crocodile", "Beaver") to listOf("Oso polar", "Nutria marina", "Cocodrilo", "Castor"),
            2,
            "Crocodiles slow their metabolism dramatically while submerged." to "Los cocodrilos ralentizan drásticamente su metabolismo bajo el agua.",
        ),
        QuizQuestionEntry(
            "What does an axolotl famously do that other amphibians don't?" to "¿Qué hace el ajolote que otros anfibios no?",
            listOf("Glow in the dark", "Stay a larva for life", "Walk on water", "Hibernate") to listOf("Brilla en la oscuridad", "Permanece como larva toda su vida", "Camina sobre el agua", "Hiberna"),
            1,
            "It never undergoes metamorphosis under normal conditions." to "Nunca sufre metamorfosis en condiciones normales.",
        ),
        QuizQuestionEntry(
            "How many wing-beats per second can a hummingbird reach?" to "¿Cuántos aleteos por segundo puede alcanzar un colibrí?",
            listOf("About 5", "About 20", "About 80", "About 200") to listOf("Unos 5", "Unos 20", "Unos 80", "Unos 200"),
            2,
            "Hummingbirds beat their wings up to ~80 times per second." to "Los colibríes baten sus alas hasta unas 80 veces por segundo.",
        ),
        QuizQuestionEntry(
            "Which animal can rotate its head 270 degrees?" to "¿Qué animal puede girar la cabeza 270 grados?",
            listOf("Owl", "Sloth", "Both owl and sloth", "Cheetah") to listOf("Búho", "Perezoso", "Tanto búho como perezoso", "Guepardo"),
            2,
            "Owls and three-toed sloths can both turn their heads about 270°." to "Los búhos y los perezosos de tres dedos pueden girar la cabeza unos 270°.",
        ),
        QuizQuestionEntry(
            "Where do emperor penguins lay their eggs?" to "¿Dónde ponen sus huevos los pingüinos emperador?",
            listOf("On grass nests", "On the male's feet", "In ice burrows", "In tree hollows") to listOf("En nidos de hierba", "Sobre los pies del macho", "En madrigueras de hielo", "En huecos de árboles"),
            1,
            "Fathers incubate the egg on top of their feet for two months." to "Los padres incuban el huevo sobre sus pies durante dos meses.",
        ),
        QuizQuestionEntry(
            "Which sense do sharks rely on to detect blood from far away?" to "¿Qué sentido usan los tiburones para detectar sangre a gran distancia?",
            listOf("Vision", "Hearing", "Smell", "Touch") to listOf("Visión", "Oído", "Olfato", "Tacto"),
            2,
            "Great whites can sense one drop of blood in 100 litres of water." to "Los tiburones blancos pueden detectar una gota de sangre en 100 litros de agua.",
        ),
    )

    private val scenarioEntries: List<SurvivalScenarioEntry> = listOf(
        SurvivalScenarioEntry(
            "You meet a wolf alone in the forest. What do you do?" to "Te encuentras solo con un lobo en el bosque. ¿Qué haces?",
            listOf(
                SurvivalChoiceEntry("Run as fast as you can" to "Correr lo más rápido posible", "Running triggers chase instinct." to "Correr activa su instinto de persecución.", false),
                SurvivalChoiceEntry("Stand tall, back away slowly" to "Mantenerte erguido y retroceder despacio", "Calm posture and distance keep you safer." to "Una postura calmada y la distancia te mantienen más seguro.", true),
                SurvivalChoiceEntry("Climb the nearest tree" to "Trepar al árbol más cercano", "Wolves can wait you out — and you'll get tired." to "Los lobos pueden esperar — y tú te cansarás.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "A crocodile is sliding into the river beside you." to "Un cocodrilo se desliza al río junto a ti.",
            listOf(
                SurvivalChoiceEntry("Swim across quickly" to "Cruzar nadando rápido", "You're now lunch." to "Ahora eres su almuerzo.", false),
                SurvivalChoiceEntry("Stay 5 m back from the bank" to "Mantenerte a 5 m de la orilla", "Crocs ambush from the edge — distance saves you." to "Los cocodrilos atacan desde el borde — la distancia te salva.", true),
                SurvivalChoiceEntry("Throw rocks to scare it" to "Lanzarle piedras para asustarlo", "It just gets annoyed and stays." to "Solo se molesta y se queda.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "An eagle is circling above your trail in the mountains." to "Un águila vuela en círculos sobre tu sendero en la montaña.",
            listOf(
                SurvivalChoiceEntry("Wave arms and shout" to "Agitar los brazos y gritar", "Eagles avoid noisy, large-looking creatures." to "Las águilas evitan criaturas grandes y ruidosas.", true),
                SurvivalChoiceEntry("Lie down and play dead" to "Tumbarte y hacerte el muerto", "You look like easier prey now." to "Ahora pareces presa más fácil.", false),
                SurvivalChoiceEntry("Sprint across the open ground" to "Correr por terreno abierto", "You become a moving target." to "Te conviertes en un blanco en movimiento.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "A brown bear charges at you from a thicket." to "Un oso pardo carga hacia ti desde la maleza.",
            listOf(
                SurvivalChoiceEntry("Run downhill — bears can't run downhill" to "Correr cuesta abajo — los osos no pueden", "Myth. Bears outrun humans on any slope." to "Mito. Los osos superan a humanos en cualquier pendiente.", false),
                SurvivalChoiceEntry("Stand your ground, speak calmly, slowly retreat" to "Mantenerte firme, hablar con calma, retirarte despacio", "Most charges are bluffs — calm de-escalates." to "La mayoría de las cargas son fingidas — la calma desescala.", true),
                SurvivalChoiceEntry("Climb the closest tree" to "Trepar al árbol más cercano", "Brown bears climb trees and you'll be cornered." to "Los osos pardos trepan árboles y quedarás acorralado.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "You wake up at night and see a snake inside your tent." to "Despiertas de noche y ves una serpiente dentro de tu tienda.",
            listOf(
                SurvivalChoiceEntry("Quickly grab and throw it outside" to "Cogerla rápido y lanzarla fuera", "Most bites happen when people grab snakes." to "La mayoría de los mordiscos ocurren al agarrar serpientes.", false),
                SurvivalChoiceEntry("Stay still, slowly move away, give it an exit" to "Quedarte quieto, alejarte despacio, dejarle salida", "Snakes leave when they have a clear path." to "Las serpientes se van si tienen un camino libre.", true),
                SurvivalChoiceEntry("Shine a flashlight directly at its head" to "Apuntarle con linterna a la cabeza", "It feels threatened and may strike." to "Se siente amenazada y puede atacar.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "A bull moose blocks the trail in front of you." to "Un alce macho bloquea el sendero frente a ti.",
            listOf(
                SurvivalChoiceEntry("Wait quietly behind a tree until it leaves" to "Esperar en silencio detrás de un árbol hasta que se vaya", "Moose are dangerous when surprised. Patience wins." to "Los alces son peligrosos cuando se sorprenden. La paciencia gana.", true),
                SurvivalChoiceEntry("Walk past on the side, smiling" to "Pasar por el lado, sonriendo", "Moose charge anything they see as a threat in their space." to "Los alces atacan cualquier amenaza en su espacio.", false),
                SurvivalChoiceEntry("Throw your backpack to distract it" to "Lanzar tu mochila para distraerlo", "Now it's angry AND has your snacks." to "Ahora está enfadado Y tiene tu comida.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "A lion lifts its head from the tall grass and stares." to "Un león levanta la cabeza de la hierba alta y te mira.",
            listOf(
                SurvivalChoiceEntry("Stare back, raise your arms, retreat slowly" to "Mantén la mirada, levanta los brazos, retírate despacio", "Look big, look confident — turn back is for prey." to "Parece grande, parece firme — dar la espalda es para presas.", true),
                SurvivalChoiceEntry("Sprint to your vehicle" to "Correr a tu vehículo", "Sprinting triggers the chase reflex." to "Correr activa el reflejo de persecución.", false),
                SurvivalChoiceEntry("Crouch and pretend to be small" to "Agacharte y hacerte pequeño", "You just rebranded yourself as easy prey." to "Acabas de ofrecerte como presa fácil.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "A swarm of bees rises up around your head." to "Un enjambre de abejas se levanta alrededor de tu cabeza.",
            listOf(
                SurvivalChoiceEntry("Drop and lie flat on the ground" to "Tumbarte plano en el suelo", "Lying still doesn't deter them." to "Quedarte quieto no las disuade.", false),
                SurvivalChoiceEntry("Run in a straight line, cover your face" to "Correr en línea recta, cubrirte la cara", "Distance and a covered face are your best chance." to "La distancia y la cara cubierta son tu mejor opción.", true),
                SurvivalChoiceEntry("Jump in the nearest pond" to "Saltar al estanque más cercano", "Bees wait above the surface for you to come up." to "Las abejas esperan en la superficie a que salgas.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "You spot a shark fin while swimming offshore." to "Ves una aleta de tiburón mientras nadas mar adentro.",
            listOf(
                SurvivalChoiceEntry("Splash hard to look bigger" to "Salpicar fuerte para parecer más grande", "Splashing mimics distressed prey." to "Salpicar imita a una presa en apuros.", false),
                SurvivalChoiceEntry("Move calmly toward shore, keep eye contact" to "Moverte con calma hacia la costa, mantén el contacto visual", "Calm motion + facing the shark deters most species." to "El movimiento calmado y mirar al tiburón disuade a la mayoría.", true),
                SurvivalChoiceEntry("Dive deep and swim under it" to "Bucear profundo y nadar bajo él", "It hunts better than you swim." to "Caza mejor que tú nadas.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "A hippo yawns at you from a nearby river bank." to "Un hipopótamo bosteza desde la orilla del río cercana.",
            listOf(
                SurvivalChoiceEntry("Back away — a yawn is a threat display" to "Retírate — un bostezo es señal de amenaza", "That yawn is a warning. Distance saves you." to "Ese bostezo es una advertencia. La distancia te salva.", true),
                SurvivalChoiceEntry("Take a photo, move closer" to "Hacer una foto, acercarte", "Hippos kill more humans than lions." to "Los hipopótamos matan a más humanos que los leones.", false),
                SurvivalChoiceEntry("Splash water playfully" to "Salpicar agua de forma juguetona", "You just declared war." to "Acabas de declarar la guerra.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "A polar bear appears in the distance on the ice." to "Un oso polar aparece a lo lejos sobre el hielo.",
            listOf(
                SurvivalChoiceEntry("Group together, look big, retreat slowly" to "Agruparse, parecer grandes, retirarse despacio", "A unified group looks unattractive as prey." to "Un grupo unido luce poco apetecible como presa.", true),
                SurvivalChoiceEntry("Lie down behind a snow ridge" to "Tumbarte detrás de una cresta de nieve", "You become a perfect ambush target." to "Te conviertes en blanco perfecto de emboscada.", false),
                SurvivalChoiceEntry("Walk out alone to scare it off" to "Salir solo a asustarlo", "Polar bears actively hunt humans." to "Los osos polares cazan humanos activamente.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "A wild boar emerges with piglets nearby." to "Un jabalí salvaje aparece con sus crías cerca.",
            listOf(
                SurvivalChoiceEntry("Back off slowly without turning around" to "Retírate despacio sin darle la espalda", "Mothers defend ferociously — leave their space." to "Las madres defienden con ferocidad — sal de su espacio.", true),
                SurvivalChoiceEntry("Pet the piglets — they're cute" to "Acariciar a las crías — son adorables", "You won't survive the mother." to "No sobrevivirás a la madre.", false),
                SurvivalChoiceEntry("Make eye contact and hold ground" to "Mantener contacto visual y firmeza", "She'll read it as a challenge." to "Lo leerá como un desafío.", false),
            ),
        ),
        SurvivalScenarioEntry(
            "A king cobra rises 1.5 m off the path in front of you." to "Una cobra real se yergue 1,5 m frente a ti en el sendero.",
            listOf(
                SurvivalChoiceEntry("Freeze, then slowly retreat backwards" to "Quédate quieto, luego retrocede despacio", "Cobras strike at movement. Slow distance saves you." to "Las cobras atacan al movimiento. La distancia lenta te salva.", true),
                SurvivalChoiceEntry("Try to pin its head with a stick" to "Intentar inmovilizar su cabeza con un palo", "Even experts get bitten this way." to "Hasta los expertos son mordidos así.", false),
                SurvivalChoiceEntry("Throw your hat at it" to "Lanzarle tu sombrero", "Now it's a moving threat near you." to "Ahora es una amenaza que se mueve cerca de ti.", false),
            ),
        ),
    )
}
