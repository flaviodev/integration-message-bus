package com.github.flaviodev.employee.msgbus.base;

import java.util.List;

public interface TopicManagement {
	
	void createTopic(String topicName);
	
	void deleteTopic(String topicName);
	
	List<String> listTopics();
	
	void createSubscription(String subscriptionName, String nameTopic);
	
	void deleteSubscription(String subscriptionName);
	
	List<String> listSubscriptions();
	
	TopicManagement topicManagement();
}
