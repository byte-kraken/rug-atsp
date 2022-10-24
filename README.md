# CA-SVS: a Central Authority Secure Voting System

This is an implementation of a patent on a ["Secure voting system"](https://patents.google.com/patent/US20200258338A1/en) proposed by the United States Postal Service.
It was done as part of a project in Advanced Topics in Security and Privacy at the University of Groningen.

The implementation is solely educational in nature and by no means a polished product, and the authors are conscious of gaping security flaws within it which are mostly due to specifications in the patent.

## Implementation Details
Most implementation details have been directly taken from the patent. We made extensive use of the diagrams and descriptions provided in the patent, they are linked accordingly in the KDoc.

As is the nature of patents, most aspects described in the patent were very vague and many assumptions have been made.<br>
We have also documented aspects of the code we considered needlessly complex or insecure using `[NOTE] comment`; 
several of these instances have already been fixed however, as we would have been unable to provide a working prototype otherwise.

The project was written in Kotlin.

## Running the Project
Naturally, the patent assumes the availability of several natural governmental actors.
We have provided mock-up services to allow for an integration test of the system without them.<br>
To test the system, simply run `UserMain.kt` and give console inputs as instructed.

## Extending the Project
Given the very limited scope of usage of a blockchain in the original patent, we decided against implementing communication with an actual blockchain.
Instead, we have created a more lightweight local blockchain class that reacts similiarly to a real blockchain, accepts transactions and is publically available.
