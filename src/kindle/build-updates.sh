#!/bin/bash -e -x
#
# $Id$
#

HACKNAME="${1}Booklet"
PKGNAME="${HACKNAME}"
PKGVER="${2%%-g*}"

# Setup KindleTool packaging metadata flags to avoid cluttering the invocations
KT_PM_FLAGS=( "-xPackageName=${PKGNAME}" "-xPackageVersion=${1}" "-xPackageAuthor=ixtab, NiLuJe, twobob, stepk, coplate" "-xPackageMaintainer=coplate, NiLuJe" "-X" )

# We need kindletool (https://github.com/NiLuJe/KindleTool) in $PATH
if (( $(kindletool version | wc -l) == 1 )) ; then
	HAS_KINDLETOOL="true"
fi

if [[ "${HAS_KINDLETOOL}" != "true" ]] ; then
	echo "You need KindleTool (https://github.com/NiLuJe/KindleTool) to build this package."
	exit 1
fi


# We'll want to pull libotautils5 from my SVN tree (to avoid code duplication)
curl http://svn.ak-team.com/svn/Configs/trunk/Kindle/Touch_Hacks/Common/lib/libotautils5 > ../booklet/install/libotautils5
curl http://svn.ak-team.com/svn/Configs/trunk/Kindle/Touch_Hacks/Common/lib/libotautils5 > ../booklet/uninstall/libotautils5

# Also, we kind of need the Booklet itself ;).
cp -f ../../../../../../../${PKGNAME}.jar ../booklet/install/${PKGNAME}.jar

echo "Doing Packaging"

# Install (>= 5.1.2)
kindletool create ota2 "${KT_PM_FLAGS[@]}" -d kindle5 -s 1679530004 -C ../booklet/install Update_${PKGNAME}_${PKGVER}_install.bin
# Hotfix (>= 5.12.2)
kindletool create ota2 -d paperwhite2 -d basic -d voyage -d paperwhite3 -d oasis -d basic2 -d oasis2 -d paperwhite4 -d basic3 -d oasis3 -O -s 3556150002 -C ../booklet/install Update_${PKGNAME}_hotfix_${PKGVER}_install.bin
# Uninstall
kindletool create ota2 "${KT_PM_FLAGS[@]}" -d kindle5 -C ../booklet/uninstall Update_${PKGNAME}_${PKGVER}_uninstall.bin

echo "Done packaging"
ls -l
pwd
# Move our updates
mkdir -p ../dist
rm -f ../dist/*.bin

mv -f *.bin ../dist/
echo "Cleanup"
# Cleanup behind us
rm -f ../booklet/install/libotautils5  ../booklet/uninstall/libotautils5 ../booklet/install/${PKGNAME}.jar
