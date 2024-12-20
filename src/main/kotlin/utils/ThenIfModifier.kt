package utils

import androidx.compose.ui.Modifier

fun Modifier.thenIf(condition: Boolean, modifier: Modifier?) =
    then(if (condition && modifier != null) modifier else Modifier)
