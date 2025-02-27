package com.kls.dsgcodechallenge.ui.sharedviews

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun NetworkExceptionView(error: String) {
    AlertDialog(
        onDismissRequest = {  },
        confirmButton = {
            TextButton(onClick = {}) {
                Text(stringResource(id = android.R.string.ok))
            }
        },
        title = { Text(stringResource(id = com.kls.dsgcodechallenge.R.string.error)) },
        text = { Text(error) }
    )
}