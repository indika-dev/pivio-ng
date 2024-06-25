# A view for the db

This is a first shot on how to view things in the DB.

This is totally minimal and using Spring/Java here is just to have some framework around to do some templating.
Maybe extend it later to have some plugins. For now just passing links around.

You can include 'external' websites and if you need to access the config of this service ask http://<pivio-view>:<port>/config .

It could very well be just a bunch of html and javascript. All the queries are done in Javascript. Templating inside JS is done with handlebars. Sounds a bit crazy but of you want to contribute ... you are very welcome ;-).

```
gradle build
```

# Config File


```
api: http://localhost:9123/ // Points to your Pivio DB instance
pages:
  - description: Overview
    url: /app/overview
    id: tabOverview
  - description: Query
    url: app/query
    id: tabQuery
  - description: Service Graph
    url: /app/servicegraph
    id: tabServiceGraph
  - description: Feed
    url: /app/feed
    id: tabFeed
```

# Run

```
java -jar build/libs/view-0.0.1-SNAPSHOT.jar -f service_config.yaml
```

# Build Status

[![Build Status](https://travis-ci.org/pivio/pivio-web.svg?branch=master)](https://travis-ci.org/pivio/pivio-web)

