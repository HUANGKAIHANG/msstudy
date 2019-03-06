package cn.study.microservice.organization.service;

import cn.study.microservice.organization.domain.Organization;
import cn.study.microservice.organization.event.publisher.OrgChangePublisher;
import cn.study.microservice.organization.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationService {

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private OrgChangePublisher orgChangePublisher;

	public Organization getOrg(String organizationId) {
		return organizationRepository.findById(organizationId);
	}

	public void saveOrg(Organization organization) {
		organization.setId(UUID.randomUUID().toString());
		organizationRepository.save(organization);
	}

	public void updateOrg(Organization organization) {
		organizationRepository.save(organization);
		orgChangePublisher.publish("update", organization.getId());
	}

	public void deleteOrg(Organization organization) {
		organizationRepository.delete(organization.getId());
		orgChangePublisher.publish("delete", organization.getId());
	}

	public List<Organization> getAll() {
		return (List<Organization>) organizationRepository.findAll();
	}
}
