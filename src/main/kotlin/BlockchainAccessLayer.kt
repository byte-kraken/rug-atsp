import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

class BlockchainAccessLayer() {
    val blockchain = Blockchain()
    val identityManager = IdentityManager()
    val keyStore = KeyStore()
    val securityAdmin = SocialSecurityAdmin()
    val ballots = listOf<Ballot>()
    val registeredVoterDatabase = RegisteredVoterDatabase()
    val registrar = Registrar()
    val tokenEngine = TokenEngine()
    val vault = Vault()

    /**
     * Implementation of Fig. 6
     *
     * Voter registers with <b>system</b> using gov issued secret (e.g. ssn) and desired username + pw; gov stores connection secret <-> user
     * Secret is checked, credentials stored (ID manager), public-private keys created and stored (key storage), voterID created (UUID)
     * Voter receives their voterID and private key
     */
    fun registerVoter(user: User): Pair<UUID, PrivateKey> {
        if (!securityAdmin.checkSSN(user.ssn)) throw IllegalArgumentException()
        val uuid = generateUUID()
        identityManager.store(uuid, user.username, sha(user.password))
        val (publicKey, privateKey) = generateKeyPair()
        keyStore.store(uuid, publicKey, privateKey)
        return Pair(uuid, privateKey)
    }

    private fun generateKeyPair(): Pair<PublicKey, PrivateKey> {
        // e.g. RSA key generation
        TODO("Not yet implemented")
    }

    private fun generateUUID(): UUID {
        // TODO: sensible UUID creation
        return UUID.randomUUID()
    }

    /**
     * Implementation of Fig. 7
     * TODO: Salting
     */
    fun login(uuid: UUID, username: String, password: String): Boolean {
        return identityManager.check(uuid, username, sha(password))
    }

    /**
     * Implementation of Fig. 8 #1
     */
    fun requestBallot(uuid: UUID, username: String, password: String, ballotID: Int): Ballot {
        if (!identityManager.check(uuid, username, password)) throw IllegalArgumentException("Wrong credentials")
        return ballots[ballotID]
    }

    /**
     * Implementation of Fig. 8 #2
     */
    fun submitBallot(uuid: UUID, username: String, password: String, ballot: Ballot) {
        if (!identityManager.check(uuid, username, password)) throw IllegalArgumentException("Wrong credentials")
        if (!registeredVoterDatabase.checkExists(uuid, ballot.election))
            throw IllegalArgumentException("Voter is not registered")

        blockchain.add(Node(uuid, ballot))
        // TODO: Potentially extend to detect multiple votes
    }

    /**
     * Implementation of Fig 9
     *
     * Voter registers for a specific election.
     */
    fun registerForElection(uuid: UUID, election: Election): Boolean {
        blockchain.register(uuid, election)
        // TODO: optionally: register to USPS as well

        // asynchronous waiting for registrar
        Thread {
            val auth = registrar.verifyAndApproveRegistration(uuid, election)
                ?: throw IllegalStateException("Registrar could not approve registration.")
            blockchain.approve(uuid, election, auth)

            val token = tokenEngine.generateToken(uuid, auth, election.ID)

            val success = vault.storeToken(uuid, token, election)
            // TODO: Inform user and registrar
        }.start()
        return true
    }


}