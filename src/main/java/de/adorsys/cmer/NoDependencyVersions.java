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
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

/**
 * @author Sandro Sonntag
 */
public class NoDependencyVersions implements EnforcerRule {
	
	private String ignoreMasterProjectGroupId;
	private String allowedGroupPrefix;

	public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
		if (ignoreMasterProjectGroupId == null) {
			throw new EnforcerRuleException("ignoreMasterProjectGroupId parameter is not defined");
		}

		if (allowedGroupPrefix == null) {
			throw new EnforcerRuleException("allowedGroupPrefix parameter is not defined");
		}

		try {

			// get the various expressions out of the helper.
			MavenProject project = (MavenProject) helper.evaluate("${project}");

			checkOnNoDependencieVersion(project);
			checkOnNoDependencieVersionsFrom3rdParty(project);

		} catch (ExpressionEvaluationException e) {
			throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
		}
	}

	private void checkOnNoDependencieVersion(MavenProject project) throws EnforcerRuleException {
		Model originalModel = project.getOriginalModel();
		StringBuilder dependenciesWithVersionDeclaration = new StringBuilder();
		boolean fail = false;
		for (Dependency d : originalModel.getDependencies()) {
			if (d.getVersion() != null) {
				dependenciesWithVersionDeclaration.append(" - " + d.toString() + "\n");
				fail = true;
			}
		}
		if (fail) {
			throw new EnforcerRuleException("This project contains explicit dependeny versions.\n"
					+ "Please declare for maintainance reasons 3rd pary versions in " + ignoreMasterProjectGroupId
					+ " or\n" + "in case of bei '" + allowedGroupPrefix
					+ "*' Libs in the root pom in the 'dependencyManagement' section.\n"
					+ "This dependencies sould be corrected:\n" + dependenciesWithVersionDeclaration

			);
		}
	}

	private void checkOnNoDependencieVersionsFrom3rdParty(MavenProject project) throws EnforcerRuleException {
		Model originalModel = project.getOriginalModel();
		StringBuilder dependenciesWithVersionDeclaration = new StringBuilder();
		if (project.getGroupId().equals(ignoreMasterProjectGroupId) || originalModel.getDependencyManagement() == null
				|| originalModel.getDependencyManagement().getDependencies() == null) {
			return;
		}
		boolean fail = false;
		for (Dependency d : originalModel.getDependencyManagement().getDependencies()) {
			if (!d.getGroupId().startsWith(allowedGroupPrefix)) {
				dependenciesWithVersionDeclaration.append(" - " + d.toString() + "\n");
				fail = true;
			}
		}
		if (fail) {
			throw new EnforcerRuleException("This Project contains Dependency-Versions from 3rd party libs (not "
					+ allowedGroupPrefix + ").\n" + "Please declare for maintainance reasons 3rd pary versions in "
					+ ignoreMasterProjectGroupId + ".\n" + "This dependencies sould be corrected:\n"
					+ dependenciesWithVersionDeclaration

			);
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
