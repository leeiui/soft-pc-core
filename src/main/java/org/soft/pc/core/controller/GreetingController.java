package org.soft.pc.core.controller;

import org.soft.pc.core.model.Greeting;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
	
	@RequestMapping(path = "/greeting", method = RequestMethod.GET)
	public ResponseEntity<Greeting> greeting() {
		Greeting greeting = new Greeting(1, "First SpringBoot App!");
		return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);
	}

}
