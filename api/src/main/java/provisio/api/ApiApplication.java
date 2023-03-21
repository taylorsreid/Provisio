package provisio.api;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import java.util.Scanner;

@SpringBootApplication(scanBasePackages = "provisio.api", exclude=DataSourceAutoConfiguration.class)
public class ApiApplication {

	@Getter
	protected static String dbUsername;
	@Getter
	protected static String dbPassword;

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		//force manual entry of credentials so that they're not stored on github
		System.out.print("Enter database username: ");
		dbUsername = scanner.nextLine();
		System.out.println();
		System.out.print("Enter database password: ");
		dbPassword = scanner.nextLine();

		scanner.close();

		SpringApplication.run(ApiApplication.class, args);
	}

}
