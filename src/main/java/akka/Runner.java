package akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Runner {
    public static void main(String[] args) throws Exception {
        List<String> sentences = Arrays.asList(
            "The quick brown fox tried to jump over the lazy dog and fell on the dog",
            "Dog is man's best friend",
            "Dog and Fox belong to the same family",
            "The dog was the first domesticated species",
            "The origin of the domestic dog is not clear"
        );

        ActorSystem system = ActorSystem.create();
        ActorRef sentenceCounter = system.actorOf(Props.create(SentenceCountActor.class));

        Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
        ExecutionContext executionContext = system.dispatcher();

        List<Future<Object>> futures = new ArrayList<>();
        for (String sentence : sentences) {
            Future<Object> ask = Patterns.ask(sentenceCounter, sentence, timeout);
            futures.add(ask);
        }

        Future<Iterable<Object>> sequence = Futures.sequence(futures, executionContext);
        Object result = Await.result(sequence, timeout.duration());
        System.out.println(result);

    }
}