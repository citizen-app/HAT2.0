[![Build Status](https://travis-ci.org/Hub-of-all-Things/HAT2.0.svg?branch=master)](https://travis-ci.org/Hub-of-all-Things/HAT2.0)
<!--[![Coverage Status](https://coveralls.io/repos/Hub-of-all-Things/HAT2.0/badge.svg?branch=master&service=github)](https://coveralls.io/github/Hub-of-all-Things/HAT2.0?branch=master)-->

# [Hub of All Things](https://hubofallthings.com)

This repository contains an implementation of the [Hub-of-All-Things](http://hubofallthings.com) project Personal Data Store.

## Releases

The current project version is 2.5.1: [HAT 2.5.1](https://github.com/Hub-of-all-Things/HAT2.0/releases/tag/v2.5.1).

## About the project

Hub-of-All-Things is a platform for a Multi-Sided Market powered by the Internet of Things.

A Personal Data Management System (“the HAT”) is a personal single tenant (“the individual self”) technology system that is fully individual self-service, to enable an individual to define a full set of “meta-data” defining as a specific set of personal data, personal preferences, personal behaviour events. The HAT allows individuals to share the right information (quality and quantity), with the right people, in the right situations for the right purposes and gain the benefits.

[![PhD Comics explains the HAT](http://img.youtube.com/vi/y1txYjoSQQc/0.jpg)](http://www.youtube.com/watch?v=y1txYjoSQQc)

## Technology stack

This HAT PDS implementation is written in Scala (2.11.8) uses the following technology stack:

- PostgreSQL relational database (version 9.5)
- Play Framework (version 2.6.6)
- Akka (version 2.5.4)
- Slick as the database access layer (version 3.2.1)

## Running the project


### HAT Setup

HAT runs as a combination of a backing PostgreSQL database (with a 
[public schema](https://github.com/Hub-of-all-Things/hat-database-schema) 
for flattened data storage) and a software stack that provides logic to
work with the schema using HTTP APIs.

To run it from source in a development environment two sets of tools are required:

- PostgreSQL database and utilities
- Scala Build Tool (SBT)

To launch the HAT, follow these steps:

1. Create the database:
    ```bash
    createdb testhatdb1
    createuser testhatdb1
    psql postgres -c "GRANT CREATE ON DATABASE testhatdb1 TO testhatdb1"
    ```
2. Compile the project:
    ```bash
    sbt compile
    ```
3. Add custom local domain mapping to your `/etc/hosts` file. This will make sure when you go to the defined address from your machine you will be pointed back to your own machine. E.g.:
    ```
    127.0.0.1   bobtheplumber.hat.org
    ```
4. Run the project:
    ```bash
    sbt "project hat" "run -Dconfig.resource=dev.conf"
    ```
5. Go to http://bobtheplumber.hat.org:9000

**You're all set!**

### Customising development environment

Your best source of information on how the development environment could
be customised is the `hat/conf/dev.conf` configuration file. Make sure you
run the project locally with the configuration enabled (using the steps above)
or it will just show you the message that the HAT could not be found.

Among other things, the configuration includes:

- host names alongside port numbers of the test HATs (bobtheplumber.hat.org:9000)
- access credentials used to log in as the owner or restricted platform user into the HAT (the default password is a very unsafe *testing*)
- database connection details (important if you want to change your database setup above)
- private and public keys used for token signing and verification  

Specifically, it has 4 major sections:

- Enables the development environment self-initialisation module:
    ```
    play.modules {
      enabled += "org.hatdex.hat.modules.DevHatInitializationModule"
    }
    ```
- Sets the list of database evolutions to be executed on initialisation:
    ```
    devhatMigrations = [
      "evolutions/hat-database-schema/11_hat.sql",
      "evolutions/hat-database-schema/12_hatEvolutions.sql",
      "evolutions/hat-database-schema/13_liveEvolutions.sql",
      "evolutions/hat-database-schema/14_newHat.sql"]
    ```  
- `devhats` list sets out the list of HATs that are served by the current server, for 
each including owner details to be initialised with and database access
credentials. Each specified database must exist before launching the server
but are initialised with the right schema at start time
- `hat` section lists all corresponding HAT configurations to serve, here
you could change the HAT domain name, owner's email address or public/private
keypair used by the HAT for its token operations

## Deployment

HAT is intended to be run inside Docker containers. To build a new container, execute:

```bash
export REPOSITORY_NAME="hubofallthings"
./deployment/ecs/docker-aws-deploy.sh
```

The script is a thin wrapper around a few basic Docker commands. It allows for changing
the name of the repository you  want to deploy the container to and tags it with the git
commit fingerprint for the current code version. Otherwise, it could be simplified to:

```bash
# Scala Build Tool to compile the code and prepare Dockerfile
sbt "project hat" docker:stage

# Build the Docker container
docker build -t hubofallthings/hat hat/target/docker/stage
```

Uploading the container to a Docker repository is left out

## Additional information

- API documentation can be found at the [developers' portal](https://developers.hubofallthings.com))
- [HAT Database Schema](https://github.com/Hub-of-all-Things/hat-database-schema) has been split up into a separate project for easier reuse across different environments.

## License

[HAT including HAT Schema and API] is licensed under [AGPL - GNU AFFERO GENERAL PUBLIC LICENSE](https://github.com/Hub-of-all-Things/HAT/blob/master/LICENSE/AGPL)
