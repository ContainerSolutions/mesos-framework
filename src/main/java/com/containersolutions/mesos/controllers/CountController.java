package com.containersolutions.mesos.controllers;

import com.containersolutions.mesos.scheduler.InstanceCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/mesos/resources")
public class CountController {
    private InstanceCount instanceCount;

    @Autowired
    public CountController(InstanceCount instanceCount) {
        this.instanceCount = instanceCount;
    }

    @RequestMapping(value = "/count", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> countEndpointPost(@RequestBody String count) {
        instanceCount.setCount(Integer.parseInt(count));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> countEndpointGet() {
        return new ResponseEntity<>(Integer.toString(instanceCount.getCount()), HttpStatus.OK);
    }
}
