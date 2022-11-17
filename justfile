set dotenv-load

# just displays the receipes
default:
    @just --list

# builds the project
build:
    sbt compile

# cleans the project
clean:
	sbt clean

# runs linter on the project (no files are changed)
lint:
	sbt scalafmtCheckAll

# runs linter and fix (files are changed!)
fix:
	sbt scalafmt scalafixAll

# runs the application
run:
	sbt "project app" run
