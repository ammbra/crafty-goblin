# DevSpace vs Skaffold demo code

This repository contains the application code and setup used to inspect DevSpace vs Skaffold capabilities for iterative development.

## Working with DevSpace

Project [task-buddy](https://github.com/ammbra/crafty-goblin/tree/main/task-buddy) contains the application on which DevSpace was demonstrated.

### Generate the k8s manifests

The project contains the spring dekorate.io dependency:

```xml
       <dependency>
            <groupId>io.dekorate</groupId>
            <artifactId>kubernetes-spring-starter</artifactId>
            <version>3.7.0</version>
        </dependency>
```

and the following configuration in _aplication.properties_:

```text
dekorate.docker.registry=ghcr.io
dekorate.docker.group=ammbra
dekorate.docker.name=task-buddy
dekorate.docker.version=1.0
dekorate.docker.auto-push-enabled=false # disable image push
dekorate.docker.auto-build-enabled=false # disable image build
dekorate.output-path=../../k8s  # overwrite default path where the kubernetes manifests are generated
```

You can also generate Kubernetes manifests inside _k8s_ folder (or any other folder of your liking) by running:

`mvn verify -Ddekorate.build=true --Ddekorate.output-path=../../k8s".

By default Dekorate generates the kubernetes manifests under _target/classes_, so `-Ddekorate.output-path` instructs Dekorate to create them in a directory relative to that path.

### Start the dev mode

Run the following command to build a container image for your local development:

`devspace build -t 1.0.0 --var=TARGET=build` or its equivalent with `devspace build-dev`

Then just execute in a terminal `devspace dev`.

Next, you can execute `devspace ui` to see the commands that you can run in this project.


### Deploy and rollback  an application

You can build a production image by running: 

`devspace build-prod`

Deploy the application using: 

`devspace deploy`

In case of errors, run `devspace run-pipeline rollback`.

## Working with Skaffold

Project [task-pal](https://github.com/ammbra/crafty-goblin/tree/main/task-pal) contains the application on which Skaffold was demonstrated.


### Generate the k8s manifests

The project contains the spring dekorate.io dependency:

```xml
       <dependency>
            <groupId>io.dekorate</groupId>
            <artifactId>kubernetes-spring-starter</artifactId>
            <version>3.7.0</version>
        </dependency>
```

and the following configuration in _aplication.properties_:

```text
dekorate.docker.registry=ghcr.io
dekorate.docker.group=ammbra
dekorate.docker.name=task-pal
dekorate.docker.version=1.0
dekorate.docker.auto-push-enabled=false # disable image push
dekorate.docker.auto-build-enabled=false # disable image build
dekorate.output-path=../../k8s  # overwrite default path where the kubernetes manifests are generated
```

You can also generate Kubernetes manifests inside _k8s_ folder (or any other folder of your liking) by running:

`mvn verify -Ddekorate.build=true --Ddekorate.output-path=../../k8s".

By default Dekorate generates the kubernetes manifests under _target/classes_, so `-Ddekorate.output-path` instructs Dekorate to create them in a directory relative to that path.

### Start the dev mode

Run the following command to start development mode:

`skaffold dev --default-repo=localhost:5000 -p dev --port-forward`. 


### Deploy an application

You can build a production image by running: 

`skaffold deploy -p prod -t 1.0`

Deploy the application using: 

In case of errors, run `skaffold debug` and attach a remote debugger to the application.


