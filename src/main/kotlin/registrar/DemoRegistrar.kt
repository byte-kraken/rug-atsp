package registrar

import BlockchainAccessLayer
import observerPattern.MailService
import utils.*
import java.util.*

/**
 * A demo implementation of a Registrar that lacks critical analysis skill and blankly accepts all authorization request.
 */
class DemoRegistrar(private val vault: Vault) : Registrar {
    private val elections = mutableListOf<Election>()
    private val ballotTemplates = mutableListOf<BallotTemplate>()

    /**
     * Checks if a user is authorized to register for an election.
     * Our DemoRegistrar is not very good at their job and authorizes everybody.
     *
     * @param uuid the user
     * @param election the election
     * @return an Authorization object if the user should be authorized, otherwise null
     */
    override fun verifyAndApproveRegistration(uuid: UUID, election: Election): Authorization? {
        // simulates manual authorization check
        if (true) return Authorization(this)
        else return null
    }

    /**
     * Creates and stores an election that can be voted on.
     *
     * @return the election created
     */
    override fun createElection(election: Election): Boolean {
        return elections.add(election)
    }

    /**
     * Creates and stores a ballot template for all voters.
     * This template will later be personalized to each voter.
     *
     * @return the ballot template created
     */
    override fun createBallotTemplate(ballotTemplate: BallotTemplate): Boolean {
        return ballotTemplates.add(ballotTemplate)
    }

    /**
     * Stores a token in its vault.
     *
     * @param uuid the user id the token should be stored to
     * @param token the token that should be stored
     * @param election the election the token should be stored to
     */
    override fun storeToken(uuid: UUID, election: Election, token: Token) {
        vault.storeToken(uuid, token, election)
    }

    /**
     * Verifies that the given token exists for the given user.
     *
     * @param uuid the user that should have the token
     * @param hashedToken a hash of the token the user should have
     * @return true if the token exists, false otherwise
     */
    override fun verifyToken(uuid: UUID, hashedToken: String): Boolean {
        return vault.verifyToken(uuid, hashedToken)
    }

    /**
     * Is notified when a voter was successfully registered to the voter database.
     * On notification, creates a ballot for the mailservice to deliver to the voter.
     *
     * @param uuid the user that has been registered
     * @param election the election they have registered to
     * @param mailService the mailservice the ballot should be sent with
     */
    override fun instructToSendBallot(uuid: UUID, election: Election, mailService: MailService) {
        val ballotTemplate = ballotTemplates.first { it.election == election }
        Thread {
            println("[Registrar] Sending ballot in wormhole mail, expected delivery in 1 second!")
            Thread.sleep(1000)
            mailService.sendMail(uuid, ballotTemplate, vault.getToken(uuid, election))
        }.start()
    }

    companion object {
        private val dummyElection0 = Election(ID = 0, eligibleFunction = null, name = "British Prime Minister Vote")
        private val dummyElection1 = Election(
            ID = 1,
            eligibleFunction = { u -> (u.leastSignificantBits.toInt() % 2 == 0) },
            name = "Should users with UUIDs % 2 == 0 be allowed to vote?"
        ) // only allows half of all users to vote
        private val dummyElection2 =
            Election(ID = 2, eligibleFunction = null, name = "Should the USPS deliver all NFTs?")

        /**
         * Populates a blockchain with dummy elections.
         *
         * @param blockchainAccessLayer the bcAccLayer the elections should be created at
         */
        fun populateWithDummyElections(blockchainAccessLayer: BlockchainAccessLayer) {
            with(blockchainAccessLayer) {
                createElection(dummyElection0)
                createElection(dummyElection1)
                createElection(dummyElection2)
            }
        }

        /**
         * Populates a blockchain with dummy ballot templates.
         *
         * @param blockchainAccessLayer the bcAccLayer the ballot templates should be created at
         */
        fun populateWithDummyBallots(blockchainAccessLayer: BlockchainAccessLayer) {
            with(blockchainAccessLayer) {
                createBallotTemplate(
                    BallotTemplate(
                        0, dummyElection0, listOf(Candidate("Liz Truss"), Candidate("Cabbage McCabbageFace"))
                    )
                )
                blockchainAccessLayer.createBallotTemplate(
                    BallotTemplate(
                        1, dummyElection1, listOf(Candidate("Yes"), Candidate("No"))
                    )
                )
                createBallotTemplate(
                    BallotTemplate(
                        2, dummyElection2, listOf(Candidate("Yes"), Candidate("Yes!"))
                    )
                )
            }
        }
    }
}