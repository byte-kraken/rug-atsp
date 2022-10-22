import demostubs.Registrar
import java.io.File
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
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

data class User(val username: String, val password: String, val ssn: Int)

class SocialSecurityAdmin {
    fun checkSSN(ssn: Int): Boolean = true
}

data class Authorization(val registrar: Registrar)

class TokenEngine() {
    fun generateToken(uuid: UUID, auth: Authorization, electionID: Int): Token {
        return Token(uuid, auth, electionID, generateRandomAlphanumericSequence(20))
    }

    companion object {
        fun generateRandomAlphanumericSequence(length: Int): String {
            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

            val random = SecureRandom()
            val bytes = ByteArray(length)
            random.nextBytes(bytes)

            return (bytes.indices).map { charPool[random.nextInt(charPool.size)] }.joinToString("")
        }
    }
}

class Vault(private val tokens: MutableSet<VaultEntry> = mutableSetOf()) {
    fun storeToken(uuid: UUID, token: Token, election: Election) {
        tokens.add(VaultEntry(uuid, token, election))
    }

    fun verifyToken(uuid: UUID, hashedToken: String): Boolean {
        return tokens.any { it.uuid == uuid && it.token.hash() == hashedToken }
    }
}

data class VaultEntry(val uuid: UUID, val token: Token, val election: Election)

data class Token(val uuid: UUID, val auth: Authorization, val electionID: Int, val randomAlphanumericSequence: String) {
    fun hash(): String {
        return sha(this.toString())
    }
}

data class Ballot(
    val ID: Int,
    val uuid: UUID,
    val election: Election,
    val hashedToken: String,
    var status: BallotStatus = BallotStatus.Created,
    val candidates: List<Candidate>,
    var vote: Candidate?
)

data class Candidate(val id: String, val name: String)

enum class BallotStatus {
    Created, Delivered, Cast
}

// TODO
class BallotScan(val image: File) {
    fun hash(): String {
        return sha(image.toString())
    }

    companion object {
        fun dummy() = BallotScan(File(""))
    }
}

// TODO: Should probably also store the candidates
data class Election(val ID: Int)

class RegisteredVoterDatabase(private val registeredVoters: MutableMap<Election, MutableList<UUID>> = mutableMapOf()) {
    fun checkExists(uuid: UUID, election: Election): Boolean {
        return registeredVoters[election]?.contains(uuid) ?: false
    }

    fun addElection(election: Election) {
        registeredVoters[election] = mutableListOf()
    }

    fun addVoter(uuid: UUID, election: Election) {
        registeredVoters[election]?.add(uuid) ?: run {
            addElection(election)
            registeredVoters[election]!!.add(uuid)
        }
    }
}

/**
 * Creates a SHA-256 encrypted hex string from password
 */
fun sha(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest().joinToString(separator = "") { "%02x".format(it) }
}