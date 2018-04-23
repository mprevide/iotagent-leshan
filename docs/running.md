# Full Example

This example will bring up the dojot environment, 
build and run a docker image containing the iotagent,
build a Zephyr RTOS example mocking a LWM2M device and interact with this device.

## Dependencies

    sudo apt-get install libpcap-dev socat libssl-dev

Be sure to have the necessary environment with docker, docker-compose, zephyr, zephyr_sdk, and net-tools

- https://docs.docker.com/install/
- https://docs.docker.com/compose/install/
- http://docs.zephyrproject.org/getting_started/installation_linux.html
- https://github.com/zephyrproject-rtos/net-tools

## Deploy

Bring up the dojot environment:

    # On Terminal #1 
    # Clone and bring up the dojot infrastructure with docker compose
    # Be aware this may take a few minutes (or hours depending on your connection)
    git clone git@github.com:dojot/docker-compose.git
    cd docker-compose
    git checkout 0.2.0
    docker-compose up -d

Build the iotagent and populate the database
    
    # On Terminal #2
    git clone --recursive https://github.com/jsiloto/iotagent-leshan
    # if you forgot to clone recursively run:
    # git submodule update --init
    cd iotagent
    
    # Run Database Fixture for this example
    cd client
    pip3 install --user -r requirements.txt
    python3 db_fixture.py
    
    # Build and Run IoTAgent
    cd iotagent-leshan
    docker build -f Dockerfile -t local/iotagent-leshan .
    docker run --rm -it --network docker-compose_default -p 5683:5683/udp -p 5693:5693/udp local/iotagent-leshan

Set up a SLIP interface using net-tools

    # On Terminal #3
    cd net-tools
    ./loop-socat.sh
    
    # On Terminal #4
    cd net-tools
    ./loop-slip-tap.sh
    
Clone an alternative zephyr repo containing the working example:

    # On Terminal #5
    # Get repo version with working example
    cd zephyr
    git remote add jsiloto git@github.com:jsiloto/zephyr.git
    git fetch jsiloto
    git checkout jsiloto/master
    cd samples/net/lwm2m_client/
    mkdir -p build
    cd build
    cmake -DBOARD=qemu_cortex_m3 ..
    make run -j4

# Run

Your example is ready to be used:
- Access dojot's front-end [http://localhost:8000](http://localhost:8000).
- Login with admin/admin. 
- There should be one device ready and publishing dynamic data
- Check out our use [examples](./doc/usage.md)
