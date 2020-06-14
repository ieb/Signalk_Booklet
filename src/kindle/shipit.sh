#! /bin/sh -x
#
# Package everything for release :).
#
##

# Check args
if (( $# < 2 )) ; then
	echo "Not enough args!"
	exit 1
fi

PACKAGE_NAME="${1}"
PACKAGE_VERSION="${2}"
PACKAGE_DATE="${3}"

echo "Packaging ${PACKAGE_NAME} ${PACKAGE_VERSION} (${PACKAGE_DATE}) . . ."

# Handle being called from a different directory (ie. by ant)...
WD="${0%/*}"
cd "${WD}"

# Clean dist directory...
rm -f ../../../../../../../dist/*.tar.xz

# Build the Booklet update package
./build-updates.sh "${PACKAGE_NAME}" "${PACKAGE_VERSION}"

# Make Windows users happy...
unix2dos -k ../dist/*.txt ../../../../../../../*.txt

# And package it (flatten the directory structure)
gtar --exclude='MR_THREAD.txt' --transform 's,^.*/,,S' --show-transformed-names -cvJf ../../../../../../../dist/${PACKAGE-NAME}-${PACKAGE_VERSION}-${PACKAGE_DATE}.tar.xz ../dist/* ../../../../../../../*.txt

# Git handles this properly, but it shouts at us a bit...
dos2unix -k ../dist/*.txt ../../../../../../../*.txt

# Cleanup behind build-updates
rm -f ../dist/*.bin

# Go back
cd - &>/dev/null
