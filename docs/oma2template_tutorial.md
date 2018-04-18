# HOW-TO: Converting OMA to Dojot Models

We provide a best effort [script](../client/oma2template.py) to convert an OMA 
Object Model(xml) to a Dojot Template(json). As explained [previously](modeling.md),
there is no direct conversion between Dojot and OMA models, so some 
post-script manual editing is needed




# Running

    Usage: oma2template.py [options]
    
    Options:
      -h, --help            show this help message and exit
      -i FILE, --input=FILE
                            input xml file
      -o FILE, --output=FILE
                            output json file

# Post-Script Editing

After generating the output file, perform these steps:

- Edit "type": "static" to "type": "dynamic" where needed
- Edit "type": "actuator" to "type": "dynamic" for RW attributes where write is not needed



# Example
    
    cd client
    python3 oma2template.py -i example/3311.xml -o example/3311.json
    # You can use your preference difftool instead of meld
    meld example/3311.json example/3311_postedit.json
    