package com.kls.dsgcodechallenge.extensions


// Extension to keep only the first letter of each word capitalized
// "DETROIT" -> "Detroit" or "NEW YORK" -> "New York"
fun capitalizeWords(input: String): String {
    return input.split(" ")
        .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercaseChar() } }
}