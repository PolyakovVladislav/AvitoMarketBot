package domain.exceptions

import domain.models.Notebook

class NotebookDetailsParsingException(
    val notebook: Notebook,
    val reason: String,
    val pageHtml: String
) : Exception(
    "Error occurred parsing $notebook\n" +
            "Reason: $reason" +
            "\n" +
            pageHtml
) {
}