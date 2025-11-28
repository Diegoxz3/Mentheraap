package com.example.mentheraap.data

/**
 * Categorías de meditación
 */
enum class MeditationType(val displayName: String) {
    BODY_AWARENESS("Consciencia Corporal"),
    MINDFULNESS("Mindfulness"),
    LOVING_KINDNESS("Compasión"),
    GROUNDING("Anclaje"),
    VISUALIZATION("Visualización"),
    ACCEPTANCE("Aceptación")
}

/**
 * Representa una sesión de meditación guiada
 */
data class Meditation(
    val id: String,
    val name: String,
    val description: String,
    val benefits: String,
    val durationMinutes: Int,
    val type: MeditationType,
    val difficulty: Difficulty,
    val icon: String,
    val script: List<MeditationStep>
)

/**
 * Un paso en la meditación guiada
 */
data class MeditationStep(
    val durationSeconds: Int,
    val instruction: String,
    val voicePrompt: String // Texto que se mostraría/narraría
)

/**
 * Catálogo de meditaciones validadas científicamente
 */
object Meditations {
    val meditations = listOf(
        // 1. Body Scan (Escaneo Corporal)
        Meditation(
            id = "body_scan",
            name = "Escaneo Corporal",
            description = "Recorre tu cuerpo con atención plena, liberando tensión acumulada. " +
                    "Técnica fundamental del programa MBSR (Mindfulness-Based Stress Reduction).",
            benefits = "• Reduce tensión muscular\n" +
                    "• Mejora conexión mente-cuerpo\n" +
                    "• Disminuye somatización de ansiedad\n" +
                    "• Promueve relajación profunda",
            durationMinutes = 10,
            type = MeditationType.BODY_AWARENESS,
            difficulty = Difficulty.BEGINNER,
            icon = "🫀",
            script = listOf(
                MeditationStep(60, "Preparación", "Encuentra una posición cómoda, sentado o acostado. Cierra los ojos suavemente."),
                MeditationStep(30, "Respiración inicial", "Toma tres respiraciones profundas, sintiendo cómo el aire entra y sale de tu cuerpo."),
                MeditationStep(45, "Pies", "Lleva tu atención a tus pies. Observa cualquier sensación: temperatura, hormigueo, presión."),
                MeditationStep(45, "Piernas", "Sube tu atención hacia tus piernas. Sin juzgar, simplemente nota lo que hay."),
                MeditationStep(45, "Abdomen", "Ahora tu abdomen. Siente cómo se expande y contrae con cada respiración."),
                MeditationStep(45, "Pecho", "Lleva la atención a tu pecho. Nota los latidos de tu corazón, el movimiento de tu respiración."),
                MeditationStep(45, "Brazos y manos", "Escanea tus brazos y manos. Observa cualquier tensión y permítele estar ahí."),
                MeditationStep(45, "Hombros y cuello", "Atención a hombros y cuello. Estas áreas suelen guardar estrés. Solo obsérvalas."),
                MeditationStep(45, "Cara y cabeza", "Finalmente, tu rostro y cabeza. Relaja la mandíbula, las cejas, el entrecejo."),
                MeditationStep(60, "Cuerpo completo", "Siente tu cuerpo como un todo. Un organismo vivo, respirando, existiendo."),
                MeditationStep(45, "Gratitud", "Agradece a tu cuerpo por sostenerte. Por estar aquí, ahora."),
                MeditationStep(60, "Retorno", "Lentamente, mueve dedos de manos y pies. Cuando estés listo, abre los ojos.")
            )
        ),

        // 2. Mindfulness de Observación
        Meditation(
            id = "mindful_observation",
            name = "Observador de Pensamientos",
            description = "Aprende a ver tus pensamientos como nubes pasando en el cielo. " +
                    "No eres tus pensamientos, eres quien los observa.",
            benefits = "• Reduce identificación con pensamientos ansiosos\n" +
                    "• Crea distancia cognitiva\n" +
                    "• Disminuye rumia mental\n" +
                    "• Base de la terapia cognitiva mindfulness",
            durationMinutes = 5,
            type = MeditationType.MINDFULNESS,
            difficulty = Difficulty.BEGINNER,
            icon = "☁️",
            script = listOf(
                MeditationStep(45, "Anclaje", "Siéntate cómodamente. Cierra los ojos. Respira naturalmente."),
                MeditationStep(60, "Consciencia de respiración", "Nota tu respiración, sin cambiarla. Solo observa."),
                MeditationStep(90, "Aparición de pensamientos", "Pronto vendrán pensamientos. Esto es normal y está bien."),
                MeditationStep(90, "Metáfora del cielo", "Imagina que tu mente es el cielo. Los pensamientos son nubes que pasan."),
                MeditationStep(60, "Observar sin atrapar", "Cuando venga un pensamiento, nótalo. 'Ahí hay un pensamiento'. No lo sigas."),
                MeditationStep(90, "Dejar pasar", "Como nubes, los pensamientos vienen y van. Tú eres el cielo, no las nubes."),
                MeditationStep(30, "Retorno a la respiración", "Cada vez que notes que seguiste un pensamiento, vuelve gentilmente a tu respiración."),
                MeditationStep(45, "Cierre", "Abre los ojos lentamente. Nota cómo te sientes ahora.")
            )
        ),

        // 3. Loving Kindness (Metta)
        Meditation(
            id = "loving_kindness",
            name = "Autocompasión",
            description = "Cultiva bondad hacia ti mismo. La autocrítica alimenta la ansiedad; " +
                    "la autocompasión la calma.",
            benefits = "• Reduce autocrítica destructiva\n" +
                    "• Aumenta emociones positivas\n" +
                    "• Mejora resiliencia emocional\n" +
                    "• Disminuye vergüenza y culpa",
            durationMinutes = 8,
            type = MeditationType.LOVING_KINDNESS,
            difficulty = Difficulty.INTERMEDIATE,
            icon = "💚",
            script = listOf(
                MeditationStep(45, "Preparación", "Siéntate cómodamente. Coloca una mano sobre tu corazón."),
                MeditationStep(60, "Conexión contigo", "Siente el calor de tu mano. Reconoce que estás aquí, ahora, vivo."),
                MeditationStep(75, "Primera frase", "Repite mentalmente: 'Que yo esté libre de sufrimiento'."),
                MeditationStep(75, "Segunda frase", "'Que yo esté en paz'."),
                MeditationStep(75, "Tercera frase", "'Que yo sea bondadoso conmigo mismo'."),
                MeditationStep(75, "Cuarta frase", "'Que yo acepte quién soy en este momento'."),
                MeditationStep(90, "Sentir las palabras", "No solo digas las palabras. Siéntelas. Deséate esto de verdad."),
                MeditationStep(60, "Tu sufrimiento es válido", "Reconoce: tu ansiedad es real. No es tu culpa. Eres humano."),
                MeditationStep(90, "Repetir frases", "Vuelve a las frases. 'Que yo esté libre de sufrimiento. Que yo esté en paz.'"),
                MeditationStep(60, "Cierre", "Respira profundo. Agradécete por dedicar este tiempo a cuidarte.")
            )
        ),

        // 4. Meditación del Ancla
        Meditation(
            id = "anchor",
            name = "Ancla de Emergencia",
            description = "Para momentos de ansiedad aguda. Usa tus sentidos para volver al presente. " +
                    "Técnica de grounding de la terapia DBT.",
            benefits = "• Detiene espirales de pánico\n" +
                    "• Grounding inmediato\n" +
                    "• Portable, úsala en cualquier lugar\n" +
                    "• Efectiva en crisis",
            durationMinutes = 3,
            type = MeditationType.GROUNDING,
            difficulty = Difficulty.BEGINNER,
            icon = "⚓",
            script = listOf(
                MeditationStep(20, "Reconocer", "Estás sintiendo ansiedad. Eso está bien. Vamos a anclar juntos."),
                MeditationStep(30, "5 cosas que ves", "Nombra 5 cosas que puedes VER ahora mismo. No importa qué."),
                MeditationStep(30, "4 cosas que tocas", "4 cosas que puedes TOCAR. Tu ropa, la silla, el aire en tu piel."),
                MeditationStep(30, "3 cosas que oyes", "3 cosas que puedes OÍR. Sonidos cercanos o lejanos."),
                MeditationStep(30, "2 cosas que hueles", "2 cosas que puedes OLER. Aunque sea sutil."),
                MeditationStep(30, "1 cosa que saboreas", "1 cosa que puedes SABOREAR. Tu boca, un recuerdo de sabor."),
                MeditationStep(30, "Nota tus pies", "Siente tus pies en el suelo. Presiona firmemente. Estás aquí."),
                MeditationStep(30, "Respiración", "Tres respiraciones profundas. Uno... dos... tres."),
                MeditationStep(30, "Presente", "Estás en el presente. El futuro temido no está aquí. Estás a salvo ahora.")
            )
        ),

        // 5. Lugar Seguro
        Meditation(
            id = "safe_place",
            name = "Tu Refugio Interior",
            description = "Crea un espacio mental de seguridad. Tu mente puede ser tu refugio. " +
                    "Técnica usada en terapia EMDR para trauma.",
            benefits = "• Activa sistema nervioso parasimpático\n" +
                    "• Recurso interno de calma\n" +
                    "• Reduce hipervigilancia\n" +
                    "• Sensación de control",
            durationMinutes = 7,
            type = MeditationType.VISUALIZATION,
            difficulty = Difficulty.INTERMEDIATE,
            icon = "🏡",
            script = listOf(
                MeditationStep(45, "Preparación", "Cierra los ojos. Respira cómodamente."),
                MeditationStep(60, "Recordar o crear", "Piensa en un lugar donde te sientas seguro. Real o imaginario."),
                MeditationStep(60, "Visualizar", "Puede ser una playa, un bosque, tu cuarto de niño, un planeta inventado. Tú eliges."),
                MeditationStep(75, "Detalles visuales", "¿Qué ves? Colores, luces, formas. Observa cada detalle."),
                MeditationStep(75, "Sonidos", "¿Qué oyes en tu lugar seguro? Olas, pájaros, silencio, música."),
                MeditationStep(75, "Sensaciones táctiles", "¿Qué sientes en tu piel? Brisa, calor del sol, suavidad."),
                MeditationStep(75, "Olores", "¿Huele a algo? Sal marina, pino, pan recién horneado."),
                MeditationStep(90, "Sentimiento de seguridad", "Nota cómo tu cuerpo se relaja aquí. Estás completamente seguro."),
                MeditationStep(60, "Ancla", "Este lugar siempre existe dentro de ti. Puedes volver cuando lo necesites."),
                MeditationStep(45, "Retorno", "Gradualmente, regresa. Trae esa calma contigo al abrir los ojos.")
            )
        ),

        // 6. Aceptación
        Meditation(
            id = "acceptance",
            name = "Aceptación Radical",
            description = "Paradoja de la ansiedad: luchar contra ella la fortalece. " +
                    "Aceptarla la disminuye. Basado en ACT.",
            benefits = "• Reduce lucha interna\n" +
                    "• Disminuye evitación experiencial\n" +
                    "• Defusión cognitiva\n" +
                    "• Mayor flexibilidad psicológica",
            durationMinutes = 6,
            type = MeditationType.ACCEPTANCE,
            difficulty = Difficulty.ADVANCED,
            icon = "🤝",
            script = listOf(
                MeditationStep(45, "Consciencia", "Siéntate en silencio. Nota tu estado actual, sin juzgarlo."),
                MeditationStep(60, "Invitar", "Si hay ansiedad presente, no la alejes. Invítala a sentarse contigo."),
                MeditationStep(75, "Localizar", "¿Dónde la sientes en tu cuerpo? Pecho, garganta, estómago."),
                MeditationStep(75, "Describir sin juicio", "¿Cómo es? Caliente, fría, pesada, tensa. Solo observa."),
                MeditationStep(90, "Dar espacio", "En lugar de contraerte, expándete. Dale espacio a la sensación."),
                MeditationStep(75, "Mensaje", "Repite: 'Está bien que esto esté aquí. Puedo sostenerlo'."),
                MeditationStep(90, "Paradoja", "Nota algo curioso: al dejar de luchar, la ansiedad pierde fuerza."),
                MeditationStep(60, "Respirar con ella", "Respira junto a la sensación. No para eliminarla, sino para acompañarla."),
                MeditationStep(45, "Cierre", "Lentamente, abre los ojos. Agradece tu valentía de sentir.")
            )
        )
    )

    fun getById(id: String): Meditation? {
        return meditations.find { it.id == id }
    }

    fun getByType(type: MeditationType): List<Meditation> {
        return meditations.filter { it.type == type }
    }

    fun getByDifficulty(difficulty: Difficulty): List<Meditation> {
        return meditations.filter { it.difficulty == difficulty }
    }
}