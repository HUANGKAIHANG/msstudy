package cn.study.microservice.licenses.event.handler;

import cn.study.microservice.licenses.event.model.OrgChangeModel;
import cn.study.microservice.licenses.event.sink.OrgChangeSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(OrgChangeSink.class)
public class OrgChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(OrgChangeHandler.class);

	@StreamListener("orgChangeInput")
	public void handle(OrgChangeModel model){
		logger.info("Received a message of type "+ model.getType());

		switch (model.getAction()){
			case "get":
				logger.info("Received a GET event from the organization service" +
						"for organization id {}", model.getOrganizationId());
				break;
			case "save":
				logger.info("Received a SAVE event from the organization service" +
						"for organization id {}", model.getOrganizationId());
				break;
			case "update":
				logger.info("Received a UPDATE event from the organization service" +
						"for organization id {}", model.getOrganizationId());
				break;
			case "delete":
				logger.info("Received a DELETE event from the organization service" +
						"for organization id {}", model.getOrganizationId());
				break;
			default:
				logger.error("Received an UNKNOWN event from " +
					"the organization service of type {}", model.getType());
				break;
		}
	}
}
