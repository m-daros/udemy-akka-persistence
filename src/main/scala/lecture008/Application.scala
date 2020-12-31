package lecture008

import akka.actor.{ActorSystem, Props}
import lecture008.actors.VotingManagerActor
import lecture008.commands.{ShowState, Vote}

object Application extends App {

  val actorSystem: ActorSystem = ActorSystem ( "lecture008" )

  val votingManagerActor = actorSystem.actorOf ( Props [ VotingManagerActor ], "votingManagerActor" )

  votingManagerActor ! Vote ( "citizen-0001", "Biden" )
  votingManagerActor ! Vote ( "citizen-0002", "Biden" )
  votingManagerActor ! Vote ( "citizen-0003", "Trump" )
  votingManagerActor ! Vote ( "citizen-0004", "Biden" )
  votingManagerActor ! Vote ( "citizen-0001", "Biden" )

  votingManagerActor ! ShowState

  //actorSystem.terminate ()
}