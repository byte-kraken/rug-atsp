package utils

import registrar.Registrar
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

class KeyStore(private val keys: MutableMap<UUID, Pair<PublicKey, PrivateKey>> = mutableMapOf()) {
    fun store(uuid: UUID, publicKey: PublicKey, privateKey: PrivateKey) {
        keys[uuid] = Pair(publicKey, privateKey)
    }
}

class IdentityManager(private val identities: MutableMap<UUID, Pair<String, String>> = mutableMapOf()) {
    /**
     * Stores an uuid-username-password combination
     */
    fun store(uuid: UUID, username: String, hashedPassword: String) {
        identities[uuid] = Pair(username, hashedPassword)
    }

    /**
     * Checks if an uuid-username-password combination is present
     */
    fun check(uuid: UUID, username: String, hashedPassword: String): Boolean {
        return identities.containsKey(uuid) && (identities[uuid]
            ?.first.equals(username) && identities[uuid]
            ?.second?.equals(hashedPassword) ?: false)
    }
}

class SocialSecurityAdmin {
    fun checkSSN(ssn: Int): Boolean = true
}

data class Authorization(val registrar: Registrar)


// TODO: Should probably also store the candidates
data class Election(
    val ID: Int,
    val eligibleFunction: ((UUID) -> Boolean)?,
    var status: ElectionStatus = ElectionStatus.Open
)

enum class ElectionStatus {
    Open, Closed
}

/**
 * Creates a SHA-256 encrypted hex string from password
 */
fun sha(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(password.toByteArray()).joinToString(separator = "") { "%02x".format(it) }
}