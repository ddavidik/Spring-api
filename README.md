# Java developer task

Java developer task by Daniel Davidík

## How to start the service

- Make sure your docker is running
- In cmd, run `docker-compose up` at the root of the project
- Go to [localhost](http://localhost:8081/endpoints) at port 8081

## Additional info and thoughts

In addition to Spring MVC I have developed a RESTful API with spring HATEOAS, which I would prefer to see as something which would be consumed by another frontend. This side of API is mapped by [rest/endpoints](http:localhost:8081/rest/endpoints). I had no prior experience with Spring MVC, the view part is quite bare-bones, but I focused primarily on backend.

The database is preloaded with two users - Superman and Batman. They also have these accessTokens:  `93f39e2f-80de-4033-99ee-249d92736a25` and `dcb20f8a-5657-4f1b-9f7f-ce65739b359e`. Authentication is made using a header named `X-Authorization`. To supply this header I used Postman or a browser extension like [ModHeader](https://docs.modheader.com/using-modheader/introduction).

I also had no prior real experience with Docker, but I decided to make it work for that sweet bonus points! It's also much easier to run. One thing didn't go according to plan - I am not using a .jar file and docker-compose.yml builds the app, but in that step I had to skip tests, because the database is not yet ready during compose and so the tests would fail. I could probably achieve this by using external bash scripts that would check if the database is ready to connect to, but that seemed to be a bit over the top. The tests work, though. I didn't set up any volumes on purpose, I don't want to persist data and `spring.jpa.hibernate.dll-auto` is set to create-drop for easier testing.

The urlService map implementation is wrong on so many levels, but I didn't come up with a better solution at the time. The controllers should be lighter as well, the logic could be extracted to services.
