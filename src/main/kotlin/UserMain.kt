import absenteeBallotFiller.ConsoleAbsenteeBallotFiller
import ballotScanner.ConsoleBallotScanner
import observerPattern.PostBox
import observerPattern.WormholeMailService
import registrar.DemoRegistrar
import registrar.DemoSocialSecurityAdministration
import user.ConsoleUser
import utils.Ballot

val bacLayer = BlockchainAccessLayer(
    ConsoleBallotScanner(),
    DemoRegistrar(),
    WormholeMailService(),
    DemoSocialSecurityAdministration()
)

fun main() {
    val user = ConsoleUser()
    val ballotFiller = ConsoleAbsenteeBallotFiller()
    println("Creating a user account.")
    val username = user.enterUsername()
    val password = user.enterPassword()
    val ssn = user.enterSSN()

    val (uuid, privateKey) = bacLayer.registerVoter(username, password, ssn)
    println("Registered User with UUID [$uuid] and private key [$privateKey]")

    bacLayer.login(uuid, username, password)

    println("Welcome! These are your available elections.")
    val elections = bacLayer.getOpenElections(uuid)

    val chosenElectionToRegisterTo = user.chooseElection()
    bacLayer.registerForElection(uuid, elections[chosenElectionToRegisterTo])

    // In practice, there would be a pause here until the Registrar registers.
    println("Would you like to request an absentee ballot?")
    val registeredElections = bacLayer.getRegisteredElections(uuid)
    val chosenElectionToRequestBallot = user.chooseElection()

    val absenteeBallotForm = bacLayer.requestAbsenteeBallotForm(uuid, username, password, chosenElectionToRequestBallot)
    ballotFiller.signForm(absenteeBallotForm)
    bacLayer.submitAbsenteeBallotRequest(uuid, username, password, absenteeBallotForm)

    // waiting for mail service to send ballot in mail
    val postBox = PostBox(uuid, absenteeBallotForm.template.ID)
    val mailService = WormholeMailService()
    mailService.observe(postBox)

    var ballot: Ballot? = null
    while (ballot == null) {
        ballot = postBox.ballot
    }

    assert(bacLayer.scanBallotAndRegisterVote(ballot))
    // original ballot must now be sent to government via mail service and can be checked there
}
