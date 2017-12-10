package strohmfn.springtest.topic;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TopicService {

	List<Topic> topics = Arrays.asList(new Topic(1, "dog", "4 legs and a tail"), new Topic(2, "spider", "8 legs"),
			new Topic(3, "human", "2 legs and 2 arms"));

	public List<Topic> getAllTopics() {
		return topics;
	}
	
	public Topic getTopic(int id) {
		return topics.stream().filter(t -> t.getId() == id).findFirst().get();
	}

}