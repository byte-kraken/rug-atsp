import ballotScanner.BallotScanner
import observerPattern.MailService
import registrar.Registrar
import registrar.SocialSecurityAdministration
import utils.*
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

/**
 * A Blockchain access layer implementing most of the application's logic.
 * Provides functionality to communicate between different services, creates Blockchain transactions for certain actions.
 *
 * @property ballotScanner an external device that can scan a ballot (e.g. mobile phone)
 * @property registrar an external governmental entity that can authorize election registrations and create elections
 * @property mailService an external mail service that delivers ballots via mail to voters
 * @property securityAdmin an external administration that can authenticate a voter
 */
class BlockchainAccessLayer(
    private val ballotScanner: BallotScanner,
    private val registrar: Registrar,
    private val mailService: MailService,
    private val securityAdmin: SocialSecurityAdministration
) {
    private val blockchain = Blockchain()
    private val identityManager = IdentityManager()
    private val keyStore = KeyStore()
    private val ballotDB = BallotDB()
    private val registeredVoterDB = RegisteredVoterDB()
    private val tokenEngine = TokenEngine()
    private val vault = Vault()

    /**
     * When a voter first uses the election system, they need to register using a government issued secret (e.g. ssn).
     * Their Secret is checked, then the credentials are stored (ID manager),
     * public-private keys are created and stored (key storage),
     * and a voterID is created (UUID).
     *
     * @param username the user's desired username
     * @param password the user's desired password
     * @param ssn the user's secret, here a social security number
     *
     * @return The voter receives their voterID and private key
     *
     * @see <a href="https://patentimages.storage.googleapis.com/31/8c/d9/dbbd9fe988657e/US20200258338A1-20200813-D00012.png">
     *     Implementation of Fig 6 of patent </a>
     */
    fun registerVoter(username: String, password: String, ssn: Int): Pair<UUID, PrivateKey> {
        if (!securityAdmin.checkSSN(ssn)) throw IllegalArgumentException()
        val uuid = generateUUID()
        identityManager.store(uuid, username, sha(password))
        val (publicKey, privateKey) = generateKeyPair()
        keyStore.store(uuid, publicKey, privateKey)
        return Pair(uuid, privateKey)
    }

    /**
     * Generates a public-private key pair.
     *
     * @return the created pair
     */
    private fun generateKeyPair(): Pair<PublicKey, PrivateKey> {
        // e.g. RSA key generation
        TODO("Not yet implemented")
    }

    /**
     * Generates unique user identifier.
     *
     * @return the created uuid
     */
    private fun generateUUID(): UUID {
        // TODO: sensible UUID creation
        return UUID.randomUUID()
    }

    /**
     * Performs a login for a registered user by checking their credentials.
     * TODO: Salting
     *
     * @param uuid the user id
     * @param username the username
     * @param password the password
     *
     * @see <a href="https://patentimages.storage.googleapis.com/60/bd/a5/d6f15d2c99b429/US20200258338A1-20200813-D00013.png">
     *     Implementation of Fig 7 of patent </a>
     */
    fun login(uuid: UUID, username: String, password: String): Boolean {
        return identityManager.check(uuid, username, sha(password))
    }


    /**
     * Performs a user request for an absentee ballot form.
     *
     * @param uuid the user id of the requester
     * @param username the username used for authentication
     * @param password the password used for authentication
     * @param ballotID the ballot a form is requested for
     *
     *
     * @see <a href="https://patentimages.storage.googleapis.com/6d/10/99/b105d2e83fd74b/US20200258338A1-20200813-D00014.png">
     *     First part of implementation of Fig 8 of patent </a>
     */
    fun requestAbsenteeBallotForm(
        uuid: UUID,
        username: String,
        password: String,
        ballotID: Int
    ): AbsenteeBallotRequest {
        if (!identityManager.check(uuid, username, password)) throw IllegalArgumentException("Wrong credentials")
        val ballotTemplate = ballotDB.getBallot(ballotID)
        return AbsenteeBallotRequest(ballotTemplate)
    }

    /**
     * Performs a user request for an absentee ballot form.
     *
     * @param uuid the user id of the requester
     * @param username the username used for authentication
     * @param password the password used for authentication
     * @param absenteeBallotRequest the filled out absentee ballot request
     *
     * @see <a href="https://patentimages.storage.googleapis.com/6d/10/99/b105d2e83fd74b/US20200258338A1-20200813-D00014.png">
     *     Second part of implementation of Fig 8 of patent </a>
     */
    fun submitAbsenteeBallotRequest(
        uuid: UUID,
        username: String,
        password: String,
        absenteeBallotRequest: AbsenteeBallotRequest
    ) {
        if (!identityManager.check(uuid, username, password)) throw IllegalArgumentException("Wrong credentials")
        if (!registeredVoterDB.checkExists(uuid, absenteeBallotRequest.template.election))
            throw IllegalArgumentException("Voter is not registered")

        blockchain.recordAbsenteeBallotIsOrdered(uuid, absenteeBallotRequest)
        // TODO: Potentially extend to detect multiple votes
    }

    /**
     * Performs a voter registration for an election.
     *
     * @param uuid the user's id
     * @param election the election the user wants to register for
     * @return true if the registration was successful
     *
     * @see <a href="https://patentimages.storage.googleapis.com/d5/9b/1e/da99bb6172e3af/US20200258338A1-20200813-D00015.png">
     *     Implementation of Fig 9 of patent #1 </a> and
     *     <a href="https://patentimages.storage.googleapis.com/d5/9b/1e/da99bb6172e3af/US20200258338A1-20200813-D00016.png">
     *     Implementation of Fig 9 of patent #2 </a>
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
            registeredVoterDB.register(uuid, election)
            registrar.instructToSendBallot(uuid, election, token.hash(), mailService)
        }.start()
        return true
    }

    /**
     * Performs a scan of a filled out ballot and registers the vote.
     *
     * @param ballot the empty ballot before it is filled out
     * @return true if the vote was cast successfully
     *
     * @see <a href="https://patentimages.storage.googleapis.com/d5/9b/1e/da99bb6172e3af/US20200258338A1-20200813-D00019.png">
     *     Implementation of Fig 11 of patent #1 </a> and
     *     <a href="https://patentimages.storage.googleapis.com/d5/9b/1e/da99bb6172e3af/US20200258338A1-20200813-D00020.png">
     *     Implementation of Fig 11 of patent #2 </a>
     */
    fun scanBallotAndRegisterVote(ballot: Ballot): Boolean {
        if (ballot.status == BallotStatus.Cast) throw IllegalStateException("Ballot was already used")

        if (!vault.verifyToken(ballot.uuid, ballot.hashedToken)) throw IllegalArgumentException("Invalid ballot")
        ballot.status = BallotStatus.Delivered

        val scannedBallot = ballotScanner.scan(ballot)
        ballotDB.storeBallotScan(scannedBallot)
        blockchain.recordBallotFilledByVoter(ballot, scannedBallot)
        ballot.status = BallotStatus.Cast
        return true
    }

    // TODO: Create RegistrarMain and make project thread-able?
    /**
     * Creates an election via a registrar.
     *
     * @see <a href="https://patentimages.storage.googleapis.com/60/bd/a5/d6f15d2c99b429/US20200258338A1-20200813-D00021.png">
     *     Implementation of Fig 12 of patent </a>
     */
    fun createElection() {
        val election = registrar.createElection()
        ballotDB.storeElectionRecord(election)
        registeredVoterDB.addElection(election)
        blockchain.recordElectionCreatedByRegistrar(election)
    }

    /**
     * Creates a ballot template via a registrar.
     *
     * @see <a href="https://patentimages.storage.googleapis.com/60/bd/a5/d6f15d2c99b429/US20200258338A1-20200813-D00022.png">
     *     Implementation of Fig 13 of patent </a>
     */
    fun createBallotTemplate() {
        val ballotTemplate = registrar.createBallotTemplate()
        ballotDB.storeBallotTemplate(ballotTemplate)
        blockchain.recordBallotTemplateCreatedByRegistrar(ballotTemplate)
    }

    /**
     * Fetches all open elections a user can vote in.
     *
     * @param uuid the user id, since some elections are only open to certain users (regional elections...)
     * @return the filtered list of open elections
     */
    fun getOpenElections(uuid: UUID): List<Election> {
        return ballotDB.getOpenElections().filter { it.eligibleFunction?.let { func -> func(uuid) } ?: true }
    }

    /**
     * Fetches all elections a user is registered for.
     *
     * @param uuid the user's id
     * @return a list of elections
     */
    fun getRegisteredElections(uuid: UUID): List<Election> {
        return registeredVoterDB.getAllElectionsRegistered(uuid)
    }
}