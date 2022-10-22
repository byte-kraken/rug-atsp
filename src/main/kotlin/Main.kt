import java.security.MessageDigest

/**
 * Creates a SHA-256 encrypted hex string from password
 */
fun sha(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest().joinToString(separator = "") { "%02x".format(it) }
}




