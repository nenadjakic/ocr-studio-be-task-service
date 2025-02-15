package com.github.nenadjakic.ocrstudio.task.service

import com.github.nenadjakic.ocrstudio.task.config.OcrProperties
import org.apache.tika.config.TikaConfig
import org.apache.tika.detect.Detector
import org.apache.tika.mime.MediaType
import org.apache.tika.metadata.Metadata
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.util.*

@Service
class FileStorageService (
    private val ocrProperties: OcrProperties
){
    private val rootPath: String = ocrProperties.rootPath
    private val inputDirectoryName: String = "input"
    private val outputDirectoryName: String = "output"

    companion object {
        private val tikaConfig = TikaConfig()
        private val detector: Detector = tikaConfig.detector

        @Throws(IOException::class)
        fun getContentType(file: File): MediaType = detector.detect(cloneInputStream(file.inputStream()), Metadata())

        @Throws(IOException::class)
        fun getContentType(multiPartFile: MultipartFile): String? {
            var contentType = multiPartFile.contentType

            if ("application/octet-stream".equals(contentType, true)) {
                contentType = detector.detect(cloneInputStream(multiPartFile.inputStream), Metadata()).toString()
            }
            return contentType
        }

        @Throws(IOException::class)
        private fun cloneInputStream (inputStream: InputStream): InputStream {
            val byteArrayOutputStream = ByteArrayOutputStream()
            inputStream.transferTo(byteArrayOutputStream)

            return ByteArrayInputStream(byteArrayOutputStream.toByteArray())
        }
    }

    @Throws(IOException::class, InvalidPathException::class)
    fun getInputFile(taskId: UUID, randomizedFileName: String): File = Path.of(rootPath, taskId.toString(), inputDirectoryName, randomizedFileName).toFile()

    @Throws(IOException::class, InvalidPathException::class)
    fun getOutputFile(taskId: UUID, randomizedFileName: String): File = Path.of(rootPath, taskId.toString(), outputDirectoryName, randomizedFileName).toFile()

    @Throws(IOException::class, InvalidPathException::class)
    fun createDirectories(id: UUID) {
        val path = Path.of(rootPath, id.toString())
        Files.createDirectories(path)
        Files.createDirectory(path.resolve("input"))
        Files.createDirectory(path.resolve("output"))
    }

    @Throws(IOException::class, InvalidPathException::class)
    fun uploadFile(
        multiPartFile: MultipartFile,
        taskId: UUID,
        fileName: String,
        input: Boolean = true) {

        val targetFile = Path.of(
            ocrProperties.rootPath,
            taskId.toString(),
            (if (input) inputDirectoryName else outputDirectoryName),
            fileName
        ).toFile()
        multiPartFile.transferTo(targetFile.absoluteFile)
    }

    @Throws(IOException::class)
    fun deleteFile(file: Path) {
        Files.delete(file)
    }
    fun cleanUp(id: UUID) {
        deleteDirectoryRecursively(Path.of(ocrProperties.rootPath))
    }

    @Throws(IOException::class, SecurityException::class)
    private fun deleteDirectoryRecursively(path: Path) {
        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete)
    }
}