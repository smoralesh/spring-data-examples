/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.springdata.jdbc.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.*;
import org.springframework.data.jdbc.mapping.model.JdbcMappingContext;
import org.springframework.data.jdbc.mybatis.MyBatisDataAccessStrategy;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

import static java.util.Arrays.asList;

/**
 * @author Jens Schauder
 */
@Configuration
@EnableJdbcRepositories
public class MyBatisConfiguration {

	// temporary workaround for https://jira.spring.io/browse/DATAJDBC-155
	@Bean
	DataAccessStrategy defaultDataAccessStrategy(JdbcMappingContext context, DataSource dataSource, SqlSessionFactory sqlSessionFactory) {

		NamedParameterJdbcOperations operations = new NamedParameterJdbcTemplate(dataSource);

		DelegatingDataAccessStrategy delegatingDataAccessStrategy = new DelegatingDataAccessStrategy();
		MyBatisDataAccessStrategy myBatisDataAccessStrategy = new MyBatisDataAccessStrategy(sqlSessionFactory);

		CascadingDataAccessStrategy cascadingDataAccessStrategy = new CascadingDataAccessStrategy(asList(myBatisDataAccessStrategy, delegatingDataAccessStrategy));

		DefaultDataAccessStrategy defaultDataAccessStrategy = new DefaultDataAccessStrategy( //
				new SqlGeneratorSource(context), //
				operations, //
				context, //
				cascadingDataAccessStrategy);

		delegatingDataAccessStrategy.setDelegate(defaultDataAccessStrategy);

		return cascadingDataAccessStrategy;
	}
}
