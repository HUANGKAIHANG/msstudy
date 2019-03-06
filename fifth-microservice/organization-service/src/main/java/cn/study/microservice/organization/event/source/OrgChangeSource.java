package cn.study.microservice.organization.event.source;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OrgChangeSource {

	@Output("orgChangeOutput")
	MessageChannel output();
}
