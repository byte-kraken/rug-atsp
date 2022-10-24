import absenteeBallotFiller.ConsoleAbsenteeBallotFiller
import ballotScanner.ConsoleBallotScanner
import observerPattern.PostBox
import observerPattern.WormholeMailService
import registrar.DemoRegistrar
import registrar.DemoSocialSecurityAdministration
import user.ConsoleUser
import utils.AbsenteeBallotRequest
import utils.Ballot
import utils.RSAKeyPairGenerator
import utils.Vault
import java.util.*

val mailService = WormholeMailService()

val bacLayer = BlockchainAccessLayer(
    ConsoleBallotScanner(),
    DemoRegistrar(Vault()),
    mailService,
    DemoSocialSecurityAdministration(),
    RSAKeyPairGenerator()
)
val user = ConsoleUser()
val ballotFiller = ConsoleAbsenteeBallotFiller()

fun main() {
    DemoRegistrar.populateWithDummyElections(bacLayer)
    DemoRegistrar.populateWithDummyBallots(bacLayer)

    val (uuid, username, password) = createAndRegisterUser()
    println("\nSuccessfully logged in. Welcome $username!")

    registerForElection(uuid)

    Thread.sleep(1000) // give registrar time to authorize us

    val absenteeBallotForm = requestAbsenteeBallot(uuid, username, password)

    val ballot = waitForBallot(uuid, absenteeBallotForm)

    println("\n ### Voting ###")
    bacLayer.scanBallotAndVote(ballot)

    println("\n ### Sending paper ballot ###")
    println("The original ballot must now be sent to the government via mail service and can be checked there.")

    println("\n ### Inspecting blockchain contents ###")

    bacLayer.getBlockchainContents().forEach {
        println("# $it")
    }
}

/**
 * Busy waits for a ballot to arrive in the mail.
 *
 * @param uuid the user the ballot is for
 * @param absenteeBallotForm the ballot type that is being waited on
 * @return the arrived ballot
 */
fun waitForBallot(uuid: UUID, absenteeBallotForm: AbsenteeBallotRequest): Ballot {
    println("\n ### Starring at Postbox ###")

    val postBox = PostBox(uuid, absenteeBallotForm.template.ID)
    mailService.observe(postBox)

    var ballot: Ballot? = null

    while (ballot == null) {
        ballot = postBox.ballot
        Thread.onSpinWait()
    }

    println("Received ballot!")
    return ballot
}


/**
 * Requests an absentee ballot for a user.
 *
 * @param uuid the user's id
 * @param username the user's name
 * @param password the user's password
 * @return the completed absentee ballot request
 */
fun requestAbsenteeBallot(uuid: UUID, username: String, password: String): AbsenteeBallotRequest {
    println("\n  ### Requesting an Absentee Ballot ###")

    println("These are your registered elections. Would you like to request an absentee ballot for one of them?")
    val registeredElections = bacLayer.getRegisteredElections(uuid)
    val chosenElectionToRequestBallot = user.chooseElection(registeredElections)
        ?: throw IllegalStateException("You are not registered to any elections right now.")

    val absenteeBallotForm = bacLayer.requestAbsenteeBallotForm(uuid, username, password, chosenElectionToRequestBallot)
    ballotFiller.signForm(absenteeBallotForm)
    bacLayer.submitAbsenteeBallotRequest(uuid, username, password, absenteeBallotForm)
    println("Absentee ballot requested, please check your mail for a ballot.")

    return absenteeBallotForm
}

/**
 * Registers for an open election.
 *
 * @param uuid the user trying to register
 */
fun registerForElection(uuid: UUID) {
    println("\n  ### Registering for an election ###")
    println("These are your available elections:")
    val elections = bacLayer.getOpenElections(uuid)

    val chosenElectionToRegisterTo = user.chooseElection(elections)
        ?: throw IllegalStateException("There are no open elections available right now.")

    bacLayer.registerForElection(uuid, chosenElectionToRegisterTo)
    println("Registration request noted. A Registrar will verify it shortly!")
}

/**
 * Creates a user account and registers it as a voter.
 *
 * @return the credentials created
 */
fun createAndRegisterUser(): Triple<UUID, String, String> {
    println("\n  ### Creating a user account ###")
    val username = user.enterUsername()
    val password = user.enterPassword()
    val ssn = user.enterSSN()

    val (uuid, privateKey) = bacLayer.registerVoter(username, password, ssn)
    println("Registered User [$username] with UUID [$uuid] and a RSA private key.")

    return Triple(uuid, username, password)
}
