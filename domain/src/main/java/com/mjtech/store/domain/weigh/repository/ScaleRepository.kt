package com.mjtech.store.domain.weigh.repository

import com.mjtech.store.domain.common.Result
import kotlinx.coroutines.flow.Flow

interface ScaleRepository {

    fun lerPeso(): Flow<Result<Double>>

    fun inicializar(): Flow<Result<Unit>>

    fun finalizar(): Flow<Result<Unit>>

    fun ativar(): Flow<Result<Unit>>

    fun desativar(): Flow<Result<Unit>>

    fun configurarBalanca(): Flow<Result<Unit>>
}