package org.scoula.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {
        "org.scoula.chatgpt.util",
        "org.scoula.common.config",
        "org.scoula.mypage",
        "org.scoula"
})
//@PropertySource("file:${config.location}/application.properties")
@MapperScan(basePackages = {
        "org.scoula.community.board.mapper",
        "org.scoula.community.post.mapper",
        "org.scoula.community.comment.mapper",
        "org.scoula.community.postlike.mapper",
        "org.scoula.community.commentlike.mapper",
        "org.scoula.member.mapper",
        "org.scoula.community.board.mapper",
        "org.scoula.mypage.portfolio.mapper",
        "org.scoula.mypage.recentView.mapper",
        "org.scoula.mypage.favorite.mapper",
        "org.scoula.community.scrap.mapper"
})
public class RootConfig {

  @Value("${jdbc.driver}")
  String driver;
  @Value("${jdbc.url}")
  String url;
  @Value("${jdbc.username}")
  String username;
  @Value("${jdbc.password}")
  String password;
  @Autowired
  ApplicationContext applicationContext;
  @PostConstruct
  public void printProperties() {
    System.out.println("[TEST] jdbc.driver = " + driver);
    System.out.println("[TEST] jdbc.url = " + url);
    System.out.println("[TEST] jdbc.username = " + username);
    System.out.println("[TEST] jdbc.password = " + password);
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertyConfig() throws IOException {
    PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

    String configLocation = System.getProperty("config.location");
    if (configLocation == null) {
      throw new IllegalStateException("System property 'config.location' must be set");
    }

    System.out.println("[DEBUG] config.location: " + configLocation);
    System.out.println("[DEBUG] user.dir: " + System.getProperty("user.dir"));

    List<Resource> resources = new ArrayList<>();
    File baseFile = new File(configLocation + "/application.properties");
    if (baseFile.exists()) {
      resources.add(new FileSystemResource(baseFile));
    }

    // application.properties에서 active.profile을 직접 읽어옴
    Properties baseProps = new Properties();
    baseProps.load(new FileInputStream(baseFile));
    String activeProfiles = baseProps.getProperty("active.profile", "local");

    for (String profile : activeProfiles.split("\\s*,\\s*")) {
      String profilePath = configLocation + "/application-" + profile + ".properties";
      File profileFile = new File(profilePath);
      if (profileFile.exists()) {
        System.out.println("[INFO] Load profile config: " + profilePath);
        resources.add(new FileSystemResource(profileFile));
      } else {
        System.err.println("[WARN] Profile config not found: " + profilePath);
      }
    }

    // 최종 설정
    configurer.setLocations(resources.toArray(new Resource[0]));
    configurer.setIgnoreUnresolvablePlaceholders(true);

    return configurer;
  }

  @Bean
  public DataSource dataSource() {
    // HikariCP 설정 객체 생성
    HikariConfig config = new HikariConfig();

    // 데이터베이스 연결 정보 설정
    config.setDriverClassName(driver);          // JDBC 드라이버 클래스
    config.setJdbcUrl(url);                    // 데이터베이스 URL
    config.setUsername(username);              // 사용자명
    config.setPassword(password);              // 비밀번호

    // 커넥션 풀 추가 설정 (선택사항)
    config.setMaximumPoolSize(10);             // 최대 커넥션 수
    config.setMinimumIdle(5);                  // 최소 유지 커넥션 수
    config.setConnectionTimeout(30000);       // 연결 타임아웃 (30초)
    config.setIdleTimeout(600000);            // 유휴 타임아웃 (10분)

    // HikariDataSource 생성 및 반환
    HikariDataSource dataSource = new HikariDataSource(config);
    return dataSource;
  }

  /**
   * SqlSessionFactory 빈 등록
   * - MyBatis의 핵심 팩토리 객체를 스프링 컨테이너에 등록
   *
   * @param dataSource 위 dataSource() 메서드에서 등록된 bean이 주입됨
   */
  @Bean
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();

    // MyBatis 설정 파일 위치 지정
    sqlSessionFactory.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));

    // 데이터베이스 연결 설정
    sqlSessionFactory.setDataSource(dataSource);

    return sqlSessionFactory.getObject();
  }

  /**
   * 트랜잭션 매니저 설정
   * - 데이터베이스 트랜잭션을 스프링이 관리하도록 설정
   */
  @Bean
  public DataSourceTransactionManager transactionManager(DataSource dataSource) {
    DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
    return manager;
  }
}
