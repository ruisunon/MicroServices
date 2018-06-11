/**
 * 
 */
package com.person.service;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.person.config.HytrixConfig;
import com.person.dto.OrganizationDTO;
import com.person.dto.client.OrganizationClient;
import com.person.filter.UserContextHolder;

/**
 * @author chandresh.mishra
 *
 */
@Component
/*@DefaultProperties(
commandProperties= {
		 @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="11000") // after 11 sec call will be timeout// default for whole class.
})*/

public class OrganizationServiceData {
	
	private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceData.class);
	
	@Autowired
	private OrganizationClient organizationClient;
	
	@Autowired
	private HytrixConfig hytrixConfig;
	
	/*@HystrixCommand(fallbackMethod="buildFallBackOrganization"
	 ,threadPoolKey="organizationThreadPool",  // Following bulkhead pattern
	 threadPoolProperties=
	 {	@HystrixProperty (name="coreSize", value="30"), //request/sec at peak * 99 * latency/sec /100 
		@HystrixProperty(name="maxQueueSize", value="10") // It will use LinkedBlockingQueue to hold  the request	 
			 
	 },// Configured property for the Hystrix at method level.
	commandProperties={
            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),
            @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="7000"),
            @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds", value="15000"),
            @HystrixProperty(name="metrics.rollingStats.numBuckets", value="5")})*/
	public OrganizationDTO getOrganizationData(int OrganizationId)
	{
		logger.debug("OrganizationServiceData.getOrganizationData  Correlation id: {}", UserContextHolder.getContext().getCorrelationId());
		
		randomlyRunLong();  // Testing circuit breaker
		ResponseEntity<OrganizationDTO> reponse= organizationClient.getOrganization(OrganizationId);
		OrganizationDTO organizationDTO=reponse.getBody();
		return organizationDTO;
	}
	
	//FallBack method used by the Hytrix
	private  OrganizationDTO buildFallBackOrganization(int OrganizationId)
	{
		OrganizationDTO organizationDTO=new  OrganizationDTO();
		organizationDTO.setLocation("Unknown");
		organizationDTO.setOrganizationName("Unknown");
		return organizationDTO;
	}

	/*Testing Hytrix cirtuit Breaker
	{
	    "timestamp": 1518218372390,
	    "status": 500,
	    "error": "Internal Server Error",
	    "exception": "com.netflix.hystrix.exception.HystrixRuntimeException",
	    "message": "getPerson timed-out and fallback failed.",
	    "path": "/getPerson"
	}*/
	private void randomlyRunLong(){
		Random rand = new Random();
		int randomNum = rand.nextInt((3 - 1) + 1) + 1;
		if (randomNum==3) sleep();
		}
	
private void sleep(){
		try {
		Thread.sleep(12000);
		} catch (InterruptedException e) {
		e.printStackTrace();
		}
		}
}