import SocialSecurityAdmin.Companion.checkSSN
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*


class RegisteredVoterDatabase() {

}

/**
 * Implementation of Fig. 6
 *
 * Voter registers with system using gov issued secret (e.g. ssn) and desired username + pw; gov stores connection secret <-> user
 * Secret is checked, credentials stored (ID manager), public-private keys created and stored (key storage), voterID created (UUID)
 * Voter receives their voterID and private key
 */
fun registerVoter(user: User): Pair<UUID, PrivateKey> {
    if (!checkSSN(user.ssn)) throw IllegalArgumentException()
    val uuid = generateUUID()
    identityManager.store(uuid, user.username, sha(user.password))
    val (publicKey, privateKey) = generateKeyPair()
    keyStore.store(uuid, publicKey, privateKey)
    return Pair(uuid, privateKey)
}

/**
 * Creates a SHA-256 encrypted hex string from password
 */
fun sha(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest().joinToString(separator = "") { "%02x".format(it) }
}

/**
 * Implementation of Fig. 7
 * No salting
 */
fun login(uuid: UUID, username: String, password: String): Boolean {
    return identityManager.check(uuid, username, sha(password))
}


fun generateKeyPair(): Pair<PublicKey, PrivateKey> {
    // e.g. RSA key generation
    TODO("Not yet implemented")
}

fun generateUUID(): UUID {
    return UUID.randomUUID()
}

class KeyStore(private val keys: MutableMap<UUID, Pair<PublicKey, PrivateKey>>) {
    fun store(uuid: UUID, publicKey: PublicKey, privateKey: PrivateKey) {
        keys[uuid] = Pair(publicKey, privateKey)
    }
}

class IdentityManager(private val identities: MutableMap<UUID, Pair<String, String>>) {
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

data class User(val ssn: Int, val username: String, val password: String)

class SocialSecurityAdmin {
    companion object {
        fun checkSSN(ssn: Int): Boolean {
            return true
        }
    }
}

