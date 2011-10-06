package jenkins.plugin.assembla;

import hudson.Extension;
import hudson.Util;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.util.CopyOnWriteList;
import hudson.util.FormValidation;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import jenkins.plugin.assembla.api.AssemblaHttpClient;
import jenkins.plugin.assembla.api.AssemblaSite;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class AssemblaProjectProperty extends JobProperty<AbstractProject<?, ?>> {

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	private static final Logger LOGGER = Logger
			.getLogger(AssemblaProjectProperty.class.getName());

	private String spaceName;

	private boolean backtrackEnabled;

	private String siteName;

	private boolean pluginEnabled;

	@DataBoundConstructor
	public AssemblaProjectProperty(String spaceName, boolean backtrackEnabled,
			String siteName, boolean pluginEnabled) {

		LOGGER.info("AssemblaProjectProperty created");
		LOGGER.info("spaceName=" + spaceName);
		LOGGER.info("backtrackEnabled=" + backtrackEnabled);
		LOGGER.info("siteName=" + siteName);
		LOGGER.info("pluginEnabled=" + pluginEnabled);

		this.siteName = siteName;
		this.spaceName = spaceName;
		this.backtrackEnabled = backtrackEnabled;
		this.pluginEnabled = pluginEnabled;
	}

	public boolean isPluginEnabled() {
		return pluginEnabled;
	}

	public String getSpaceName() {
		return spaceName;
	}

	public boolean isBacktrackEnabled() {
		return backtrackEnabled;
	}

	public String getSiteName() {

		return siteName;
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return DESCRIPTOR;
	}

	public static final class DescriptorImpl extends JobPropertyDescriptor {

		private final CopyOnWriteList<AssemblaSite> sites = new CopyOnWriteList<AssemblaSite>();

		public DescriptorImpl() {
			super(AssemblaProjectProperty.class);
			load();
		}

		public void setSites(AssemblaSite site) {
			sites.add(site);
		}

		public AssemblaSite[] getSites() {

			AssemblaSite[] result = sites.toArray(new AssemblaSite[0]);

			LOGGER.info("Called getSites size=" + result.length);

			return result;
		}

		@Override
		public String getDisplayName() {

			return "Assembla Plugin";
		}

		@Override
		public JobProperty<?> newInstance(StaplerRequest req,
				JSONObject formData)
				throws hudson.model.Descriptor.FormException {

			LOGGER.info("DescriptorImpl newInstance called");

			AssemblaProjectProperty jpp = req.bindParameters(
					AssemblaProjectProperty.class, "assembla.");
			if (jpp.siteName == null)
				jpp = null; // not configured
			return jpp;
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) {

			LOGGER.info("DescriptorImpl configure called");

			sites.replaceBy(req.bindParametersToList(AssemblaSite.class,
					"assembla."));
			save();

			LOGGER.info("sites.size()=" + sites.size());

			return true;
		}

		public FormValidation doUrlCheck(@QueryParameter final String value)
				throws IOException, ServletException {

			return new FormValidation.URLCheck() {
				@Override
				protected FormValidation check() throws IOException,
						ServletException {
					String url = Util.fixEmpty(value);
					if (url == null) {
						return FormValidation.error(Messages
								.AssemblaProjectProperty_AssemblaUrlMandatory());
					} else {

						return FormValidation.ok();
					}

				}
			}.check();
		}

		public FormValidation doTestConnection(
				@QueryParameter("assembla.url") final String url,
				@QueryParameter("assembla.username") final String username,
				@QueryParameter("assembla.password") final String password)
				throws IOException, ServletException {
			try {

				AssemblaSite site = new AssemblaSite(username, password, url,
						null);
				AssemblaHttpClient client = new AssemblaHttpClient(site);

				int serverError = client.executeHttpGet("my_spaces", null);
				if (serverError == 200) {

					return FormValidation
							.okWithMarkup("<img src='"
									+ Hudson.getInstance().getRootUrl()
									+ "/plugin/assembla-jenkins/button-check.png'/><font color='#008000'><b>Success</b></font>");
				} else {

					return FormValidation.error("Cannot connect to '" + url
							+ "': server error[" + serverError + "]");
				}

			} catch (Exception e) {

				return FormValidation.error("Client error : " + e.getMessage());
			}
		}
	}

	public AssemblaSite getSite() {
		AssemblaSite result = null;
		AssemblaSite[] sites = DESCRIPTOR.getSites();
		if (siteName == null && sites.length > 0) {
			// default
			result = sites[0];
		}

		for (AssemblaSite site : sites) {
			if (site.getName().equals(siteName)) {
				result = site;
				break;
			}
		}

		if (result != null) {
			result.setSpace(spaceName);
			result.setBacktrackEnabled(backtrackEnabled);
			result.setPluginEnabled(pluginEnabled);
		}

		return result;
	}
}
