package kotlinx.coroutines.test.internal

import kotlinx.coroutines.*

/**
 * A variant of [SupervisorJob] that additionally notifies about child failures via a callback.
 */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
internal class ReportingSupervisorJob(
    parent: Job? = null,
    val onChildCancellation: (Throwable) -> Unit
) : JobImpl(parent) {
    override fun childCancelled(cause: Throwable): Boolean =
        try {
            onChildCancellation(cause)
        } catch (e: Throwable) {
            cause.addSuppressed(e)
            /* the coroutine context does not matter here, because we're only interested in reporting this exception
            to the platform-specific global handler, not to a [CoroutineExceptionHandler] of any sort. */
            handleCoroutineException(this, cause)
        }.let { false }
}
