#!/bin/sh
sbt assembly
scp -r -B -C target/scala-2.11/ScalaExtractors-assembly-1.0.jar nutch@amazon:/nutch/search/lib/
