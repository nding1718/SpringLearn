package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) throws Exception {
        log.info("Creating tables");
        jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE customers(" + "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
        // split up the Array of whole names into an array of first/last names
        List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream().map(name -> name.split(" ")).collect(Collectors.toList());
        // use a java 8 stream to print out each tuple of the list;
        splitUpNames.forEach(name -> log.info(String.format("Inserting customer record of %s %s", name[0], name[1])));
        // use jdbctemplate's batchupate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);
        log.info("Querying for customers records where first_name='Josh':");
        jdbcTemplate.query("SELECT id, first_name, last_name FROM customers WHERE first_name=?", new Object[] {"Josh"}, (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"),rs.getString("last_name"))).forEach(customer -> log.info(customer.toString()));
    }
}
