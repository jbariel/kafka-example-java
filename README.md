# Simple Kafka example - in Java
Kafka producer/consumer example in Java

## Build
`mvn` => builds the jar files

`docker-compose build` => builds the docker config

## Run
`docker-compose up` => starts the producer/consumer 

(**_ASSUMES CONNECTIVITY AVAILABLE TO [`dockerized-kafka`](https://github.com/jbariel/dockerized-kafka)_**)

# Related
This can be run with the [`dockerized-kafka`](https://github.com/jbariel/dockerized-kafka) repo.  If not using this repo, you probably need to update the `docker-compose.yml` file.

# License
This is licensed under the Apache-2.0