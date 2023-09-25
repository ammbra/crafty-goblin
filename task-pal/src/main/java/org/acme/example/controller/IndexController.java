package org.acme.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/")
public class IndexController {

	@RequestMapping(method = RequestMethod.GET)
	public void redirect(HttpServletResponse response) throws IOException {
		response.sendRedirect("todo.html");
	}

	@RequestMapping( value = "hello",method = RequestMethod.GET)
	public String hello() {
		return "Hello";
	}
}
