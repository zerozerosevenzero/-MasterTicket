package com.example.masterticket.util

import com.opencsv.CSVWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileWriter

object CustomCSVWriter {
    private val logger: Logger = LoggerFactory.getLogger(CustomCSVWriter::class.java)

    fun write(fileName: String, data: List<Array<String>>): Int {
        var rows = 0
        try {
            CSVWriter(FileWriter(fileName)).use { writer ->
                writer.writeAll(data)
                rows = data.size
            }
        } catch (e: Exception) {
            logger.error("CustomCSVWriter - write: CSV 파일 생성 실패, fileName: {}", fileName)
        }
        return rows
    }
}