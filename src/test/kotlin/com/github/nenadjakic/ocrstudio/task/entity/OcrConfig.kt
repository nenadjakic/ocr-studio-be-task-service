package com.github.nenadjakic.ocrstudio.task.entity

class OcrConfig(
    var language: String = "eng",
    var ocrEngineMode: OcrEngineMode = OcrEngineMode.DEFAULT,
    var pageSegmentationMode: PageSegmentationMode = PageSegmentationMode.MODE_3,
    var tessVariables: Map<String, String>? = null,
    var preProcessing: Boolean = false,
    var fileFormat: FileFormat = FileFormat.TEXT,
    var mergeDocuments: Boolean = false
) {
    enum class OcrEngineMode(val tesseractValue: Int, val descritpion: String) {
        LEGACY(0, "Legacy engine only."),
        LSTM(1, "Neural nets LSTM engine only."),
        LEGACY_LSTM(2, "Legacy + LSTM engines."),
        DEFAULT(3, "Default, based on what is available.");
    }

    enum class PageSegmentationMode(val tesseractValue: Int, val descritpion: String) {
        MODE_0(0, "Orientation and script detection (OSD) only."),
        MODE_1(1, "Automatic page segmentation with OSD."),
        MODE_2(2, "Automatic page segmentation, but no OSD, or OCR. (not implemented)"),
        MODE_3(3, "Fully automatic page segmentation, but no OSD. (Default)"),
        MODE_4(4, "Assume a single column of text of variable sizes."),
        MODE_5(5, "Assume a single uniform block of vertically aligned text."),
        MODE_6(6, "Assume a single uniform block of text."),
        MODE_7(7, "Treat the image as a single text line."),
        MODE_8(8, "Treat the image as a single word."),
        MODE_9(9, "Treat the image as a single word in a circle."),
        MODE_10(10 ,"Treat the image as a single character."),
        MODE_11(11 ,"Sparse text. Find as much text as possible in no particular order."),
        MODE_12(12 ,"Sparse text with OSD."),
        MODE_13(13 ,"Raw line. Treat the image as a single text line, bypassing hacks that are Tesseract-specific.")
    }
    enum class FileFormat {
        PDF, HOCR, TEXT;

        fun getExtension(): String = when (this) {
            PDF -> "pdf"
            HOCR -> "hocr"
            TEXT -> "txt"
        }
    }
}