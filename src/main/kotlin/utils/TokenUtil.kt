package utils

import java.security.SecureRandom
import java.util.*

/**
 * An engine capable of crating unique tokens.
 */
class TokenEngine {
    /**
     * Generates a token based on a user id, a voting authorization, an election id and a random string.
     *
     * @param uuid the user id
     * @param auth the election authorization
     * @param electionID the election voted in
     * @return the created token
     */
    fun generateToken(uuid: UUID, auth: Authorization, electionID: Int): Token {
        return Token(uuid, auth, electionID, generateRandomAlphanumericSequence(20))
    }

    companion object {
        /**
         * Generates a cryptographically secure random alphanumeric sequence.
         *
         * @param length the length of the sequence
         * @return the created sequence
         */
        fun generateRandomAlphanumericSequence(length: Int): String {
            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

            val random = SecureRandom()
            val bytes = ByteArray(length)
            random.nextBytes(bytes)

            return bytes.map { charPool[random.nextInt(charPool.size)] }.joinToString("")
        }
    }
}

/**
 * A secure vault to store tokens in.
 *
 * @property tokens the stored tokens as VaultEntry objects
 */
class Vault(private val tokens: MutableSet<VaultEntry> = mutableSetOf()) {
    /**
     * Stores a token in the vault.
     *
     * @param uuid the user id the token belongs to
     * @param token the token itself
     * @param election the election the token is for
     * @return false if token was already in vault, true if storing was successful
     */
    fun storeToken(uuid: UUID, token: Token, election: Election): Boolean {
        return tokens.add(VaultEntry(uuid, token, election))
    }

    /**
     * Verifies that a token is in the vault.
     *
     * @param uuid the user id the token supposedly belongs to
     * @param hashedToken the hash of a token supposedly in the vault
     * @return true if the token does indeed exist in the vault, false otherwise
     */
    fun verifyToken(uuid: UUID, hashedToken: String): Boolean {
        return tokens.any { it.uuid == uuid && it.token.hash() == hashedToken }
    }

    /**
     * Gets a ballot token for a user id and election from the vault.
     *
     * [NOTE] This is obviously a very sensible function, since it provides access to the secret token.
     *   Naturally, the Registrar sending mail with the token must have access to it, however.
     *   As such, despite the descriptions in the patent, the vault was changed to be a field of registrar,
     *   giving the registrar control over its access.
     *
     * @param uuid the user id
     * @param election the election
     * @return the stored token for the combination of uuid and election
     */
    fun getToken(uuid: UUID, election: Election): Token {
        return tokens.first { it.uuid == uuid && it.election == election }.token
    }
}

/**
 * A helper class for vault symbolizing a vault entry
 *
 * @property uuid the user id
 * @property token the token
 * @property election the election
 */
data class VaultEntry(val uuid: UUID, val token: Token, val election: Election)

/**
 * A unique Token used on ballots to ensure that a ballot is valid.
 *
 * @property uuid the user the token is issued to
 * @property auth the authorization of a Registrar that the user may vote in an election
 * @property electionID the election the token is issued for
 * @property randomAlphanumericSequence a cryptographically random string
 */
data class Token(val uuid: UUID, val auth: Authorization, val electionID: Int, val randomAlphanumericSequence: String) {
    fun hash(): String {
        return sha(this.toString())
    }
}