#!/bin/bash

cat colors.csv | while read line; 
    do 
        SHADE=$(echo $line | cut -d ',' -f 1) 
        NAME=$(echo $line | cut -d ',' -f 2) 
        HEX=$(echo $line | cut -d ',' -f 3) 
        R=$(echo ${HEX:1:2})
        G=$(echo ${HEX:3:2})
        B=$(echo ${HEX:5:2})
        echo "mColorList.add(new ColorName(\"$SHADE\", \"$NAME\", 0x$R, 0x$G, 0x$B));"
    done
