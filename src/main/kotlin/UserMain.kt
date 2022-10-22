import demostubs.*

val bacLayer = BlockchainAccessLayer(ConsoleBallotScanner(), DemoRegistrar(), WormholeMailService())


fun main() {
    val user = ConsoleUser()
    val ballotFiller = ConsoleAbenteeBallotFiller()
    println("Creating a user account.")
    val username = user.enterUsername() ?: "Demo"
    val password = user.enterPassword() ?: "hunter1"
    val ssn = user.enterSSN() ?: 123456789

    val (uuid, privateKey) = bacLayer.registerVoter(username, password, ssn)
    println("Registered User with UUID [$uuid] and private key [$privateKey]")

    bacLayer.login(uuid, username, password)

    println("Welcome! These are your available elections.")
    val elections = bacLayer.getOpenElections(uuid)

    val chosenElectionToRegisterTo = user.chooseElection()
    bacLayer.registerForElection(uuid, elections[chosenElectionToRegisterTo])

    // In practice, there would be a pause here until the Registrar registers.
    println("Would you like to request an absentee ballot?")
    user.enterYN()

    val registeredElections = bacLayer.getRegisteredElections(uuid)
    val chosenElectionToRequestBallot = user.chooseElection()

    val absenteeBallotForm = bacLayer.requestAbsenteeBallotForm(uuid, username, password, chosenElectionToRequestBallot)
    ballotFiller.signForm(absenteeBallotForm)
    bacLayer.submitAbsenteeBallotRequest(uuid, username, password, absenteeBallotForm)

    val mailService = WormholeMailService()

    // waiting for mail service to send ballot in mail
    val postBox = PostBox(uuid, absenteeBallotForm.template.ID)
    mailService.observe(postBox)

    var ballot: Ballot? = null
    while (ballot == null) {
        ballot = postBox.ballot
    }

    assert(bacLayer.scanBallot(ballot))
}
