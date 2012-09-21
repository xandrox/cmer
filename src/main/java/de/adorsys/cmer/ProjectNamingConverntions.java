/**
 * Copyright (C) 2012 Sandro Sonntag sso@adorsys.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.adorsys.cmer;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

/**
 * @author Sandro Sonntag
 */
public class ProjectNamingConverntions implements EnforcerRule {

	private String allowedGroupPrefix;

	public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
		if (allowedGroupPrefix == null) {
			throw new EnforcerRuleException("allowedGroupPrefix parameter is not defined");
		}
		
		try {

			// get the various expressions out of the helper.
			MavenProject project = (MavenProject) helper.evaluate("${project}");

			checkGroupIdPrefix(project);
			checkGroupIdAndArifactOverlapping(project, helper);

		} catch (ExpressionEvaluationException e) {
			throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
		}
	}

	private void checkGroupIdAndArifactOverlapping(MavenProject project, EnforcerRuleHelper helper)
			throws EnforcerRuleException {
		String[] groupParts = project.getGroupId().split("[.]");
		String[] artifactParts = project.getArtifactId().split("[.]");
		String lastPartGroup = groupParts[groupParts.length - 1];
		String firstPartArtifactId = artifactParts[0];
		if (!lastPartGroup.equals(firstPartArtifactId)) {
			throw new EnforcerRuleException(
					"The last fragment of the groupId does not macht the first part of the artifactId of artifact "
							+ project.getArtifactId());
		}
	}

	private void checkGroupIdPrefix(MavenProject project) throws EnforcerRuleException {
		if (!project.getGroupId().startsWith(allowedGroupPrefix)) {
			throw new EnforcerRuleException("The groupId of project " + project.getArtifact() + " have to start with :"
					+ allowedGroupPrefix);
		}
	}

	public String getCacheId() {
		// no hash on boolean...only parameter so no hash is needed.
		return "";
	}

	public boolean isCacheable() {
		return false;
	}

	public boolean isResultValid(EnforcerRule arg0) {
		return false;
	}
}
