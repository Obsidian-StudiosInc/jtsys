# jtsys
[![License](http://img.shields.io/badge/license-GPLv3-9977bb.svg?style=plastic)](https://github.com/Obsidian-StudiosInc/jtsys/blob/master/LICENSE)
[![Build Status](https://img.shields.io/travis/Obsidian-StudiosInc/jtsys/master.svg?colorA=9977bb&style=plastic)](https://travis-ci.org/Obsidian-StudiosInc/jtsys)
[![Build Status](https://img.shields.io/shippable/58b6492eddd8e80700462c3b/master.svg?colorA=9977bb&style=plastic)](https://app.shippable.com/projects/58b6492eddd8e80700462c3b/)
[![Code Quality](https://img.shields.io/coverity/scan/12327.svg?colorA=9977bb&style=plastic)](https://scan.coverity.com/projects/obsidian-studiosinc-jtsys)
[![Code Quality](https://sonarcloud.io/api/project_badges/measure?project=jtsys&metric=alert_status)](https://sonarcloud.io/dashboard?id=jtsys)

A 100% pure java library interface to TSYS merchant processor for 
processing and settling credit card transations. Presently only 
supporting Sierra platform using Visa "K" 1080/1081 formats. The library 
does not have any external depdencies.

## Usage
Instructions on usage will come. The basics, build using gradle or 
javac. To use call auth or settle after auth. Then parse the returned 
LinkedHashMap.

Requires a valid TSYS merchant account for the Sierra (aka Vital) platform.
Visit [TSYS Merchant Solutions](http://tsysmerchantsolutions.com/) for 
more information on TSYS Merchant Solutions.

## PA-DSS Validation
This libary is not PA-DSS validated. That is a long term goal if possible.

## Legal

Total System Services, Inc.®, TSYS®, and VirtualNet® are federally 
registered service marks of Total System Services, Inc. in the United 
States. Total System Services, Inc. and its affiliates own a number of 
service marks that are registered in the United States and in other 
countries. All other products and company names are trademarks of their 
respective companies.

Usage is subject to TSYS merchant terms and conditions.
