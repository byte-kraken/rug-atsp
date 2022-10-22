import demostubs.ConsoleBallotScanner
import demostubs.DemoRegistrar


fun main() {
    val bacLayer = BlockchainAccessLayer(ConsoleBallotScanner(), DemoRegistrar())
    val username = "Demo"
    val password = "hunter1"
    val ssn = 123456789

    val (uuid, privateKey) = bacLayer.registerVoter(User(username, password, ssn))

    bacLayer.login(uuid, username, password)
    println("Welcome! These are your available ballots, would you like to register to an election?")

    val chosenBallot = 0

    val ballot = bacLayer.requestBallot(uuid, username, password, chosenBallot)
    bacLayer.scanBallot(ballot)
}

