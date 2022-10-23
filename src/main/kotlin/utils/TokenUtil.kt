package utils

import java.security.SecureRandom
import java.util.*

class TokenEngine {
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
    fun storeToken(uuid: UUID, token: Token, election: Election): Boolean {
        tokens.add(VaultEntry(uuid, token, election))
        return true
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