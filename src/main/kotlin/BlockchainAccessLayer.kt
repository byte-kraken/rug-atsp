import demostubs.BallotScanner
import demostubs.MailService
import demostubs.Registrar
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

class BlockchainAccessLayer(
    private val ballotScanner: BallotScanner,
    private val registrar: Registrar,
    private val mailService: MailService
) {
    private val blockchain = Blockchain()
    private val identityManager = IdentityManager()
    private val keyStore = KeyStore()
    private val securityAdmin = SocialSecurityAdmin()
    private val ballotDB = BallotDB()
    private val registeredVoterDatabase = RegisteredVoterDatabase()
    private val tokenEngine = TokenEngine()
    private val vault = Vault()

    /**
     * Implementation of Fig. 6
     *
     * Voter registers with <b>system</b> using gov issued secret (e.g. ssn) and desired username + pw; gov stores connection secret <-> user
     * Secret is checked, credentials stored (ID manager), public-private keys created and stored (key storage), voterID created (UUID)
     * Voter receives their voterID and private key
     */
    fun registerVoter(username: String, password: String, ssn: Int): Pair<UUID, PrivateKey> {
        if (!securityAdmin.checkSSN(ssn)) throw IllegalArgumentException()
        val uuid = generateUUID()
        identityManager.store(uuid, username, sha(password))
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
     *
     * User logs in
     * TODO: Salting
     */
    fun login(uuid: UUID, username: String, password: String): Boolean {
        return identityManager.check(uuid, username, sha(password))
    }


    /**
     * Implementation of Fig. 8 #1
     *
     * User requests and submits (absentee) ballot
     */
    fun requestAbsenteeBallotForm(uuid: UUID, username: String, password: String, ballotID: Int): AbsenteeBallot {
        if (!identityManager.check(uuid, username, password)) throw IllegalArgumentException("Wrong credentials")
        val ballotTemplate = ballotDB.getBallot(ballotID)
        return AbsenteeBallot(ballotTemplate)
    }

    /**
     * Implementation of Fig. 8 #2
     */
    fun submitAbsenteeBallotRequest(uuid: UUID, username: String, password: String, absenteeBallot: AbsenteeBallot) {
        if (!identityManager.check(uuid, username, password)) throw IllegalArgumentException("Wrong credentials")
        if (!registeredVoterDatabase.checkExists(uuid, absenteeBallot.template.election))
            throw IllegalArgumentException("Voter is not registered")

        blockchain.recordAbsenteeBallotIsOrdered(uuid, absenteeBallot)
        // TODO: Potentially extend to detect multiple votes
    }

    /**
     * Implementation of Fig 9
     *
     * Voter registers for a specific election.
     */
    fun registerForElection(uuid: UUID, election: Election): Boolean {
        blockchain.recordVoterRegistersForElection(uuid, election)
        // TODO: optionally: register to USPS as well

        // asynchronous waiting for registrar
        Thread {
            val auth = registrar.verifyAndApproveRegistration(uuid, election)
                ?: throw IllegalStateException("Registrar could not approve registration.")
            blockchain.recordRegistrarApprovesRegistrationToElection(uuid, election, auth)

            val token = tokenEngine.generateToken(uuid, auth, election.ID)

            val success = vault.storeToken(uuid, token, election)
            registeredVoterDatabase.register(uuid, election)
            registrar.notify(uuid, election, token.hash(), mailService)
        }.start()
        return true
    }

    /**
     * Implementation of Fig. 11
     * Scanning a ballot and voting
     */
    fun scanBallot(ballot: Ballot): Boolean {
        if (ballot.status == BallotStatus.Cast) throw IllegalStateException("Ballot was already used")

        if (!vault.verifyToken(ballot.uuid, ballot.hashedToken)) throw IllegalArgumentException("Invalid ballot")
        ballot.status = BallotStatus.Delivered

        val scannedBallot = ballotScanner.scan(ballot)
        blockchain.recordBallotFilledByVoter(ballot, scannedBallot)
        ballot.status = BallotStatus.Cast

        // TODO: ballot must be send to USPS
        return true
    }

    /**
     * Implementation of Fig. 12
     * Registrar creates election template
     */
    fun createElection() {
        val election = registrar.createElection()
        ballotDB.storeElectionRecord(election)
        registeredVoterDatabase.addElection(election)
        blockchain.recordElectionCreatedByRegistrar(election)
    }

    /**
     * Implementation of Fig. 13
     * Registrar creates ballot template
     */
    fun createBallotTemplate() {
        val ballotTemplate = registrar.createBallotTemplate()
        ballotDB.storeBallotTemplate(ballotTemplate)
        blockchain.recordBallotTemplateCreatedByRegistrar(ballotTemplate)
    }

    /**
     * Fetches all open elections a user can vote in.
     *
     * @param uuid: Some elections are only open to certain users (regional elections...)
     */
    fun getOpenElections(uuid: UUID): List<Election> {
        return ballotDB.getOpenElections().filter { it.eligibleFunction?.let { func -> func(uuid) } ?: true }
    }

    fun getRegisteredElections(uuid: UUID): List<Election> {
        return registeredVoterDatabase.getAllElectionsRegistered(uuid)
    }
}