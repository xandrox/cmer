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
 * @author <a href="mailto:torben@adorsys.de">Torben Jaeger</a>
 */
public class GroupIdAndVersionInheritance implements EnforcerRule {
	private String declaringProjectArtefactId;

	public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
		try {
			// get the various expressions out of the helper.
			MavenProject project = (MavenProject) helper.evaluate("${project}");

			if (declaringProjectArtefactId.equals(project.getOriginalModel().getParent().getArtifactId())) {
				return;
			}

			if (null != project.getOriginalModel().getGroupId() | null != project.getOriginalModel().getVersion()) {
				throw new EnforcerRuleException("The project contains a groupId or version declaration. "
						+ "Thease are already defined in the parent pom.");
			}

		} catch (ExpressionEvaluationException e) {
			throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
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
