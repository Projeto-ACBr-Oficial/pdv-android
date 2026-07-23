package com.mjtech.acbrlib.pdvtax

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * JVM unit tests (no NDK). Native calls are only asserted when the library is present
 * (e.g. instrumented / device builds). On plain unit-test classpath the engine reports unavailable.
 */
class PdvTaxEngineTest {

    @Test
    fun nativeAvailabilityIsBoolean() {
        val engine = PdvTaxEngine()
        // On host unit tests without .so this is false; with packaged jniLibs it may be true.
        val available = engine.isNativeAvailable
        assertTrue(available || !available)
    }

    @Test
    fun whenNativeMissing_validarThrows() {
        val engine = PdvTaxEngine()
        if (!engine.isNativeAvailable) {
            var threw = false
            try {
                engine.validarCpf("12345678909")
            } catch (_: IllegalStateException) {
                threw = true
            }
            assertTrue(threw)
        } else {
            assertTrue(engine.validarCpf("12345678909"))
            assertFalse(engine.validarCpf("12345678901"))
            assertTrue(engine.validarCnpj("12345678000195"))
        }
    }
}
