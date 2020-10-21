package com.satya.springbatch.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.satya.springbatch.batch.DBWriter;
import com.satya.springbatch.batch.Processor;
import com.satya.springbatch.controller.LoadController;
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

	@Value("file:c:/Users/sivan/Document/MyCsv/person*.csv")
	private Resource[] resources;

	List<Resource> listSource = new ArrayList<>();
	List<Resource> listDestination = new ArrayList<>();

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
		try {
			Resource[] resources = resolver.getResources("file:c:/Users/sivan/Document/MyCsv/person*.csv");
			for (Resource resource : resources) {
				System.out.println("File names are :" + resource.getFilename());
			}
			resourceItemReader.setResources(resources);
			resourceItemReader.setDelegate(reader());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("New file is" + resourceItemReader.getCurrentResource());

		return resourceItemReader;
	}

	@Bean
	@Qualifier
	@StepScope
	public MultiResourceItemReader<User> ItemReader2() {
		MultiResourceItemReader<User> resourceItemReader2 = new MultiResourceItemReader<User>();
		LoadController lcr=new LoadController();
		Resource[] res=lcr.getRes();
		if (res.length != 0) {
			resourceItemReader2.setResources(res);
			resourceItemReader2.setDelegate(reader());
			System.out.println("New file is" + resourceItemReader2.getCurrentResource());
			return resourceItemReader2;
		}
	//		else {
//			ClassLoader cl = this.getClass().getClassLoader();
//			ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
//			Resource[] Res = null;
//			try {
//				Resource[] resources = resolver.getResources("file:c:/Users/sivan/Document/MyCsv/person*.csv");
//				SpringBatchConfig sfg = new SpringBatchConfig();
//
//				for (Resource resource : resources) {
//					System.out.println("resource names SECOND " + resource.getFilename());
//					listDestination.add(resource);
//				}
//				resourceItemReader2.setResources(resources);
//				resourceItemReader2.setDelegate(reader());
//
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			int size = listDestination.size() - listSource.size() - 1;
//			List<Resource> subResource = listDestination.subList(listSource.size(), (listDestination.size()));
//
//			for (Resource resource : subResource) {
//
//				System.out.println("Sub_List Is" + resource.getFilename());
//			}
//			Res = subResource.toArray(Res);
//			System.out.println("New file is" + resourceItemReader2.getCurrentResource());
//			return resourceItemReader2;
//		}
		return resourceItemReader2;
	}

	@Bean
	@Primary
	public FlatFileItemReader<User> reader() {
		FlatFileItemReader<User> reader = new FlatFileItemReader<User>();
		System.out.println("File Names are " + reader.toString());
		reader.setLinesToSkip(1);
		System.out.println(reader.toString());
		reader.setLineMapper(new DefaultLineMapper<User>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "id", "name", "dept", "salary" });
					}
				});
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
