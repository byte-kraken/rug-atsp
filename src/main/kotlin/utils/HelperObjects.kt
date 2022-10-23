package utils

import registrar.Registrar
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

/**
 * Stores the public-private key pairs of users.
 *
 * @property keys the keys of users
 */
class KeyStore(private val keys: MutableMap<UUID, Pair<PublicKey, PrivateKey>> = mutableMapOf()) {
    fun store(uuid: UUID, publicKey: PublicKey, privateKey: PrivateKey) {
        keys[uuid] = Pair(publicKey, privateKey)
    }
}

/**
 * Stores the connection between a user's id, their username and their password.
 *
 * @property identities a dictionary of the user's id to username and password
 */
class IdentityManager(private val identities: MutableMap<UUID, Pair<String, String>> = mutableMapOf()) {
    /**
     * Stores an uuid-username-password combination.
     *
     * @param uuid the unique id of a user
     * @param username the chosen name of a user
     * @param hashedPassword the hash of a user's chosen password
     */
    fun store(uuid: UUID, username: String, hashedPassword: String) {
        identities[uuid] = Pair(username, hashedPassword)
    }

    /**
     * Checks if an uuid-username-password combination exists.
     *
     * @param uuid the unique id of the user
     * @param username the name of the user
     * @param hashedPassword the user's hashed password
     * @return true if a user with those credentials exists, false otherwise
     */
    fun check(uuid: UUID, username: String, hashedPassword: String): Boolean {
        return identities.containsKey(uuid) && (identities[uuid]
            ?.first.equals(username) && identities[uuid]
            ?.second?.equals(hashedPassword) ?: false)
    }
}

/**
 * An election authorization created by a registrar.
 * Proves that a user is authorized to vote in an election.
 *
 * @property registrar the issuer of the authorization
 */
data class Authorization(val registrar: Registrar)

/**
 * An election that users can vote in. Can be set to only allow certain users to vote (e.g. a certain ZIP code).
 *
 * @property ID the unique id of the election
 * @property eligibleFunction determines who is allowed to vote (all if null)
 * @property status the current status of the election
 */
data class Election(
    val ID: Int,
    val eligibleFunction: ((UUID) -> Boolean)?,
    var status: ElectionStatus = ElectionStatus.Open
)

/**
 * The status of an election. Voting on closed elections should be prevented.
 */
enum class ElectionStatus {
    Open, Closed
}

/**
 * Creates a SHA-256 encrypted hex string from a string.
 *
 * @param password the string to hash
 */
fun sha(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(password.toByteArray()).joinToString(separator = "") { "%02x".format(it) }
}