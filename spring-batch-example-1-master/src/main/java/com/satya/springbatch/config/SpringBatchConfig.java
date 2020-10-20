package com.satya.springbatch.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.satya.springbatch.batch.DBWriter;
import com.satya.springbatch.batch.Processor;
import com.satya.springbatch.model.User;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DBWriter dbwriter;

	@Autowired
	private Processor process;

	@Bean

	public Job job() {
		return jobBuilderFactory.get("job").incrementer(new RunIdIncrementer()).start(step()).build();
	}

	@Bean

	public Step step() {
		return stepBuilderFactory.get("step").<User, User>chunk((3))

				.reader(itemReader())

				.processor(process)

				.writer(dbwriter).taskExecutor(taskExecutor()).build();
	}

	@Bean
	@Qualifier
	@StepScope
	public MultiResourceItemReader<User> itemReader() {
		MultiResourceItemReader<User> resourceItemReader = new MultiResourceItemReader<User>();

		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
		// Resource[] res = null;

		try {
			Resource[] resources = resolver.getResources("file:c:/Users/sivan/Document/MyCsv/person*.csv");
			for (Resource resource : resources) {
				System.out.println("file names :" + resource.getFilename());
				// lisSrc.add(resource);
			}
			resourceItemReader.setResources(resources);
			resourceItemReader.setDelegate(reader());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("new file " + resourceItemReader.getCurrentResource());

		return resourceItemReader;
	}

	@Bean
	@Primary
	// @StepScope
	public FlatFileItemReader<User> reader() {
		// Create reader instance
		FlatFileItemReader<User> reader = new FlatFileItemReader<User>();
		// Set number of lines to skips. Use it if file has header rows.
		System.out.println("file names " + reader.toString());
		reader.setLinesToSkip(1);
		System.out.println(reader.toString());
		// Configure how each line will be parsed and mapped to different values
		reader.setLineMapper(new DefaultLineMapper<User>() {
			{
				// 3 columns in each row
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "id", "name", "dept", "salary" });
					}
				});
				// Set values in User class
				setFieldSetMapper(new BeanWrapperFieldSetMapper<User>() {
					{
						setTargetType(User.class);
					}
				});
			}
		});
		return reader;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(10);
		taskExecutor.afterPropertiesSet();
		taskExecutor.getActiveCount();

		return taskExecutor;
	}
}
