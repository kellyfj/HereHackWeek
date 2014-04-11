HereHackWeek
============

#### Summary
Mining the Twitter Feed location tweets to help check for Place freshness within Places API

#### Team Members
Frank Kelly, Will Fergus, Yuntao Zhang

#### Team Name
House Baratheon

#### Detailed Description

Approx 1-2% of all Tweets are Geo-Tagged. That's still a lot of data. What can we do with that?
Well for the Hackathon we basically listened to FourSquare related tweets that were geotagged e.g. "I'm at Some Cafe w/ @JoeMyFriend" 

Then we

1. Parsed the Tweet to pull out the Place Name "Some Cafe"
2. Did a Search against Places API with the Name and the Lat/Long from the tweet
3. If we found NO hits at all (yes this is a little strict because sometimes you can get hits that are NOT a match)
We added it to a list
4. We dumped the list into D3 for graphing

Technologies Used: Java Twitter client, D3.js (for cluster visualization) e.g. https://github.com/mbostock/d3/wiki/Gallery

From running for 3.5 hours here is a display of the 250+ candidate new places 

![alt tag](https://raw.github.com/kellyfj/HereHackWeek/master/demo/newplaces.png)

You will note that FourSquare is used heavily in Turkey and Brazil

Futher Improvements:

1. Remove the "w/" in some place names searches e.g. "Karabük Üniversitesi YDYO w/"
2. Recognize and remove private places e.g. "Hocaoğlu's Home w/"
3. Pay Twitter to get more tweets (there's no way we're getting the full stream!)
