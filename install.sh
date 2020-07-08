#!/bin/bash -e
mvn clean install
scp target/Update_Signalk_uk.co.tfd.kindle.signalk_*_install.bin root@192.168.15.244:/mnt/us/mrpackages
ssh root@192.168.15.244 /mnt/us/extensions/MRInstaller/bin/mrinstaller.sh launch_installer
