package com.aravind.avl.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.EndResult;
import org.springframework.data.neo4j.conversion.Handler;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith (SpringJUnit4ClassRunner.class)
@DirtiesContext (classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration ({ "/testContext.xml"})
@Transactional
public class PlayerRepositoryTest
{
	@Autowired
	PlayerRepository repo;

	@Autowired
	LeagueRepository leagueRepo;

	@Autowired
	TeamRepository teamRepo;

	@Autowired
	Neo4jTemplate template;

	@Autowired
	GraphDatabaseService gds;

	Player p;

	@Before
	public void setUp()
	{
		p = new Player();
		p.setName("Aravind Yarram");
	}

	@Test
	public void firstNameFullTextSearch()
	{
		Player save = repo.save(p);
		assertNotNull(save);
		assertNotNull(p.getNodeId());

		Player result = repo.findByName("A*");
		assertNotNull("Starts with first letter match", result);

		result = repo.findByName("aravind");
		assertNotNull("Lower-case mathch", result);

		// Can also use generic methods
		EndResult<Player> players = repo.findAllByQuery("firstName", "A*");
		assertNotNull("Starts with first letter match", players.single());

		players = repo.findAllByQuery("FirstName", "firstName", "aravind");
		assertNotNull("Lower-case mathch", players.single());
	}

	@Test
	public void firstNameExactMatchOnly()
	{
		Player save = repo.save(p);
		assertNotNull(save);
		assertNotNull(p.getNodeId());

		// either use domain specific method
		Player result = repo.findByPropertyValue("firstName", p.getFirstName());
		assertNotNull("findByPropertyValue should be used for EXACT matches", result);

		// or generic method
		result = repo.findPlayerByFirstName(p.getFirstName());
		assertNotNull("Exact mathch", result);
	}

	@Test
	public void lastNameFullTextSearch()
	{
		Player save = repo.save(p);
		assertNotNull(save);
		assertNotNull(p.getNodeId());

		// Can also use generic methods
		EndResult<Player> players = repo.findAllByQuery("lastName", "Y*");
		assertNotNull("Starts with first letter match", players.single());

		players = repo.findAllByQuery("lastName", "yarram");
		assertNotNull("Lower-case mathch", players.single());
	}

	@Test
	public void lastNameExactMatchOnly()
	{
		Player save = repo.save(p);
		assertNotNull(save);
		assertNotNull(p.getNodeId());

		// either use domain specific method
		Player result = repo.findPlayerByLastName(p.getLastName());
		assertNotNull("Exact mathch", result);

		// or generic method
		result = repo.findByPropertyValue("lastName", p.getLastName());
		assertNotNull("findByPropertyValue should be used for EXACT matches", result);
	}

	@Test
	public void NAMEFULLTEXTSEARCH()
	{
		Player save = repo.save(p);
		assertNotNull(save);
		assertNotNull(p.getNodeId());

		Player p1 = new Player();
		p1.setName("Aravind Y");
		save = repo.save(p1);

		Player byName = repo.findByName("aravind y");
		assertNotNull(byName);

		EndResult<Player> players = repo.findAllByQuery("name", "Aravind Y".substring(0, 4));
		players.handle(new Handler<Player>()
		{
			@Override
			public void handle(Player value)
			{
				assertNotNull(value.getNodeId());
			}
		});

		Iterable<Player> findByName = repo.findByNameLike("Aravind*");
		assertNotNull(findByName);
		assertEquals(2, IteratorUtil.count(findByName));
	}

	@Test
	public void nameExactMatch()
	{
		Player save = repo.save(p);
		assertNotNull(save);
		assertNotNull(p.getNodeId());

		Player p1 = new Player();
		p1.setName("Aravind Y");
		save = repo.save(p1);

		Player result = repo.findByName("Aravind Yarram");
		assertNotNull("findByPropertyValue should be used for EXACT matches", result);

		result = repo.findByName("Aravind Y");
		assertNotNull("findByPropertyValue should be used for EXACT matches", result);
	}

	@Test
	public void playedForInLeague() throws ParseException
	{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		League june = new League();
		june.setName("June League");
		leagueRepo.save(june);

		Team teamA = new Team();
		teamA.setName("Team A");
		teamRepo.save(teamA);

		p = new Player();
		p.setName("Aravind Yarram");
		repo.save(p);

		PlayerTeamLeague playedWithJune = p.playedForInLeague(teamA, df.parse("01/06/2013"), june);
		assertNotNull(playedWithJune);
		teamA.addPlayer(p);

		League sept = new League();
		sept.setName("September League");
		leagueRepo.save(sept);

		PlayerTeamLeague playedWithSept = p.playedForInLeague(teamA, df.parse("01/09/2013"), sept);
		assertNotNull(playedWithSept);
		repo.save(p);

		Player result = repo.findOne(p.getNodeId());
		assertNotNull(result);
		template.fetch(result.getPlayedforInLeague());
		assertEquals(2, result.getPlayedforInLeague().size());
	}
}
