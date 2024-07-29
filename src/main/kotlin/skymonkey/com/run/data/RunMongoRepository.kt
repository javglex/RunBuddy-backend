package skymonkey.com.run.data

import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.*
import skymonkey.com.aws.S3BucketClient
import skymonkey.com.run.domain.*
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import software.amazon.awssdk.services.s3.model.S3Exception
import java.io.IOException
import java.nio.file.Files

class RunMongoRepository(
    runDatabase: MongoDatabase
) : RunRepository {
    private val collection = runDatabase.getCollection<RunEntity>()
    val s3Client = S3BucketClient() //TODO: inject somehow and create interface/implementations


    override suspend fun allRuns(userId: String): RunListResult {
        return try {
            val runList = collection.find(RunEntity::userId eq userId).toList().map { it.toRunDto() }
            RunListResult.Success(runList)
        } catch (e: Exception) {
            RunListResult.Failure("could not find run list")
        }
    }

    override suspend fun runById(id: String, userId: String): RunDto? {
        return collection.findOne(and(RunEntity::id eq id, RunEntity::userId eq userId))?.toRunDto()
    }

    override suspend fun upsertRun(run: CreateRunRequest, mapPicture: ByteArray, userId: String): RunResult {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val tempFile = Files.createTempFile("mapPicture", ".jpg")
                Files.write(tempFile, mapPicture)
                val keyName = "uploads/${run.id}/mapPicture.jpg"
                val putObjectResponse: PutObjectResponse = s3Client.uploadFile(keyName, tempFile.toString())
                val mapPictureUrl = s3Client.getPublicUrl(keyName)
                Files.deleteIfExists(tempFile)
                val runWithMapPictureUrl = run.toRunEntity(userId, mapPictureUrl)
                collection.updateOne(RunEntity::id eq run.id, runWithMapPictureUrl, upsert())
                RunResult.Success(runWithMapPictureUrl.toRunDto())
            } catch (e: IOException) {
                RunResult.Failure("Failed to write image to file")
            } catch (e: S3Exception) {
                RunResult.Failure("Failed to upload image to S3")
            }
        }
    }

    override suspend fun removeRun(id: String, userId: String): Boolean {
        val result = collection.deleteOne(RunEntity::id eq id)
        return result.deletedCount > 0
    }
}