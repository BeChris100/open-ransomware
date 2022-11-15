# Troubleshooting

## The Target System does not contain in the Support List
This means that in the "inject.json" file (located in src/PackageName/res/)
does not match one of the items on the "Supports" list. To support every
platform, use the * character.

## No Locations could be specified
This occurs, when the System could not determine its Operating System and did not
find the specific locations for your system. If the case is that you are using
FreeBSD (officially not supported), you need to specify custom locations for the
folders.

## The configuration message file was not found in the JAR
Check spelling and the location to the file from the Package ID
(com.bechris100.open_ransomware) root.

## Could not detect billing type
At this moment, it only supports Bitcoin and PayPal. Case sensitivity is ignored,
but providing something else like Bank is not supported.