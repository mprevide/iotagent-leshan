This is an LWM2M IoTAgent, part of the [dojot project](http://dojotdocs.readthedocs.io).
It was written in java and uses [Eclipse Leshan](https://www.eclipse.org/leshan/) as base for LWM2M handling.

The purpose of this module is to translate device configurations sent to dojot via device-manager
into LWM2M configuration and commands.

# OMA-LWM2M vs and Device Manager Model:

Dojot and LwM2M have different device and connection models. 
For starters, while dojot's device registration is a role of the user,
in LwM2M the connection is initiated by the device.

To accomodate these diferences a minimum configuration is expected of each device.
It should provide resources 3/0/1 and 3/0/2, and use the following [template](client/lwm2m_base.json):

A fuller explanation is available at the [modeling](./docs/modeling.md) docs.

# Running

The IoTAgent is a highly connected component inside the dojot ecosystem, 
proper usage requires the dojot environment up and running.
For an in depth guide of running dojot check out this [link](http://dojotdocs.readthedocs.io/en/stable/user_guide.html)
For a quick setup run the provided docker-compose, starting o version X.X.X
it should already contain this service:

    # Clone and bring up the dojot infrastructure with docker compose
    # Be aware this may take a few minutes (or hours depending on your connection)
    git clone git@github.com:dojot/docker-compose.git
    cd docker-compose
    git checkout X.X.X
    docker-compose up -d

We also provide a full example to run it yourself [here](./docs/running.md).

Also check out some use cases [here](./docs/usage.md)


   