#!/bin/bash

# build downloader
javac -d bin -cp bin src/model/Slug.java
javac -d bin -cp bin src/model/SlugList.java
javac -d bin -cp bin:lib/gson-2.12.1.jar src/downloader/Downloader.java
jar cvfe target/downloader.jar Downloader -C bin .