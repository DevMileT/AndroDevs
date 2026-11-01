package com.androdevs.luckysevens

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 22:0
 *Year: 2026
 */

import android.app.Application
import com.androdevs.luckysevens.data.GameDatabaseHelper
import com.androdevs.luckysevens.data.GameRepositoryImpl
import com.androdevs.luckysevens.data.HistoryRepositoryImpl
import com.androdevs.luckysevens.domain.BonusRules
import com.androdevs.luckysevens.domain.DateProvider
import com.androdevs.luckysevens.domain.GameEngine
import com.androdevs.luckysevens.domain.GameRepository
import com.androdevs.luckysevens.domain.HistoryRepository
import com.androdevs.luckysevens.domain.RandomReelGenerator
import com.androdevs.luckysevens.domain.SystemDateProvider

/**
 * Punto único donde se crean el helper de SQLite y los repositorios (D-13, decisión 6): una
 * sola instancia por proceso. Crear el helper no abre la base de datos (eso ocurre en un
 * hilo de fondo la primera vez que se usa), así que `lazy` aquí es seguro.
 */
class LuckySevensApp : Application() {

    private val dateProvider: DateProvider by lazy { SystemDateProvider() }
    private val helper: GameDatabaseHelper by lazy { GameDatabaseHelper(this) }

    val gameRepository: GameRepository by lazy {
        GameRepositoryImpl(helper, GameEngine(RandomReelGenerator()), dateProvider = dateProvider)
    }
    val historyRepository: HistoryRepository by lazy { HistoryRepositoryImpl(helper) }
    val bonusRules: BonusRules by lazy { BonusRules(dateProvider) }
}