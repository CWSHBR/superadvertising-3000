package api

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertDoesNotThrow
import ru.cwshbr.integrations.superset.SupersetAPI
import kotlin.test.Test
import kotlin.test.assertTrue

class SupersetTest {
    @Test
    fun testCheckDashboardsExists(){
        runBlocking {
            assertDoesNotThrow {
                SupersetAPI.checkDashboardExists()
            }
        }
    }

    @Test
    fun testDashboardCreate(){
        runBlocking {
            assertDoesNotThrow {
                assertTrue {
                    SupersetAPI.loadSupersetDashboardPreset()
                }
            }
        }
    }
}