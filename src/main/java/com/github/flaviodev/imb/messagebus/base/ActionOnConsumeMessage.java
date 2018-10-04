package com.github.flaviodev.imb.messagebus.base;

import com.google.common.collect.ImmutableMap;

import lombok.NonNull;

@FunctionalInterface
public interface ActionOnConsumeMessage<T> {
	void apply(@NonNull ImmutableMap<String, String> headers, @NonNull T payloadObject);
}
