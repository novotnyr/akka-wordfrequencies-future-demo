package akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Runner {
    public static void main(String[] args) {
        List<String> sentences = Arrays.asList(
            "The quick brown fox tried to jump over the lazy dog and fell on the dog",
            "Dog is man's best friend",
            "Dog and Fox belong to the same family",
            "The dog was the first domesticated species",
            "The origin of the domestic dog is not clear"
        );

        ActorSystem system = ActorSystem.create();
        ActorRef master = system.actorOf(Props.create(Master.class));

        Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
        ExecutionContextExecutor executionContext = system.dispatcher();

        Future<Object> future = Patterns.ask(master, sentences, timeout);
        future.onSuccess(new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object o) {
                System.out.println(o);

                system.terminate();
            }
        }, executionContext);


    }
}