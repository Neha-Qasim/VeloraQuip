package com.neha.veloraquip.util

import java.text.SimpleDateFormat
import java.util.*

private val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

fun formatTimeShort(ts: Long): String = if (ts == 0L) "" else sdf.format(Date(ts))
