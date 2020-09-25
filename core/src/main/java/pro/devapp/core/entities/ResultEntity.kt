package pro.devapp.core.entities

/**
 * Base Structure for the response which might produce exception during execution
 * For example API response can be a success or failure as a result or can end with some exception
 */
sealed class ResultEntity<out T : Any> {
    /**
     * Success result
     */
    data class Success<out T : Any>(val value: T) : ResultEntity<T>()

    /**
     * Unexpected exception
     */
    data class Error(val cause: Exception? = null) : ResultEntity<Nothing>()
}