package lecture008.actors

import akka.actor.ActorLogging
import akka.persistence.PersistentActor
import lecture008.commands.{ShowState, Vote}
import lecture008.events.{CitizenVoteRejected, CitizenVoted}

class VotingManagerActor extends PersistentActor with ActorLogging {

  // TODO Replace mutable state with immutable state and context.become
  var citizens: List [ String ] = List ()
  var votes: Map [ String, Int ] = Map ()

  override def persistenceId: String = "voting-manager-actor"

  override def receiveCommand: Receive = {

    case Vote ( citizenID: String, candidate: String ) => {

      if ( citizens.contains ( citizenID ) ) {

        val cause = "Already voted"
        persist ( CitizenVoteRejected ( citizenID, candidate, cause ) ) {

          citizenVoteRejetced => rejectVote ( citizenID, cause )
        }
      }
      else {

        persist ( CitizenVoted ( citizenID, candidate ) ) {

          citizenVoted => registerVote ( citizenID, candidate )
        }
      }
    }

    case ShowState => {

      log.info ( s"${citizens.size} citizens voted, votes are: $votes" )
    }
  }

  override def receiveRecover: Receive = {

    case CitizenVoted ( citizenID: String, candidate: String ) => registerVote ( citizenID, candidate )

    case CitizenVoteRejected ( citizenID: String, candidate: String, cause: String ) => rejectVote ( citizenID, cause )
  }

  private def registerVote ( citizenID: String, candidate: String ) = {

    citizens = citizens :+ citizenID
    votes = votes + ( candidate -> ( votes.getOrElse ( candidate, 0 ) + 1 ) )

    log.info ( s"Received vote for candidate: $candidate. Total votes: ${votes ( candidate )}" )
  }

  private def rejectVote ( citizenID: String, cause: String ) = {

    log.info ( s"Vote of citizen: $citizenID was rejected due to $cause" )
  }
}