package com.github.flaviodev.imb.messagebus.base;

import com.google.common.collect.ImmutableMap;

public interface RedirectConsumerConfig extends ConsumerConfig {

	String getRedirectGroupName(ImmutableMap<String, Object> headers);

	boolean doesMeetTheRedirectionCondition(ImmutableMap<String, Object> headers);
}
