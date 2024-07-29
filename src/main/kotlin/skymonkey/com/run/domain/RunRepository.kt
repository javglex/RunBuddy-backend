package skymonkey.com.run.domain

import skymonkey.com.run.data.RunDto
import skymonkey.com.run.data.RunListResult
import skymonkey.com.run.data.RunResult

interface RunRepository {
    suspend fun allRuns(userId: String): RunListResult
    suspend fun runById(id: String, userId: String): RunDto?
    suspend fun upsertRun(run: CreateRunRequest, mapPicture: ByteArray, userId: String): RunResult
    suspend fun removeRun(id: String, userId: String): Boolean
}