package domain.models

import java.awt.image.BufferedImage

data class Notebook(
    val id: Long,
    val url: String,
    val title: String,
    val price: Int,
    val description: String
)