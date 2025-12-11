package com.mjtech.acbrlib.bal.data.repository

import com.mjtech.acbrlib.bal.data.source.ACBrLibBALManager
import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.weigh.repository.ScaleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class AcbrLibBalRepository(private val acbrLibBal: ACBrLibBALManager) : ScaleRepository {

    private var isConfigured: Boolean = false

    override fun configurarBalanca(): Flow<Result<Unit>> = flow {
        if (isConfigured) {
            emit(Result.Success(Unit))
            return@flow
        }
        emit(Result.Loading)
        try {

            // Configuração específica para a balança Toledo Prix 3 Fit
            withContext(Dispatchers.IO) {
                val lib = acbrLibBal.getLib()
                val balDeviceSession = "BAL_DEVICE"
                val balSession = "BAL"

                lib.configGravarValor(balDeviceSession, "Parity", "0")
                lib.configGravarValor(balDeviceSession, "Baud", "9600")
                lib.configGravarValor(balDeviceSession, "Data", "8")
                lib.configGravarValor(balDeviceSession, "Stop", "0")
                lib.configGravarValor(balDeviceSession, "Handshake", "0")

                lib.configGravarValor(balSession, "Porta", "/dev/ttyUSER0")
                lib.configGravarValor(balSession, "Modelo", "2")

                lib.configGravar()
            }

            isConfigured = true
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao configurar a balança: ${e.message}"))
        }
    }

    override fun lerPeso(): Flow<Result<Double>> = flow {
        emit(Result.Loading)
        try {
            val weight = withContext(Dispatchers.IO) {
                acbrLibBal.getLib().lePeso(1000)
            }
            emit(Result.Success(weight))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao ao obter o peso: ${e.message}"))
        }
    }

    override fun inicializar(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            withContext(Dispatchers.IO) {
                acbrLibBal.getLib().inicializar()
            }
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao ao inicializar ACBrLibBAL: ${e.message}"))
        }
    }

    override fun finalizar(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            withContext(Dispatchers.IO) {
                acbrLibBal.getLib().finalizar()
            }
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao ao finalizar ACBrLibBAL: ${e.message}"))
        }
    }

    override fun ativar(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            withContext(Dispatchers.IO) {
                acbrLibBal.getLib().ativar()
            }
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao ao ativar balança: ${e.message}"))
        }
    }

    override fun desativar(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            withContext(Dispatchers.IO) {
                acbrLibBal.getLib().desativar()
            }
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao ao desativar balança: ${e.message}"))
        }
    }
}