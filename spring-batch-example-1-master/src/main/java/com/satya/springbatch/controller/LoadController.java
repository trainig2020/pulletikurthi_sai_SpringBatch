package com.satya.springbatch.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.satya.springbatch.config.SpringBatchConfig;

import javaxt.io.Directory;
import javaxt.io.Directory.Event;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/load")
public class LoadController {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job job;

	Resource[] res ;
	
	@RequestMapping("/home")
	public ModelAndView home() {
		return new ModelAndView("home");
	}

	@GetMapping
	public BatchStatus load() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
			JobRestartException, JobInstanceAlreadyCompleteException, javax.batch.operations.JobRestartException,
			IOException, InterruptedException {

		Map<String, JobParameter> maps = new HashMap<>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters parameters = new JobParameters(maps);
		JobExecution jobExecution = jobLauncher.run(job, parameters);
		System.out.println("JobExecution: " + jobExecution.getStatus());
		System.out.println("Batch is Running...");

		Directory folder = new Directory("C:\\Users\\sivan\\Document\\MyCsv");
		Directory folderCopy = new Directory("C:\\MyCsvfilescopy");
		try {
			sync(folder, folderCopy);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (jobExecution.isRunning()) {
			System.out.println("...");
		}

		return jobExecution.getStatus();
	}

	@RequestMapping("/uploadfiles")
	public ModelAndView handleFileUpload(HttpServletRequest request,
			@RequestParam("fileUpload") MultipartFile[] fileUpload) throws Exception {
		File folderCopy = new File("C:\\Users\\sivan\\Document\\MyCsv");
		Path pathCopy = folderCopy.toPath();
		if (fileUpload != null && fileUpload.length > 0) {

			for (MultipartFile multipartFile : fileUpload) {

				System.out.println("Saving file: " + multipartFile.getOriginalFilename());
				System.out.println("Content of file :"+ multipartFile.toString());

				try {
					byte[] bytes = multipartFile.getBytes();
					Path path = Paths.get(pathCopy +"\\" + multipartFile.getOriginalFilename());
					Files.write(path, bytes);
				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		}
		Map<String, JobParameter> maps = new HashMap<>();
		maps.put("time5", new JobParameter(System.currentTimeMillis()));
		JobParameters parameters = new JobParameters(maps);
		JobExecution jobExecution = jobLauncher.run(job, parameters);
		System.out.println("parameters are " + parameters.toString());
		System.out.println("JobExecution: " + jobExecution.getStatus().toString());

		return new ModelAndView("home");

	}
	@RequestMapping("/manualmodelist")
	public ModelAndView manualSchedule() {
		List<String> fileNames = new ArrayList<>();
		File folder = new File("C:\\Users\\sivan\\Document\\MyCsv");
		File[] ListFiles = folder.listFiles();

		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
		// Resource[] res = null;
		ModelAndView mdv = new ModelAndView("home");

		try {
			Resource[] resources = resolver.getResources("file:c:/Users/sivan/Document/MyCsv/person*.csv");

			for (Resource file : resources) {

				fileNames.add(file.getFilename());
			}
			mdv.addObject("fName", fileNames);
			mdv.addObject("manualtest", "checkmanual");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mdv;

	}

	@RequestMapping("/manualmode")
	public ModelAndView manualmodeSch(HttpServletRequest request, HttpServletResponse response) {

		Random rnum = new Random();

		String dateTimeLoc = request.getParameter("datetimeloc");

		String[] fileNames = request.getParameterValues("fnames");

		ClassLoader Cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Cl);
		ModelAndView sp = new ModelAndView("home");
		Resource[] res = new Resource[fileNames.length];

		int i = 0;
		try {
			Resource[] resources = resolver.getResources("file:c:/Users/sivan/Document/MyCsv/person*.csv");

			for (Resource resource : resources) {

				for (String resource2 : fileNames) {

					if (resource.getFilename().equalsIgnoreCase(resource2)) {
						res[i] = resource;
						i++;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Resource resource : res) {
			System.out.println("resources are selected " + resource.getFilename());
		}
		
TimerTask task=new TimerTask() {
	
	@Override
	public void run() {
		Map<String, JobParameter> maps=new HashMap<>();
		maps.put("time6", new JobParameter(System.currentTimeMillis()));
		JobParameters jobParameters=new JobParameters();
		try {
			JobExecution execution=jobLauncher.run(job, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
};

try {
	Date fDate=new SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ss").parse(dateTimeLoc);
	System.out.println(fDate);
	Timer timer=new Timer();
	timer.schedule(task, fDate);
}catch (ParseException e) {
	e.printStackTrace();
}
	return sp;
}

	public Resource[] getRes() {
		
		return res;
	}
	
	private void sync(Directory source, Directory destination) throws Exception {

		LoadController ld = new LoadController();
		java.util.List events = source.getEvents();
		while (true) {
			Event event;
			// Wait for new events to be added to the que
			synchronized (events) {
				while (events.isEmpty()) {
					try {
						// js.autoSchedule();
						System.out.println("waiting to do a event");
						events.wait();
						System.out.println("events are waiting");
					} catch (InterruptedException e) {
					}
				}
				event = (Event) events.remove(0);
			}
			int eventID = event.getEventID();
			System.out.println(eventID);
			if (eventID == Event.DELETE) {
				// Build path to the file in the destination directory
				String path = destination + "\\" + event.getFile().substring(source.toString().length());
				System.out.println("path is " + path);
				// Delete the file/directory
				new java.io.File(path).delete();
			} else {
				// Check if the event is associated with a file or directory so
				// we can use the JavaXT classes to create or modify the target.
				java.io.File obj = new java.io.File(event.getFile());
				if (obj.isDirectory()) {
					javaxt.io.Directory dir = new javaxt.io.Directory(obj);
					javaxt.io.Directory dest = new javaxt.io.Directory(
							destination + dir.toString().substring(source.toString().length()));
					switch (eventID) {
					case (Event.CREATE):
						dir.copyTo(dest, true);
						System.out.println("event creation");
						break;
					case (Event.MODIFY):
						System.out.println("event modification");
						break; // TODO
					case (Event.RENAME): {
						javaxt.io.Directory orgDir = new javaxt.io.Directory(event.getOriginalFile());
						dest = new javaxt.io.Directory(
								destination + orgDir.toString().substring(source.toString().length()));
						dest.rename(dir.getName());
						System.out.println("renaming");
						break;
					}
					}
				} else {
					javaxt.io.File file = new javaxt.io.File(obj);
					javaxt.io.File dest = new javaxt.io.File(
							destination + file.toString().substring(source.toString().length()));

					switch (eventID) {
					case (Event.CREATE):
						event.getFile();
						System.out.println("file name is " + event.getFile());
						LoadController lde = new LoadController();
						Map<String, JobParameter> maps = new HashMap<>();
						maps.put("time2", new JobParameter(System.currentTimeMillis()));
						JobParameters parameters = new JobParameters(maps);
						JobExecution jobExecution = jobLauncher.run(job, parameters);
						System.out.println("JobExecution: " + jobExecution.getStatus().toString());
						System.out.println("Batch is executed succesfully..");
						SpringBatchConfig spc = new SpringBatchConfig();
						ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) spc.taskExecutor();
						System.out.println(
								"value job " + job.getJobParametersIncrementer() + " " + taskExecutor.getActiveCount());
						System.out.println("else part created");
						break;
					case (Event.MODIFY):
						file.copyTo(dest, true);
						break;
					case (Event.RENAME): {
						javaxt.io.File orgFile = new javaxt.io.File(event.getOriginalFile());
						dest = new javaxt.io.File(
								destination + orgFile.toString().substring(source.toString().length()));
						dest.rename(file.getName());
						System.out.println("Remaned else part");
						break;

					}

					}
				}

			}

		}

	}
}
