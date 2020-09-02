package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	Hibernate5Module hibernate5Module() {
		// 1. 아래의 경우, 지연로딩을 기다리지 않고 바로 보내버림
		return new Hibernate5Module();
		// 2. 아래와 같은 옵션을 넣은 경우, 지연로딩까지 기다린 후 데이터 전송
		// Hibernate5Module hibernate5Module = new Hibernate5Module();
		// hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		// return hibernate5Module;
	}
 
}