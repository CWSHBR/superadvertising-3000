package api

import kotlinx.coroutines.runBlocking
import org.junit.Test
import ru.cwshbr.integrations.nominatim.Nominatim
import kotlin.test.assertTrue

class NominatimTest {

    // тестируем что яхонты там, где должны быть
    @Test
    fun testGetPosition(){
        runBlocking {
            val position = Nominatim.getPlacePosition("Яхонты Ногинск")
            assertTrue(position != null, "position is null")
            assertTrue(position.latitude in 55.8997004..55.9001740, "wrong latitude")
            assertTrue(position.longitude in 38.5115489..38.5129070, "wrong longitude")
        }
    }

    // тестируем что грузинский вал 7 точно в москве
    @Test
    fun testGetBoundingBox(){
        runBlocking {
            val boundingBox = Nominatim.getPlaceBoundingBox("москва")
            assertTrue(boundingBox != null, "bounding box is null")
            assertTrue(55.77424625 in boundingBox.latitudeA..boundingBox.latitudeB,"wrong latitude")
            assertTrue(37.57722815 in boundingBox.longitudeA..boundingBox.longitudeB,"wrong longitude")
        }
    }
}