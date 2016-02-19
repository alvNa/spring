package com.ani.twitter4elasticsearch;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class TwitterController {
	private final Logger LOGGER = Logger.getLogger(TwitterController.class);
    private Twitter twitter;

    private ConnectionRepository connectionRepository;

    @Inject
    public TwitterController(Twitter twitter, ConnectionRepository connectionRepository) {
        this.twitter = twitter;
        this.connectionRepository = connectionRepository;
    }

    @RequestMapping(method=RequestMethod.GET)
    public String helloTwitter(Model model) {
    	LOGGER.info("Authorized " + twitter.isAuthorized());
    	
        if (!isTwitterLogged()) {
            return "redirect:/connect/twitter";
        }

        retrieveTweets(model);
        retrieveFriends(model);
        return "home";
    }
    
    @RequestMapping(method = RequestMethod.POST)
	public String createTweet(Model model, @RequestBody String text) {
		twitter.timelineOperations().updateStatus(text);
		
		retrieveTweets(model);
		
		return "home";
	}
	
    private void retrieveFriends(Model model) {
//    	model.addAttribute(twitter.userOperations().getUserProfile());
        model.addAttribute("friends", twitter.friendOperations().getFriends());
	}
    
	private void retrieveTweets(Model model) {
		LOGGER.info("retrieveTweets ");
		model.addAttribute("twitterProfile", twitter.userOperations().getUserProfile().getName());
		model.addAttribute("timeline", twitter.timelineOperations().getHomeTimeline());
		model.addAttribute("myTweets", twitter.timelineOperations().getUserTimeline());
		model.addAttribute("favorites", twitter.timelineOperations().getFavorites());
	}
	
	private boolean isTwitterLogged() {
		return connectionRepository.findPrimaryConnection(Twitter.class) != null;
	}
}
