package com.kls.dsgcodechallenge.extensions

fun capitalizeWords(input: String): String {
    return input.split(" ")
        .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercaseChar() } }
}