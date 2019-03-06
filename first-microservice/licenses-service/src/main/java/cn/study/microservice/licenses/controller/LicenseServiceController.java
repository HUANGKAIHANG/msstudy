package cn.study.microservice.licenses.controller;

import cn.study.microservice.licenses.domain.License;
import cn.study.microservice.licenses.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "v1/organizations/{organizationId}/licenses")
public class LicenseServiceController {

    @Autowired
    private LicenseService licenseService;

    @RequestMapping(value = "/{licenseId}/{clientType}", method = RequestMethod.GET)
    public License getLicenseWithClient(@PathVariable("organizationId") String organizationId,
                                        @PathVariable("licenseId") String licenseId,
                                        @PathVariable("clientType") String clientType) {
        return licenseService.getLicense(organizationId, licenseId, clientType);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<License> getAll() {
        return licenseService.getAll();
    }


//    @RequestMapping(value = "/{licenseId}", method = RequestMethod.GET)
//    public License getLicenses(@PathVariable("organizationId") String organizationId,
//                               @PathVariable("licenseId") String licenseId) {
//        return new License()
//                .withId(licenseId)
//                .withOrganizationId(organizationId)
//                .withProductName("Teleco")
//                .withLicenseType("Seat");
//    }
//
//    @RequestMapping(value = "/{licenseId}", method = RequestMethod.PUT)
//    public String updateLicenses(@PathVariable("licenseId") String licenseId) {
//        System.out.println("update method with PUT for " + licenseId);
//        return String.format("This is the PUT");
//    }
//
//    @RequestMapping(value = "/{licenseId}", method = RequestMethod.POST)
//    public String saveLicenses(@PathVariable("licenseId") String licenseId) {
//        System.out.println("save method with POST for " + licenseId);
//        return String.format("This is the POST");
//    }
//
//    @RequestMapping(value = "/{licenseId}", method = RequestMethod.DELETE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public String deleteLicenses(@PathVariable("licenseId") String licenseId) {
//        System.out.println("delete method with DELETE for " + licenseId);
//        return String.format("This is the DELETE");
//    }
}
