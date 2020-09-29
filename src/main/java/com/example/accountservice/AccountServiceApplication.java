package com.example.accountservice;

import com.example.accountservice.models.User;
import com.example.accountservice.repository.UserRepository;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.ArrayList;


@SpringBootApplication
@EnableDiscoveryClient
public class AccountServiceApplication {

	@Autowired
	private UserRepository repository;

//	@Autowired
//	private PasswordEncoder encoder;
//	@PostConstruct
//	public void initUsers(){
//		ArrayList<User> users = new ArrayList<>();
//		User user1 = new User("Vanio","Ivan","Marinchev","i.marinchev@gmail.com","12345678","customer");
//		User user2 = new User("Bisle","Biser","Usufi","b.usufi@gmail.com","12345678","customer");
//		user1.setPassword(encoder.encode(user1.getPassword()));
//		user2.setPassword(encoder.encode(user2.getPassword()));
//		repository.save(user1);
//		repository.save(user2);
//	}
	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

}
