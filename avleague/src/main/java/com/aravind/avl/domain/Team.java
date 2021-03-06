package com.aravind.avl.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.support.index.IndexType;

@NodeEntity
public class Team implements Awardee
{
	@GraphId
	private Long nodeId;

	@GraphProperty
	@Indexed (indexType = IndexType.FULLTEXT, indexName = "TeamName", unique = true)
	private String name;

	@Fetch
	@RelatedTo (type = "PLAYED_WITH_TEAM", direction = Direction.INCOMING)
	private final Set<Player> players = new HashSet<Player>();

	@RelatedTo (type = "IN_POOL")
	private Pool pool;

	@RelatedTo (type = "PREVIOUSLY_KNOWN_AS")
	private Team previouslyKnownAs;

	private List<String> aliases;

	public Team()
	{}

	public List<String> getAliases()
	{
		return aliases;
	}

	public void setAliases(List<String> a)
	{
		if (a != null)
		{
			this.aliases = a;
		}
		else
		{
			aliases = Collections.emptyList();
		}
	}

	public Team getPreviouslyKnownAs()
	{
		return previouslyKnownAs;
	}

	public void setPreviouslyKnownAs(Team alias)
	{
		this.previouslyKnownAs = alias;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = StringUtil.capitalizeFirstLetter(name);
	}

	public Long getNodeId()
	{
		return nodeId;
	}

	public Collection<Player> getPlayers()
	{
		return players;
	}

	public void setPlayers(Set<Player> plyrs)
	{
		players.addAll(plyrs);
	}

	public void addPlayer(Player p)
	{
		players.add(p);
	}

	@Override
	public String toString()
	{
		return "Team [nodeId=" + nodeId + ", name=" + name + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		Team other = (Team) obj;
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		if (nodeId == null)
		{
			if (other.nodeId != null)
			{
				return false;
			}
		}
		else if (!nodeId.equals(other.nodeId))
		{
			return false;
		}
		return true;
	}

	public Pool getPool()
	{
		return pool;
	}

	public void setPool(Pool pool)
	{
		this.pool = pool;
	}

}
