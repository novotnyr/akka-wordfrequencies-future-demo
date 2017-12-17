package akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.japi.Function2;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.reflect.ClassTag$;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

        List<Future<Map<String, Integer>>> futures = sentences
                .stream()
                .map(sentence -> Patterns.ask(sentenceCounter, sentence, timeout))
                .map(f -> f.mapTo(ClassTag$.MODULE$.<Map<String, Integer>>apply(Map.class)))
                .collect(Collectors.toList());


        Future<Map<String, Integer>> fold = Futures.fold(new HashMap<>(), futures, new Function2<Map<String, Integer>, Map<String, Integer>, Map<String, Integer>>() {
            @Override
            public Map<String, Integer> apply(Map<String, Integer> allFrequencies, Map<String, Integer> frequencies) throws Exception {
                allFrequencies.putAll(frequencies);
                return allFrequencies;
            }
        }, executionContext);

        Map<String, Integer> result = Await.result(fold, timeout.duration());
        System.out.println(result);

    }
}