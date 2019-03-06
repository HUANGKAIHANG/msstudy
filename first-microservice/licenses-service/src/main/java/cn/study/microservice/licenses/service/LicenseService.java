package cn.study.microservice.licenses.service;

import cn.study.microservice.licenses.client.OrganizationDiscoveryClient;
import cn.study.microservice.licenses.client.OrganizationFeignClient;
import cn.study.microservice.licenses.client.OrganizationRestTemplateClient;
import cn.study.microservice.licenses.domain.License;
import cn.study.microservice.licenses.domain.Organization;
import cn.study.microservice.licenses.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LicenseService {

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private OrganizationDiscoveryClient organizationDiscoveryClient;

    @Autowired
    private OrganizationRestTemplateClient organizationRestTemplateClient;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    private Organization retrieveOrgInfo(String organizationId, String clientType) {
        Organization organization = null;

        switch (clientType) {
            case "discovery":
                System.out.println("正在使用discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("正在使用Ribbon rest client");
                organization = organizationRestTemplateClient.getOrganization(organizationId);
                break;
            case "feign":
                System.out.println("正在使用Feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            default:
                System.out.println("使用默认client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
        }

        return organization;
    }

    public License getLicense(String organizationId, String licenseId, String clientType) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        System.out.println("获得的license是：" + license);
        Organization org = retrieveOrgInfo(organizationId, clientType);
        System.out.println("获得的org是：" + org);
        return license.withOrganizationName(org.getName())
                .withContactName(org.getContactName())
                .withContactEmail(org.getContactEmail())
                .withContactPhone(org.getContactPhone())
                .withComment("");
    }

    public List<License> getAll() {
        return (List<License>) licenseRepository.findAll();
    }
}
