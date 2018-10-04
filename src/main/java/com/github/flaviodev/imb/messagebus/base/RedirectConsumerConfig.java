package com.github.flaviodev.imb.messagebus.base;

import com.google.common.collect.ImmutableMap;

public interface RedirectConsumerConfig<T> extends ConsumerConfig<T> {

	String getRedirectGroupName(ImmutableMap<String, String> headers);

	boolean doesMeetTheRedirectionCondition(ImmutableMap<String, String> headers);
}
