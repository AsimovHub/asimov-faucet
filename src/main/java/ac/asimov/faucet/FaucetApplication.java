package ac.asimov.faucet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication(scanBasePackages = {"ac.asimov", "ac.asimov.*"})
@EnableScheduling
@EnableAsync
@ComponentScan({"ac.asimov", "ac.asimov.*"})
public class FaucetApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaucetApplication.class, args);
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(5000);
		executor.setThreadNamePrefix("Asimov-Faucet-");
		executor.initialize();
		return executor;
	}

	/*
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:3000")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "PATCH")
						.allowCredentials(true);
			}
		};
	}
	 */
}
