package com.example.zt_rvhor_marquee.danmurv

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.CoroutineContext

/**
 * start counting down from [duration] to 0 in a background thread and invoking the [onCountdown] every [interval] in main thread
 */
fun <T> countdown2(
    duration: Long,
    interval: Long,
    context: CoroutineContext = Dispatchers.Default,
    onCountdown: suspend (Long) -> T
): Flow<T> =
    flow { (duration - interval downTo 0 step interval).forEach { emit(it) } }
        .onEach { delay(interval) }
        .onStart { emit(duration) }
        .flatMapMerge { flow { emit(onCountdown(it)) } }
        .flowOn(context)

