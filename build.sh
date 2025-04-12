#!/bin/bash

# build downloader
javac -d bin -cp bin src/model/Slug.java
javac -d bin -cp bin src/model/SlugList.java
javac -d bin -cp bin:lib/postgresql-42.7.5.jar src/db/SlugsDB.java
javac -d bin -cp bin:lib/gson-2.12.1.jar src/downloader/Downloader.java
#jar cvfe target/downloader.jar Downloader -C bin .
jar cvfm target/downloader.jar src/META-INF/MANIFEST.MF -C bin .
cp lib/gson-2.12.1.jar target/gson-2.12.1.jar
cp lib/postgresql-42.7.5.jar target/postgresql-42.7.5.jar
