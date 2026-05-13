# ObjCDumper-For-Ghidra
Objective-C Static dumper script for Ghidra that reconstructs class structures, method selectors and metadata from stripped or obfuscated Mach-O binaries and exports findings to TXT reports.

##Features

- Extract Objective-C classes
- Reconstruct method selectors
- Map class to method relationships
- Parse runtime metadata sections
- Dump raw strings from binary segments
- Works on stripped and partially obfuscated binaries
- Export results to TXT file

##Extracted Data

Objective-C runtime:

- Class names
- Methods (selectors)
- Protocols (if present)
- Properties and ivars (partial)

Binary sections:

- __objc_classlist
- __objc_selrefs
- __objc_methname
- __objc_classname
- __objc_const
- __objc_data

Strings:

- C strings from __TEXT
- Strings from __DATA / __const
- CFString entries
- Fragmented/obfuscated strings

Built for ARM64, ARM64e, ARMv8 environments.