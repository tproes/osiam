# OSIAM Server

This is the combined authorization and resource server of the OSIAM project.

Right now only PostgreSQL is supported as a database.

# Build and Deployment

To build the osiam-server run
```sh
mvn clean install
```

in the osiam-server folder.

If you want to run the osiam-server in a embedded jetty instance run
```sh
 mvn jetty:run
```

To deploy the osiam-server into a running Tomcat copy the "osiam-server.war" into the webapp folder in your Tomcat installation.


## Authorization Server

The osiam-server is based on:

* Srping-Core 3.2
* Spring Security OAuth2 1.0.0.RC3 
* Hibernate

and provides

* [OAuth2 Authorization Code Flow](http://tools.ietf.org/html/rfc6749#section-4.1)
* [SCIM 2.0 API](http://tools.ietf.org/html/draft-ietf-scim-api-01)


### Configuration

To create the database scheme you have to execute src/main/ressources/sql/init.sql. 

This SQL-Script will create all necessary tables as well as a demo user called Marissa with password 'koala'.

It also creates the first client entry with redirect URI set to localhost.

The client configuration is done via the database and the client management component.

The following values can be submited:
* token_scope
* redirect_uri
* accesstoken_validity_in_seconds
* refreshtoken_validity_in_seconds

The client_id and the client_secret are generated values.

URI to create a client:


To create a new client you need to get an access_token, with an existing
client, and call:


http://localhost:8080/osiam-server/Client

with a valid client e.q.:

```
{"accessTokenValiditySeconds": "1337", "refreshTokenValiditySeconds": "1337",
"redirect_uri": "http://localhost:5000/stuff", "scope": ["POST", "PUT", "GET", "DELETE", "PATCH"]}
```
and an access_token in the header.

```sh
curl -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" -X POST localhost:8080/osiam-server/Client -d '{YOUR_CLIENT_AS_JSON}'
```

URI for getting and deleting a client:

DELETE http://localhost:8080/osiam-server/Client/{client_id}

The database configuration is done via properties file named

 db-config.properties

which looks like

```
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://$your_url:$port/$your_database
db.username=$your_username
db.password=$your_password
```

this config file must lie within the classpath.

For tomcat you have to edit the

shared.loader

property under

$your_tomcat_dir/conf/catalina.properties

to point to a correct folder.


### Usage

To get an authorization code call:

<http://localhost:8080/osiam-server/oauth/authorize?client_id=tonr&response_type=code&redirect_uri=http://localhost:8080/oauth2-client/accessToken>

To get an access_token call:

```sh
curl --user tonr:secret -X POST -d "code=$CODE&grant_type=authorization_code&redirect_uri=http://localhost:8080/oauth2-client/accessToken" \
 http://localhost:8080/osiam-server/oauth/token
```

The client authentication is done via [Basic Access Authentication](http://tools.ietf.org/html/rfc2617).


## Resource Server

All scim calls are secured by oauth2, so you'll have to send an access_token in order to get access, e.g.:

```sh
curl -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" http://localhost:8080/osiam-server/User/{id}
```

### Service provider configuration

The following URI provides the actual supported service provider configuration

URI:
http://localhost:8080/osiam-server/ServiceProviderConfig

### Search
Searches are possible for users and groups.

User URI:

http://localhost:8080/osiam-server/User

Group URI:

http://localhost:8080/osiam-server/Group

#### Filtering including logical operators

The following filter options are supported at the moment:

eq = equals

co = contains

sw = starts with

pr = present

gt = greater than

ge = greater equals

lt = less than

le = less equals

```http
http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"

http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&filter=userName%20co%20"someValue"

http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&filter=userName%20sw%20"someValue"

http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&filter=displayName%20pr

http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20gt%20"2013-05-23T13:12:45.672#;4302:00"

http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20ge%20<an existing time>

http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20lt%20"2013-05-23T13:12:45.672#;4302:00"

http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20le%20<an existing time>
```

Additionally "AND" and "OR" as logical operators are supported, including grouping with parentheses.

```http
http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"%20and%20meta.created%20lt%20"2013-05-23T13:12:45.672#;4302:00"

http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"%20or%20userName%20eq%20"TheUserName1"

http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&filter=(userName%20eq%20"TheUserName"%20or%20userName%20eq%20"TheUserName1")%20and%20meta.created%20gt%20"2013-05-23T13:12:45.672#;4302:00"
```

#### Limiting the output to predefined attributes

It is possible to search and limit the output to a the given list of attributes. To define more than one separate them with comma.

The parameter is:

attributes = defining the attributes to be included in the response

```http
http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&attributes=userName,displayName,meta.created
```

#### Sorting

It is also possible to sort the results ascending and descending by the given attribute.

The parameters are:

sortOrder = ascending and descending. Default is ascending

sortBy = the attribute so sort by. For example "userName". The default is "id"

```http
http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&sortBy=meta.created&sortOrder=ascending

http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&sortBy=meta.created&sortOrder=descending
```

#### Paging

The paging is done via two parameters which will limit the output shown per page and the starting point.

The parameters are:

count = will limit the items per page to the given value. Default is 100

startIndex = will define the start index of the search. Default is 0

To paginate through the results increase the startIndex to the next desired position.

```http
http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&count=5&startIndex=0

http://localhost:8080/osiam-server/User?access_token=YOUR_ACCESSTOKEN&count=5&startIndex=5
```

### User

#### Get a single User

If you want to get a single user, you need to extend User with the external ID of the user:

```http
 http://localhost:8080/osiam-server/User/{id}
```

#### Create a new User

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" -X POST localhost:8080/osiam-server/User -d '{"schemas":["urn:scim:schemas:core:1.0"],"externalId":"marissa","userName":"Arthur Dent","password":""}'
```

#### Replace an old User
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" -X PUT localhost:8080/osiam-server/User/{id} -d '{"schemas":["urn:scim:schemas:core:1.0"], "externalId":"marissa","userName":"Arthur Dent","password":""}'
```

#### Update an User
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" -X PATCH localhost:8080/osiam-server/User/{id} -d '{"schemas":["urn:scim:schemas:core:1.0"], "externalId":"marissa","userName":"Arthur Dent","password":""}'
```

#### Delete an User
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" -X DELETE localhost:8080/osiam-server/User/{id}
```

### Group

#### Get a single Group

If you want to get a single group, you need to extend Group with the external ID of the group:

```http
 http://localhost:8080/osiam-server/Group/{id}
```

#### Create a new Group

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" -X POST localhost:8080/osiam-server/Group -d '{"schemas":["urn:scim:schemas:core:1.0"],"displayName":"marissasGroup"}'
```

#### Replace an old Group
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" -X PUT localhost:8080/osiam-server/Group/{id} -d '{"schemas":["urn:scim:schemas:core:1.0"], "displayName":"Group1"}'
```

#### Update an Group
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" -X PATCH localhost:8080/osiam-server/Group/{id} -d '{"schemas":["urn:scim:schemas:core:1.0"], "displayName":"marissasGroup"}'
```

#### Delete an Group
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" -X DELETE localhost:8080/osiam-server/Group/{id}
