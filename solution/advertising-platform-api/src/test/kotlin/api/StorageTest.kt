package api

import org.junit.Test
import org.junit.jupiter.api.assertDoesNotThrow
import ru.cwshbr.integrations.objectstorage.ImageLoading
import kotlin.test.assertContentEquals

class StorageTest {
    val filename = "test-380f70bc-0ab7-42aa-96d1-48121903da3e"
    @Test
    fun testSaveImage(){
        assertDoesNotThrow {
            val bytes = {}.javaClass.getResourceAsStream("/a.jpg").readBytes()
            ImageLoading.saveImageToS3(filename, bytes)
        }
    }

    @Test
    fun testLoadImage(){
        assertDoesNotThrow {
            val bytes = {}.javaClass.getResourceAsStream("/a.jpg").readBytes()

            val newBytes = ImageLoading.getImageFromS3(filename)?.readBytes()

            assertContentEquals(bytes, newBytes)
        }
    }
}