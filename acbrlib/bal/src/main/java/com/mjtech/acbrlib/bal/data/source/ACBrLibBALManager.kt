package com.mjtech.acbrlib.bal.data.source

import br.com.acbr.lib.bal.ACBrLibBAL

class ACBrLibBALManager private constructor(
    appDir: String
) {

    private val ARQCONFIG_PADRAO = "ACBrLib.ini"
    private val acbrLibInstance: ACBrLibBAL

    init {
        val arqConfigPath = "$appDir/$ARQCONFIG_PADRAO"
        acbrLibInstance = ACBrLibBAL(arqConfigPath, "")
    }

    fun getLib(): ACBrLibBAL {
        return acbrLibInstance
    }

    companion object {
        @Volatile
        private var INSTANCE: ACBrLibBALManager? = null

        fun getInstance(appDir: String): ACBrLibBALManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ACBrLibBALManager(appDir).also { INSTANCE = it }
            }
    }
}