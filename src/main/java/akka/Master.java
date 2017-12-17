package akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.reflect.ClassTag$;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Master extends UntypedActor {

    private ActorRef sentenceCounter = getContext().actorOf(Props.create(SentenceCountActor.class));

    private Timeout timeout = new Timeout(5, TimeUnit.SECONDS);

    @Override
    @SuppressWarnings("unchecked")
    public void onReceive(Object message) throws Throwable {
        if (message instanceof List) {
            handleSentences((List<String>) message);
        } else {
            unhandled(message);
        }
    }

    private void handleSentences(List<String> sentences) {
        ExecutionContext executionContext = getContext().dispatcher();

        List<Future<Map<String, Long>>> futures = sentences
                .stream()
                .map(sentence -> Patterns.ask(sentenceCounter, sentence, timeout))
                .map(f -> f.mapTo(ClassTag$.MODULE$.<Map<String, Long>>apply(Map.class)))
                .collect(Collectors.toList());

        Future<Map<String, Long>> fold = Futures.fold(new HashMap<>(), futures, (allFrequencies, frequencies) -> {
            allFrequencies.putAll(frequencies);
            return allFrequencies;
        }, executionContext);

        Patterns.pipe(fold, executionContext).pipeTo(getSender(), getSelf());
    }
}
