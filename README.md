This is an LWM2M IoTAgent, part of the [dojot project](http://dojotdocs.readthedocs.io).
It was written in java and uses [Eclipse Leshan](https://www.eclipse.org/leshan/) as base for LWM2M handling.

The purpose of this module is to translate device configurations sent to dojot via device-manager
into LWM2M configuration and commands.

# OMA-LWM2M vs and Device Manager Model:

- Explain differences
- Minimal LWM2M requirements
- OMA discovery
- oma2devicemanager.py guide

# Running

The IoTAgent is a highly connected component inside the dojot ecosystem, 
proper usage requires the dojot environment up and running.
For an in depth guide of running dojot check out this [link](http://dojotdocs.readthedocs.io/en/stable/user_guide.html)
For a quick setup run the provided docker-compose, it should already contain
this service running:

    # Clone and bring up the dojot infrastructure with docker compose
    # Be aware this may take a few minutes (or hours depending on your connection)
    git clone git@github.com:dojot/docker-compose.git
    cd docker-compose
    git checkout X.X.X
    docker-compose up -d

We also provide a full example to run it yourself [here](./docs/running.md).

Also check out some use cases [here](./docs/usage.md)


   