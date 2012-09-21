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
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:torben@adorsys.de">Torben Jaeger</a>
 */
public class InheritedDependencyScopeOverridden implements EnforcerRule {
	public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
		Log log = helper.getLog();

		try {
			// get the various expressions out of the helper.
			MavenProject project = (MavenProject) helper.evaluate("${project}");

			StringBuilder overriddenDeps = new StringBuilder();
			StringBuilder pseudoOverriddenDeps = new StringBuilder();

			List<MyDependency> inheritedDependencies = getComparableList(project.getDependencyManagement()
					.getDependencies());
			List<MyDependency> declaredDependencies = getComparableList(project.getOriginalModel().getDependencies());

			for (MyDependency dependency : declaredDependencies) {
				if (null != dependency.getScope()) {
					Dependency effectiveDependency = inheritedDependencies.get(
							inheritedDependencies.indexOf(dependency)).getRawDependency();
					String output = effectiveDependency.getManagementKey() + ": " + effectiveDependency.getScope()
							+ " --> " + dependency.getScope() + "\n";

					if (effectiveDependency.getScope() != null ? effectiveDependency.getScope().equals(
							dependency.getScope()) : dependency.getScope() == null) {
						pseudoOverriddenDeps.append(output);
					} else {
						overriddenDeps.append(output);
					}
				}
			}

			if (overriddenDeps.length() != 0 | pseudoOverriddenDeps.length() != 0) {
				String warningString = "This depenencies override the inherited scope:\n"
						+ "------------------------------------------------------\n";
				if (pseudoOverriddenDeps.length() != 0) {
					throw new EnforcerRuleException(warningString + overriddenDeps
							+ "\nThis depenencies override the inherited scope with the already defined scope value.\n"
							+ "This dependencies should be corrected:\n"
							+ "---------------------------------------------\n" + pseudoOverriddenDeps + "\n");
				}
				log.warn(warningString + overriddenDeps.toString());
			}
		} catch (ExpressionEvaluationException e) {
			throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
		}
	}

	private List<MyDependency> getComparableList(List<Dependency> dependencies) {
		ArrayList<MyDependency> returnList = new ArrayList<MyDependency>(dependencies.size());
		for (Dependency dependency : dependencies) {
			returnList.add(new MyDependency(dependency));
		}
		return returnList;
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

	private class MyDependency extends Dependency {
		private static final long serialVersionUID = 1L;

		Dependency dependency;

		private MyDependency(Dependency dependency) {
			this.dependency = dependency;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof MyDependency))
				return false;

			MyDependency that = (MyDependency) o;

			return dependency.getArtifactId().equals(that.getArtifactId())
					&& dependency.getGroupId().equals(that.getGroupId())
					&& dependency.getType().equals(that.getType())
					&& (dependency.getClassifier() != null ? dependency.getClassifier().equals(that.getClassifier())
							: that.getClassifier() == null);
		}

		@Override
		public int hashCode() {
			return dependency.hashCode();
		}

		@Override
		public String getArtifactId() {
			return dependency.getArtifactId();
		}

		@Override
		public String getGroupId() {
			return dependency.getGroupId();
		}

		@Override
		public String getVersion() {
			return dependency.getVersion();
		}

		@Override
		public String getType() {
			return dependency.getType();
		}

		@Override
		public String getClassifier() {
			return dependency.getClassifier();
		}

		@Override
		public String getScope() {
			return dependency.getScope();
		}

		@Override
		public String toString() {
			return dependency.toString();
		}

		@Override
		public String getManagementKey() {
			return dependency.getManagementKey();
		}

		public Dependency getRawDependency() {
			return dependency;
		}

	}
}
