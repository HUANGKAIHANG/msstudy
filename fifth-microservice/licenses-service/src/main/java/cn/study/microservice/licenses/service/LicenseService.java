package cn.study.microservice.licenses.service;

import cn.study.microservice.licenses.client.OrganizationDiscoveryClient;
import cn.study.microservice.licenses.client.OrganizationFeignClient;
import cn.study.microservice.licenses.client.OrganizationRestTemplateClient;
import cn.study.microservice.licenses.config.ServiceConfig;
import cn.study.microservice.licenses.domain.License;
import cn.study.microservice.licenses.domain.Organization;
import cn.study.microservice.licenses.repository.LicenseRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class LicenseService {

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private ServiceConfig serviceConfig;

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

    @HystrixCommand
    private Organization getOrganization(String organizationId) {
        return organizationRestTemplateClient.getOrganization(organizationId);
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
                .withComment(serviceConfig.getExampleProperty());
    }

    public List<License> getAll() {
        return (List<License>) licenseRepository.findAll();
    }

    //    @HystrixCommand(
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "12000")
//            }
//    )
/*
    Hystrix属性应该配置在spring cloud config server
    使用这样的解决方案，当你需要微调某些属性值时，
    可以在修改后刷新配置、重启服务实例，
    而不用重新编译、重新部署应用。
*/
    @HystrixCommand(fallbackMethod = "buildFallbackLicenseList",
            threadPoolKey = "licenseByOrgThreadPool",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30"),
                    @HystrixProperty(name = "maxQueueSize", value = "10")
            },
            commandProperties = {
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "75"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "15000"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "5")
//            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1000")
            })
    public List<License> getLicensesByOrg(String organizationId) {
        System.out.println("SERVICE CONFIG TEST " + serviceConfig.getExampleProperty());
        System.out.println("SERVICE CONFIG TEST " + serviceConfig.getTimeoutInMilliseconds());
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    private List<License> buildFallbackLicenseList(String organizationId) {
        List<License> fallbackList = new ArrayList<>();
        License license = new License()
                .withId("0000000-00-00000")
                .withOrganizationId(organizationId)
                .withProductName("Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
    }

    private void randomlyRunLong() {
        Random random = new Random();
        int randomNum = random.nextInt((3 - 1) + 1) + 1;
        System.out.println("随机数为：" + randomNum);
        if (randomNum == 3)
            sleep();
    }

    private void sleep() {
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}