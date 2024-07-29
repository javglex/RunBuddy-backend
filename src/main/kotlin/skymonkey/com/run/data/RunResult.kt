package skymonkey.com.run.data

import skymonkey.com.run.domain.RunEntity

sealed interface RunResult {
    data class Success(val run: RunDto) : RunResult
    data class Failure(val message: String) : RunResult
}

sealed interface RunListResult {
    data class Success(val runList: List<RunDto>) : RunListResult
    data class Failure(val message: String) : RunListResult
}