package akka;

import akka.actor.UntypedActor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SentenceCountActor extends UntypedActor {
    @Override
	public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            String sentence = (String) message;
            Map<String, Long> frequencies = calculateFrequencies(sentence);
            getSender().tell(frequencies, getSelf());
        } else {
            unhandled(message);
        }
	}

    public Map<String, Long> calculateFrequencies(String sentence) {
        return Arrays.stream(sentence.split("\\W"))
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}