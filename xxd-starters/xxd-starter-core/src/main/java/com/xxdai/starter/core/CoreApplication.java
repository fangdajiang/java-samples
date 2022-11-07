package com.xxdai.starter.core;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * same as @Configuration+@EnableAutoConfiguration+@ComponentScan
 * @author fangdajiang
 */
@SpringBootApplication
@RestController
@Slf4j
public class CoreApplication {

	public static void main(String[] args) {
		log.info("Ready to start xxd-starter-core");
		SpringApplication.run(CoreApplication.class, args);
		log.info("XxdStarterCoreApplication is running...");
	}

	@ApiImplicitParams({@ApiImplicitParam(paramType="header", name="clientId", value = "BOSS", defaultValue = "XXD_FRONT_END", required = true, dataType = "string"),
			@ApiImplicitParam(paramType="header", name="clientTime", value = "1459845047000", defaultValue = "1459845047000", required = true, dataType = "string"),
			@ApiImplicitParam(paramType="header", name="s", value = "md5", defaultValue = "71824bd75e1b757773d738537f2c9441", required = true, dataType = "string")})
	@RequestMapping(value = "/demo",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String helloworld() {
		log.debug("welcome, world");
		return "hello world";
	}
}
