# Kickway

[![forthebadge](https://forthebadge.com/images/badges/approved-by-george-costanza.svg)](https://forthebadge.com)

The kickway is a gateway which serves the kickchain in a RESTful way. :sleeping:

# Architecture

The foosball table is connected to a raspberry which communicates with two photocells to react in case of a new goal. The mobile devices are used to create a new game, join an existing one, display the top ten players or the statistics of a specific player. The pairing between the players mobile devices and the raspberry is implemented using the [Google Nearby API](https://developers.google.com/nearby/). 

# Use-Cases
  - Authorize the mobile clients
  - Store a game 
  - Fetch the statistics

# Prerequisites

Kickchain specific application.properties:

``` 
kickway.kickchain.url=https://localhost:7472
kickway.kickchain.name=kickchain
kickway.kickchain.password=niahckcik
```

# Links

:link: [Kickchain](https://github.com/smartsquare/kickchain)
:iphone: [Android App](https://github.com/SmartsquareGmbH/kickchain-android-client)
:pager: [Raspberry PI](https://github.com/SmartsquareGmbH/kickpi)


**Free Software, Hell Yeah!**
