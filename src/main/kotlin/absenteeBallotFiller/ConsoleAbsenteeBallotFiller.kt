package absenteeBallotFiller

import utils.AbsenteeBallot

class ConsoleAbsenteeBallotFiller : AbsenteeBallotFiller {
    override fun signForm(ballot: AbsenteeBallot) {
        print("\nPlease sign here: ")
        System.console().readLine()
    }
}