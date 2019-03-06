package cn.study.microservice.organization.event.publisher;

import cn.study.microservice.organization.event.model.OrgChangeModel;
import cn.study.microservice.organization.event.source.OrgChangeSource;
import cn.study.microservice.organization.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;

@EnableBinding(OrgChangeSource.class)
public class OrgChangePublisher {

	private static final Logger logger = LoggerFactory.getLogger(OrgChangePublisher.class);

	@Autowired
	private OrgChangeSource source;

	public void publish(String action, String orgId){
		OrgChangeModel model = new OrgChangeModel(
				OrgChangeModel.class.getTypeName(),
				action,
				orgId,
				UserContextHolder.getContext().getCorrelationId()
		);

		logger.info("sending rabbitmq message {} for Organization Id: {}",
				action,orgId);

		source.output().send(MessageBuilder.withPayload(model).build());
	}
}
