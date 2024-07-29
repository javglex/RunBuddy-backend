package skymonkey.com

import skymonkey.com.run.data.RunListResult
import skymonkey.com.run.data.RunResult
import skymonkey.com.run.domain.*

class FakeRunsRepository : RunRepository {
    private val runEntities = mutableListOf(
        RunEntity("123", "user123@email.com","2012:12:11",1000,13, 31.0, 41.0, 213.0, 223.0, 11, "url", 1, 1),
        RunEntity("456","user123@email.com","2009:12:11",5677,15, 1.0, 11.0, 223.0, 223.0, 31, "url", 1, 1),
        RunEntity("987","user222@email.com", "2022:12:11",9786,231, 11.0, 15.0, 213.0, 223.0, 31, "url", 1, 1),
    )

    override suspend fun allRuns(userId: String): RunListResult =
        RunListResult.Success(
            runEntities
                .filter{
                    it.userId == userId
                }
                .map { it.toRunDto() }
        )

    override suspend fun runById(id: String, userId: String) = runEntities.find {
        it.id.equals(id, ignoreCase = true)
    }?.toRunDto()

    override suspend fun upsertRun(runRequest: CreateRunRequest, mapPicture: ByteArray, userId: String): RunResult {
        if (runById(runRequest.id, userId) != null) {
            throw IllegalStateException("Cannot have duplicate run id!")
        }
        val runEntity = runRequest.toRunEntity(userId, "www.url.com")
        runEntities.add(runEntity)
        return RunResult.Success(runEntity.toRunDto())
    }

    override suspend fun removeRun(id: String, userId: String): Boolean {
        return runEntities.removeIf { it.id == id }
    }
}