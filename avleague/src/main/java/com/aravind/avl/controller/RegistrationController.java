package com.aravind.avl.controller;

import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.aravind.avl.domain.League;
import com.aravind.avl.domain.LeagueRepository;
import com.aravind.avl.domain.Player;
import com.aravind.avl.domain.Team;
import com.aravind.avl.domain.TeamRepository;

@Controller
@RequestMapping ("/registration")
public class RegistrationController
{
	private transient final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private LeagueRepository leagueRepo;

	@Autowired
	private TeamRepository teamRepo;

	@RequestMapping (method = RequestMethod.GET)
	public String get()
	{
		return "/registration/start";
	}

	@Transactional
	@RequestMapping (value = "/start", method = RequestMethod.POST)
	public String hasParticipatedEarlier(@RequestParam boolean participatedInEarlierLeague, Model model)
	{
		if (participatedInEarlierLeague)
		{
			LOG.debug("Participated in earlier leagues. Retrieving the past league teams.");
			Iterable<League> allLeagues = leagueRepo.findAll();
			Set<League> sortedLeagues = new TreeSet<League>();
			for (League l: allLeagues)
			{
				sortedLeagues.add(l);
			}
			LOG.debug("Past leagues sorted by start date {}", sortedLeagues);
			model.addAttribute("pastLeagues", sortedLeagues);
		}
		else
		{
			LOG.debug("Did not participate in earlier leagues. Redirecting to register the new one.");
		}
		return "/registration/leagues";
	}

	@RequestMapping (value = "/selectTeam", method = RequestMethod.POST)
	public String selectTeam(@RequestParam Long selectedTeam, Model model)
	{
		LOG.debug("Participated as team {} in previous league", selectedTeam);
		Team team = teamRepo.findOne(selectedTeam);
		// TeamView team=new TeamView();
		// team.set
		model.addAttribute("team", team);
		model.addAttribute("players", team.getPlayers());
		// model.addAttribute("selectedPlayers", new ArrayList<Long>());
		return "registration/players";
	}

	@RequestMapping (value = "/newTeam", method = RequestMethod.POST)
	public String newdTeam(@ModelAttribute Team team, Model model)
	{
		LOG.debug("Selected players from existing list {}", team.getPlayers());

		return "registration/registrationConfirmation";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder)
	{
		System.err.println("=====================================================");
		/* binder.setAllowedFields(new String[]{ "players"}); */
		binder.registerCustomEditor(Player.class, new PlayerPropertyEditor());
		binder.registerCustomEditor(Team.class, new TeamPropertyEditor());
	}
}
