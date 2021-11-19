**New features are going to be implemented in the future

# myBoard
Live version (might take few seconds to load): https://my-post-board.herokuapp.com/

myBoard is an application that gives user the opportunity to
create posts and comment post of other users. You need to register to be able to use the site or
you can use universial credentials:
- __username:__ user
- __password:__ user


<font size="5">**Tech Stack:**</font>
- Spring Boot
- Spring Security
- Spring AOP
- Hibernate
- MySQL
- Flyway
- Thymeleaf
- HTML + CSS
- Bootstrap

## How to run the project:

#### Database setup
You will need MySQL database instance. If you already have that we
can move on to editing __application.properties__. I suggest to create your own local profile
called for example __application-local.properties__ (or .yml) where you can put your
database credentials. In order to do that, edit these properties:
```.properties
    spring.datasource.url=
    spring.datasource.username=
    spring.datasource.password=
  ```
And then remember to set your profile as active:
```.properties
    spring.profiles.active=
  ```

Next thing to do is creating schemas. To do that you can use
my scripts located in __/src/main/resources/__ and called
__DatabaseInitScripts.sql__ and __SecuritySqlScripts.sql__ or just enable
flyway by changing the property to:

```.properties
    spring.flyway.enabled=true
  ```

Now you should be able to run the app on your localhost.


