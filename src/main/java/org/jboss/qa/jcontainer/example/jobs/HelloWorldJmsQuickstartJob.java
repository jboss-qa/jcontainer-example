package org.jboss.qa.jcontainer.example.jobs;

import static org.jboss.qa.phaser.ExceptionHandling.Execution.CONTINUE;
import static org.jboss.qa.phaser.ExceptionHandling.Report.THROW_AT_END;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.FileUtils;

import org.jboss.qa.jcontainer.Container;
import org.jboss.qa.jcontainer.wildfly.WildflyUser;
import org.jboss.qa.jenkins.test.executor.beans.Workspace;
import org.jboss.qa.jenkins.test.executor.phase.execution.Execution;
import org.jboss.qa.jenkins.test.executor.phase.runtimesetup.RuntimeSetup;
import org.jboss.qa.jenkins.test.executor.phase.runtimeteardown.RuntimeTeardown;
import org.jboss.qa.jenkins.test.executor.phase.staticconfiguration.StaticConfiguration;
import org.jboss.qa.jenkins.test.executor.utils.MavenCli;
import org.jboss.qa.phaser.Create;
import org.jboss.qa.phaser.Inject;
import org.jboss.qa.phaser.OnException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @see <a href="https://goo.gl/RmFBXP">helloworld-jms quickstart</a>
 */
@Slf4j
public class HelloWorldJmsQuickstartJob {

	private final String QUICKSTART_NAME = "helloworld-jms";

	@Inject
	protected Workspace workspace;

	@Inject
	private Container container;

	private File quickstartDir;

	@StaticConfiguration
	public void setup() throws Exception {
		quickstartDir = new File(workspace.getDestination(), "quickstart" + File.separator + QUICKSTART_NAME);
	}

	@StaticConfiguration(order = 3)
	public void addQuickstartUser() throws Exception {
		final WildflyUser user = new WildflyUser();
		user.setRealm(WildflyUser.Realm.APPLICATION_REALM);
		user.setUsername("quickstartUser");
		user.setPassword("quickstartPwd1!");
		user.addRoles("guest");
		container.addUser(user);
	}

	@RuntimeSetup
	public void jmsSetup() throws Exception {
		container.getClient().execute(new File(quickstartDir, "configure-jms.cli"));
	}

	@Execution
	@OnException(execution = CONTINUE, report = THROW_AT_END)
	public void executeTests(@Create MavenCli.Builder builder) throws Exception {
		final File pomFile = new File(workspace.getDestination(), "quickstart/pom.xml");
		assertTrue(String.format("File %s does not exist", pomFile.getAbsoluteFile()), pomFile.exists());

		builder.pom(pomFile)
				.goals("clean", "compile", "exec:java")
				.projects(QUICKSTART_NAME)
				.alsoMake(true);

		assertEquals("Test failed", 0, builder.build().run());
	}

	@RuntimeTeardown
	public void jmsTeardown() throws Exception {
		container.getClient().execute(new File(quickstartDir, "remove-jms.cli"));
	}
}
