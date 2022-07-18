# BetterReads

**BetterReads** is a replica of GoodReads.

Two microservice have been created to make BetterReads app.

**1. data-loader**
is a spring boot app to load data from local file to cassandra db.

**2. books-app**
is a web app were user can search book by title or author, view book, can mark book reading status and can also rate book.

![This is an image](books-app/src/main/resources/static/images/BetterReadsHomePage.png)


## Tech Stack

|  |  |
| --- | --- |
| **Backend**  | Spring Boot  |
| **Database**  | Cassandra  |
| **Cloud** | DataStax Astra |
| **UI** | Thymeleaf |
| **Security** | Spring Security |
| **OAuth** | Github |


## Cassandra

**Token Creation**

Go to **Manage Organizations**, then, **Token Management** in [Astra dashboard](https://astra.datastax.com/) and create token. This token will be updated in application.yml for parameters username, password and application.token.

## Github
Go to **Settings**, then, **Developer settings**, then, **Github apps**, then, create/edit an app, then, create user token in [Github](github.com). This token will be updated in application.yml file for github clientId and clientSecret parameters.
