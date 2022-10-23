package utils

import java.io.File
import java.util.*

data class BallotTemplate(
    val ID: Int,
    val election: Election,
    val candidates: List<Candidate>
)

data class Ballot(
    val ID: Int,
    val uuid: UUID,
    val election: Election,
    val hashedToken: String,
    var status: BallotStatus = BallotStatus.Created,
    val candidates: List<Candidate>,
    var vote: Candidate?
)

data class AbsenteeBallot(val template: BallotTemplate)

data class Candidate(val id: String, val name: String)

enum class BallotStatus {
    Created, Delivered, Cast
}

// TODO
class BallotScan(private val image: File) {
    fun hash(): String {
        return sha(image.toString())
    }

    companion object {
        fun dummy() = BallotScan(File(""))
    }
}