package dk.magenta.dafosts.saml;

import com.github.ulisesbocchio.spring.boot.security.saml.annotation.EnableSAMLSSO;
import dk.magenta.dafosts.library.DafoTokenGenerator;
import dk.magenta.dafosts.library.DatabaseQueryManager;
import dk.magenta.dafosts.library.SharedConfig;
import dk.magenta.dafosts.library.TokenGeneratorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
@EnableSAMLSSO
@ComponentScan(basePackages = {"dk.magenta.dafosts.saml", "dk.magenta.dafosts.library"})
@EnableConfigurationProperties(DafoStsBySamlConfiguration.class)
public class DafoStsBySamlApplication {

	/**
	 * Load shared config from dafo-sts-library
	 * @return SharedConfig bean
	 */
	@Bean
	SharedConfig sharedConfigBean() {
		return new SharedConfig();
	}

	/**
	 * Loads a DafoTokenGenerator bean
	 * @param tokenGeneratorProperties A TokenGeneratorProperties object with settings for the token generator
	 * @return DafoTokenGenerator bean
	 * @throws Exception
	 */
	@Bean
	DafoTokenGenerator dafoTokenGenerator(TokenGeneratorProperties tokenGeneratorProperties) throws Exception {
		return new DafoTokenGenerator(tokenGeneratorProperties);
	};

    /**
     * A bean for the querymanager
     * @param jdbcTemplate - the jdbcTemplate generated by Spring Boot.
     * @return The DatabaseQueryManager
     */
    @Bean
    DatabaseQueryManager databaseQueryManager(JdbcTemplate jdbcTemplate) {
        DatabaseQueryManager manager = new DatabaseQueryManager(jdbcTemplate);
        manager.getLocalIdpUsers();
        return manager;
    }


	public static void main(String[] args) throws Exception {
	    // Boostrap the Opensaml Library to use default configuration
        org.opensaml.DefaultBootstrap.bootstrap();
        // Run the application
		SpringApplication.run(DafoStsBySamlApplication.class, args);
	}
}
