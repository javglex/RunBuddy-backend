package skymonkey.com

interface Config {
    val jwtSecret: String
    val mongoConnectionString: String
    val awsAccessKey: String
    val awsSecretKey: String
    val awsBucketName: String
}

object EnvConfig : Config {
    override val jwtSecret: String
        get() = System.getenv("JWT_SECRET") ?: throw IllegalStateException("Missing MongoDb Atlas connection string")

    override val mongoConnectionString: String
        get() = System.getenv("MONGODB_CONNECTION_STRING") ?: throw IllegalStateException("Missing MongoDb Atlas connection string")

    override val awsAccessKey: String
        get() = System.getenv("AWS_ACCESS_KEY_ID") ?: throw IllegalStateException("Missing AWS access key")

    override val awsSecretKey: String
        get() = System.getenv("AWS_SECRET_ACCESS_KEY") ?: throw IllegalStateException("Missing AWS secret key")

    override val awsBucketName: String
        get() = System.getenv("AWS_BUCKET_NAME") ?: throw IllegalStateException("Missing AWS bucket name")
}