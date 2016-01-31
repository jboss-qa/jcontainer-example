package org.jboss.qa.jcontainer.example.jobs;

import org.jboss.qa.jcontainer.Container;
import org.jboss.qa.jcontainer.wildfly.WildflyConfiguration;
import org.jboss.qa.jcontainer.wildfly.WildflyContainer;
import org.jboss.qa.jcontainer.wildfly.WildflyUser;
import org.jboss.qa.jenkins.test.executor.beans.Destination;
import org.jboss.qa.jenkins.test.executor.phase.download.Download;
import org.jboss.qa.jenkins.test.executor.phase.download.Downloads;
import org.jboss.qa.jenkins.test.executor.phase.download.Dst;
import org.jboss.qa.jenkins.test.executor.phase.download.UnPack;
import org.jboss.qa.jenkins.test.executor.phase.start.Start;
import org.jboss.qa.jenkins.test.executor.phase.staticconfiguration.StaticConfiguration;
import org.jboss.qa.jenkins.test.executor.phase.stop.Stop;
import org.jboss.qa.phaser.Inject;
import org.jboss.qa.phaser.registry.InstanceRegistry;
import org.jboss.qa.phaser.RunAlways;

@Downloads({
		@Download(id = "wildfly",
				url = "${wildfly.zip.url}",
				destination = @Dst(id = "downloads", destination = "downloads"),
				unpack = @UnPack(unpack = true, destination = @Dst(id = "wildfly-home", destination = "wildfly"))
		)
})
public class BaseJob {

	@Inject(id = "wildfly-home")
	protected Destination wildflyHome;

	protected Container container;

	@Inject
	private InstanceRegistry registry;

	@StaticConfiguration(order = 1)
	public void setupContainer() throws Exception {
		final WildflyConfiguration conf = WildflyConfiguration.builder()
				.directory(wildflyHome.getDestination().getAbsolutePath())
				.profile("standalone-full.xml")
				.xmx("2g")
				.build();
		container = new WildflyContainer<>(conf);
		registry.insert(container);
	}

	@StaticConfiguration(order = 2)
	public void addMgmtUser() throws Exception {
		final WildflyUser mgmtUser = new WildflyUser();
		mgmtUser.setUsername("admin");
		mgmtUser.setPassword("admin");
		mgmtUser.setRealm(WildflyUser.Realm.MANAGEMENT_REALM);
		container.addUser(mgmtUser);
	}

	@Start
	public void startContainer() throws Exception {
		container.start();
	}

	@Stop
	@RunAlways
	public void stopContainer() throws Exception {
		if (container != null) {
			container.stop();
		}
	}
}
