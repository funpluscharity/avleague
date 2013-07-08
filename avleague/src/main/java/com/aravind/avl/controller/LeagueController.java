package com.aravind.avl.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.helpers.collection.IteratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.aravind.avl.domain.League;
import com.aravind.avl.domain.LeagueRepository;
import com.aravind.avl.domain.Match;
import com.aravind.avl.domain.MatchRepository;
import com.aravind.avl.domain.Pool;
import com.aravind.avl.domain.PoolRepository;
import com.aravind.avl.domain.Team;
import com.aravind.avl.domain.TeamRepository;
import com.aravind.avl.service.LeagueFactory;
import com.aravind.avl.service.LeaguePopulator;

@Controller
public class LeagueController
{
	private transient final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private LeaguePopulator populator;

	@Autowired
	private TeamRepository teamRepo;

	@Autowired
	private LeagueRepository leagueRepo;

	@Autowired
	private PoolRepository poolRepo;

	@Autowired
	private MatchRepository matchRepo;

	@Autowired
	private Neo4jTemplate template;

	@RequestMapping (value = "/leagues/new", method = RequestMethod.GET)
	public String newLeague(Model model)
	{
		League l = new League();
		model.addAttribute("newLeague", l);
		return "/leagues/new";
	}

	@RequestMapping (value = "/leagues/list", method = RequestMethod.GET)
	public String list(Model model)
	{
		Iterable<League> all = leagueRepo.findAll();
		model.addAttribute("leagues", IteratorUtil.asCollection(all));

		return "/leagues/list";
	}

	@Transactional
	@RequestMapping (value = "/leagues/new", method = RequestMethod.POST)
	public String newLeague(@ModelAttribute League newLeague)
	{
		LOG.debug("Creating new league: {}", newLeague);
		// Stores the given entity in the graph, if the entity is already
		// attached to the graph, the node is updated,
		// otherwise a new node is created.
		leagueRepo.save(newLeague);

		return "redirect:list";
	}

	@RequestMapping (value = "/leagues/{leagueId}/matches/new", method = RequestMethod.GET)
	public String newMatch(@PathVariable Long leagueId, Model model)
	{
		LOG.debug("Adding an empty Match binding to model");
		Match m = new Match();
		model.addAttribute("newMatch", m);

		Map<Long, String> teams = new LinkedHashMap<Long, String>();
		League currentLeague = leagueRepo.findCurrentLeague();
		for (Team t: currentLeague.getTeams())
		{
			teams.put(t.getNodeId(), t.getName());
		}
		model.addAttribute("teamsOfCurrentLeague", teams);

		Map<String, String> levels = new LinkedHashMap<String, String>();
		for (Match.Level l: Match.Level.values())
		{
			levels.put(l.name(), l.toString());
		}
		model.addAttribute("levels", levels);

		return "/leagues/matches/new";
	}

	@Transactional
	@RequestMapping (value = "/leagues/{leagueName}/matches/", method = RequestMethod.GET)
	public String matches(@PathVariable String leagueName, Model model)
	{
		LOG.debug("Finding league by name: [{}]", leagueName);
		League l = leagueRepo.findByName(leagueName);
		LOG.debug("Found league: {}", l);

		LOG.debug("Fetching matches");
		template.fetch(l.getMatches());
		LOG.debug("After fetch: {}", l.getMatches());

		model.addAttribute("league", l);
		return "/leagues/matches/list";
	}

	@Transactional
	@RequestMapping (value = "/leagues/{leagueId}/matches/new", method = RequestMethod.POST)
	public String newMatch(@PathVariable Long leagueId, @RequestParam ("teamA.nodeId") Long teamA,
			@RequestParam ("teamB.nodeId") Long teamB, @RequestParam ("pool") String pool, @RequestParam ("level") String level)
	{
		LOG.debug("Creating new match for League id: {}", leagueId);
		LOG.debug("Creating new match between Team A: {}", teamA);
		LOG.debug("and Team B: {}", teamB);
		LOG.debug("in Pool: {}", pool);
		LOG.debug("for Level : {}", Match.Level.valueOf(level));
		// Stores the given entity in the graph, if the entity is already
		// attached to the graph, the node is updated,
		// otherwise a new node is created.
		League league = leagueRepo.findOne(leagueId);
		Match match = league.conductMatch(teamRepo.findOne(teamA), teamRepo.findOne(teamB));
		match.setLevel(Match.Level.valueOf(level));

		Pool p = poolRepo.findByName(pool);
		if (p == null)
		{
			LOG.debug("Didn't find Pool with name {} so creating one", pool);
			p = new Pool(pool);
			poolRepo.save(p);
		}
		match.setPool(p);

		matchRepo.save(match);

		LOG.debug("Saved Match {}", match);

		return "redirect:list";
	}

	@RequestMapping (value = "/admin/populate", method = RequestMethod.GET)
	public String populateDatabase(Model model)
	{
		LOG.debug("Before populating the graph db.");
		LeagueFactory.ImportProfile profile = new LeagueFactory.ImportProfile();
		profile.teamNameRowNum = 0;
		profile.emailColumnNum = 11;
		profile.phoneColumnNum = 12;

		Collection<League> june2012 = populator.populateDatabase(
				"Sri Bala Bharathi 2012 June League_2012-06-16_2012-06-17.properties", profile);
		LOG.debug("After populating the graph db.");

		LOG.debug("Before populating the graph db.");
		profile = new LeagueFactory.ImportProfile();
		profile.teamNameRowNum = 0;
		profile.emailColumnNum = 9;
		profile.phoneColumnNum = 10;

		Collection<League> september2012 = populator.populateDatabase(
				"Sri Bala Bharathi 2012 September League_2012-09-08_2012-09-15.properties", profile);
		LOG.debug("After populating the graph db.");

		/*
		 * TODO used for testing the league save. can delete after issue #1 has been tested League league =
		 * Iterables.getOnlyElement(june2012); Long nodeId = Iterables.get(league.getTeams(), 0).getNodeId();
		 * LOG.debug("Retrieving team with node id as {}", nodeId); Team team = teamRepo.findOne(nodeId);
		 * LOG.debug("Retrieved team {} with node id as {}", team, nodeId); model.addAttribute("team", team);
		 */

		List<League> leagues = new ArrayList<League>();
		leagues.addAll(june2012);
		leagues.addAll(september2012);

		model.addAttribute("leagues", leagues);

		return "/leagues/list";
	}
}
