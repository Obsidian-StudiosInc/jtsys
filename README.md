# jtsys
[![License](http://img.shields.io/badge/license-GPLv3-9977bb.svg?style=plastic)](https://github.com/Obsidian-StudiosInc/jem/blob/master/LICENSE)
[![Build Status](https://img.shields.io/travis/Obsidian-StudiosInc/jtsys/master.svg?colorA=9977bb&style=plastic)](https://travis-ci.org/Obsidian-StudiosInc/jtsys)
[![Build Status](https://img.shields.io/shippable/58b6492eddd8e80700462c3b/master.svg?colorA=9977bb&style=plastic)](https://app.shippable.com/projects/58b6492eddd8e80700462c3b/)

A java library interface to Tsys merchant processor for processing and 
settling credit card transations. Presently only supporting Sierra 
platform using Visa "K" 1080/1081 formats.

## Usage
Instructions on usage will come. This is still in early development, so 
usage could change drastically. Thus no tags or releases yet. The 
basics, build using gradle or javac. To use call auth or settle after 
auth. Then  parse the returned LinkedHashMap.

Requires a valid Tsys merchant account for the Sierra (aka Vital) platform.

## PCI-DSS Certification
This libary is not PCI-DSS certified. That is a long term goal if possible.

## Legal

Total System Services, Inc.®, TSYS®, and VirtualNet® are federally 
registered service marks of Total System Services, Inc. in the United 
States. Total System Services, Inc. and its affiliates own a number of 
service marks that are registered in the United States and in other 
countries. All other products and company names are trademarks of their 
respective companies.

Usage is subject to Tsys merchant terms and conditions.

