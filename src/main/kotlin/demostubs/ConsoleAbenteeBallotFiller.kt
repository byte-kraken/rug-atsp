package demostubs

import AbsenteeBallot

class ConsoleAbenteeBallotFiller : AbsenteeBallotFiller {
    override fun signForm(ballot: AbsenteeBallot) {
        print("\nPlease sign here: ")
        System.console().readLine()
    }
}