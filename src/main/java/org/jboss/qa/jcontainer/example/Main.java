package org.jboss.qa.jcontainer.example;

import org.apache.commons.lang3.StringUtils;

import org.jboss.qa.jenkins.test.executor.JenkinsTestExecutor;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

	public static void main(String[] args) throws Exception {
		final List<Object> jobInstances = new ArrayList<>();

		for (String className : args) {
			final Class<?> jobClass = Class.forName(className);
			jobInstances.add(jobClass.newInstance());
		}

		log.info("Jobs: {}", StringUtils.join(args, ","));
		try {
			new JenkinsTestExecutor(jobInstances).run();
			System.exit(0);
		} catch (Exception e) {
			log.error(e.getMessage());
			System.exit(1);
		}
	}
}
