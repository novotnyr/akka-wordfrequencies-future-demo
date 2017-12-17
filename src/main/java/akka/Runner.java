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

        List<Future<Object>> futures = sentences
                .stream()
                .map(sentence -> Patterns.ask(sentenceCounter, sentence, timeout))
                .collect(Collectors.toList());

        Future<HashMap<String, Integer>> fold = Futures.fold(new HashMap<>(), futures, new Function2<HashMap<String, Integer>, Object, HashMap<String, Integer>>() {
            @Override
            public HashMap<String, Integer> apply(HashMap<String, Integer> stringIntegerHashMap, Object o) throws Exception {
                Map<String, Integer> frequencies = (Map<String, Integer>) o;
                stringIntegerHashMap.putAll(frequencies);

                return stringIntegerHashMap;
            }
        }, executionContext);

        HashMap<String, Integer> result = Await.result(fold, timeout.duration());
        System.out.println(result);

    }
}