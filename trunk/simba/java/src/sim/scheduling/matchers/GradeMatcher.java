package sim.scheduling.matchers;

import java.util.List;

import sim.model.Host;
import sim.model.Job;
import sim.scheduling.graders.Grader;
import utils.GlobalUtils;

public class GradeMatcher implements Matcher
{
	private final List<Grader> graders;

	public GradeMatcher(List<Grader> graders)
	{
		super();
		this.graders = graders;
	}

	public Host match(Job job, List<Host> hosts)
	{
		Host $ = null;
		for (Host host : hosts)
		{
			if (host.hasAvailableResourcesFor(job))
			{
				if (null == $)
				{
					$ = host;
				}
				else
				{
					$ = compareHostsByGraders($, host, job);
				}
			}
		}
		return $;
	}

	private Host compareHostsByGraders(Host host1, Host host2, Job job)
	{
		for (Grader grader : graders)
		{
			if (GlobalUtils.equals(grader.getGrade(host1, job), grader.getGrade(host2, job)))
			{
				continue;
			}
			return grader.getGrade(host1, job) > grader.getGrade(host2, job) ? host1 : host2;
		}
		return host1;
	}

	@Override
	public String toString()
	{
		return "GradeMatcher [graders=" + graders + "]";
	}

}