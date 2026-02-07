package app.eventbuzz.core.common

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable? = null, val message: String? = null) : Result<Nothing>
    data object Loading : Result<Nothing>
}

fun <T> Result<T>.successOrNull(): T? = when (this) {
    is Result.Success -> data
    else -> null
}

fun <T> Result<T>.errorMessageOrNull(): String? = when (this) {
    is Result.Error -> message ?: exception?.message
    else -> null
}
